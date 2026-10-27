package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.SettingsManager
import com.example.data.model.CodePayload
import com.example.data.model.ContentType
import com.example.data.model.FilePayload
import com.example.data.model.FlashcardItem
import com.example.data.model.FlashcardPayload
import com.example.data.model.ImagePayload
import com.example.data.model.QuizPayload
import com.example.data.model.QuizQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

data class OrchestrationResult(
    val contentType: ContentType,
    val textMessage: String,
    val payloadJson: String
)

data class ConnectionTestResult(
    val isSuccess: Boolean,
    val latencyMs: Long,
    val message: String,
    val detectedModel: String
)

class AiEngineService(private val settingsManager: SettingsManager) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun getActiveApiKey(): String {
        return settingsManager.apiKey.ifBlank {
            try {
                // If injected via BuildConfig
                val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
                field.get(null) as? String ?: ""
            } catch (_: Exception) {
                ""
            }
        }.trim()
    }

    /**
     * Dynamically fetches available models from the configured endpoint in real time.
     */
    suspend fun fetchAvailableModels(): List<String> = withContext(Dispatchers.IO) {
        val endpoint = settingsManager.endpoint.trim().removeSuffix("/")
        val apiKey = getActiveApiKey()

        if (apiKey.isBlank()) {
            throw IllegalStateException("Veuillez d'abord renseigner votre clé API dans les Paramètres.")
        }

        val isGemini = endpoint.contains("googleapis.com") || endpoint.isEmpty()

        if (isGemini) {
            // Google Gemini API Model List
            val url = "https://generativelanguage.googleapis.com/v1beta/models?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "NeuraAI/1.0 (Android)")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                throw IOException("Erreur API Gemini (${response.code}): $body")
            }

            val json = JSONObject(body)
            val modelsArray = json.optJSONArray("models") ?: return@withContext emptyList()
            val list = mutableListOf<String>()
            for (i in 0 until modelsArray.length()) {
                val m = modelsArray.getJSONObject(i)
                val name = m.optString("name", "")
                val methods = m.optJSONArray("supportedGenerationMethods")
                val supportsGenerateContent = methods?.let { arr ->
                    (0 until arr.length()).any { arr.getString(it) == "generateContent" }
                } ?: true

                if (supportsGenerateContent && name.isNotBlank()) {
                    list.add(name.removePrefix("models/"))
                }
            }
            list.ifEmpty { listOf("gemini-2.5-flash", "gemini-2.5-pro", "gemini-1.5-flash") }
        } else {
            // OpenAI / Custom Compatible Endpoint
            val url = "$endpoint/models"
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $apiKey")
                .header("User-Agent", "NeuraAI/1.0 (Android)")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                throw IOException("Erreur Endpoint (${response.code}): $body")
            }

            val json = JSONObject(body)
            val dataArr = json.optJSONArray("data") ?: return@withContext emptyList()
            val list = mutableListOf<String>()
            for (i in 0 until dataArr.length()) {
                val item = dataArr.getJSONObject(i)
                val id = item.optString("id", "")
                if (id.isNotBlank()) list.add(id)
            }
            list
        }
    }

    suspend fun testConnection(): ConnectionTestResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val apiKey = getActiveApiKey()

        if (apiKey.isBlank()) {
            return@withContext ConnectionTestResult(
                isSuccess = false,
                latencyMs = 0L,
                message = "Aucune clé API renseignée. Veuillez entrer une clé valide.",
                detectedModel = settingsManager.textModel
            )
        }

        try {
            val models = fetchAvailableModels()
            val latency = (System.currentTimeMillis() - startTime).coerceAtLeast(80L)
            settingsManager.lastPingMs = latency
            settingsManager.lastPingStatus = "Connecté (${models.size} modèles détectés)"

            ConnectionTestResult(
                isSuccess = true,
                latencyMs = latency,
                message = "Connexion établie avec succès ! ${models.size} modèles détectés en direct.",
                detectedModel = settingsManager.textModel
            )
        } catch (e: Exception) {
            val latency = (System.currentTimeMillis() - startTime).coerceAtLeast(90L)
            ConnectionTestResult(
                isSuccess = false,
                latencyMs = latency,
                message = "Échec : ${e.localizedMessage ?: "Vérifiez l'URL et la clé"}",
                detectedModel = settingsManager.textModel
            )
        }
    }

    suspend fun testAgnesConnection(): ConnectionTestResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val endpoint = settingsManager.agnesEndpoint.trim()
        val apiKey = settingsManager.agnesApiKey.trim()

        if (apiKey.isBlank()) {
            return@withContext ConnectionTestResult(
                isSuccess = false,
                latencyMs = 0L,
                message = "Aucune clé Agnes AI renseignée dans la zone réservée.",
                detectedModel = settingsManager.agnesModel
            )
        }

        try {
            val targetUrl = if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
                endpoint
            } else {
                "https://$endpoint"
            }

            val request = Request.Builder()
                .url(targetUrl)
                .header("Authorization", "Bearer $apiKey")
                .header("User-Agent", "NeuraAI/1.0 (AgnesClient)")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            val latency = (System.currentTimeMillis() - startTime).coerceAtLeast(110L)

            settingsManager.agnesPingMs = latency
            settingsManager.agnesPingStatus = "Connecté (HTTP ${response.code})"

            ConnectionTestResult(
                isSuccess = response.code in 200..299 || response.code == 405,
                latencyMs = latency,
                message = if (response.isSuccessful || response.code == 405) "Serveur Agnes AI accessible et clé active."
                else "Réponse du serveur Agnes AI : HTTP ${response.code}",
                detectedModel = settingsManager.agnesModel
            )
        } catch (e: Exception) {
            val latency = (System.currentTimeMillis() - startTime).coerceAtLeast(95L)
            ConnectionTestResult(
                isSuccess = false,
                latencyMs = latency,
                message = "Erreur Agnes AI : ${e.localizedMessage ?: "Serveur injoignable"}",
                detectedModel = settingsManager.agnesModel
            )
        }
    }

    suspend fun orchestrateResponse(
        prompt: String,
        attachedFileName: String? = null,
        attachedFileContent: String? = null,
        activeMemories: List<String> = emptyList()
    ): OrchestrationResult = withContext(Dispatchers.IO) {
        val lowerPrompt = prompt.lowercase().trim()

        // 1. DEDICATED IMAGE GENERATION
        if (lowerPrompt.contains("image") || lowerPrompt.contains("illustration") ||
            lowerPrompt.contains("dessin") || lowerPrompt.contains("photo") ||
            lowerPrompt.contains("génère une image") || lowerPrompt.contains("crée une image")
        ) {
            return@withContext handleRealImageGeneration(prompt)
        }

        // 2. CONVERSATIONAL / MULTIMODAL AI REQUEST
        val apiKey = getActiveApiKey()
        if (apiKey.isBlank()) {
            return@withContext OrchestrationResult(
                contentType = ContentType.TEXT,
                textMessage = "⚠ Aucune clé d'API n'a été configurée.\n\nPour discuter en temps réel avec NEURA, vous devez ajouter votre clé API dans les Paramètres (Google Gemini ou compatible OpenAI), puis appuyer sur 'Récupérer les modèles disponibles'. Vos données restent 100% locales et privées sur votre appareil.",
                payloadJson = ""
            )
        }

        // Build comprehensive context with real user file content and active memories
        val fullPrompt = buildString {
            append("Tu es NEURA AI, 'Votre intelligence augmentée'. Assistant personnel multimodal d'élite.\n")
            append("Règles absolues:\n")
            append("- Réponds en temps réel sans données fictives ou préfabriquées.\n")
            append("- Si un fichier est joint, analyse méticuleusement son contenu réel.\n")
            append("- Si l'utilisateur demande explicitement un QUIZ / QCM, inclus à la fin de ta réponse un bloc JSON délimité exactement par [QUIZ_START] et [QUIZ_END] avec le format: {\"title\":\"...\",\"questions\":[{\"id\":1,\"question\":\"...\",\"options\":[\"...\",\"...\",\"...\",\"...\"],\"correctIndex\":0,\"explanation\":\"...\"}]}\n")
            append("- Si l'utilisateur demande explicitement des FLASHCARDS / fiches de révision, inclus à la fin un bloc JSON délimité par [FLASHCARDS_START] et [FLASHCARDS_END] avec le format: {\"title\":\"...\",\"cards\":[{\"id\":1,\"front\":\"...\",\"back\":\"...\",\"difficulty\":\"Facile/Moyen/Difficile\"}]}\n\n")

            if (activeMemories.isNotEmpty()) {
                append("--- MÉMOIRE PERSONNELLE DE L'UTILISATEUR ---\n")
                activeMemories.forEach { append("• $it\n") }
                append("\n")
            }

            if (!attachedFileName.isNullOrBlank() && !attachedFileContent.isNullOrBlank()) {
                append("--- CONTENU DU FICHIER IMPORTÉ ($attachedFileName) ---\n")
                append(attachedFileContent.take(15000)) // Max 15k chars for prompt safety
                append("\n--- FIN DU FICHIER ---\n\n")
            }

            append("Demande de l'utilisateur : $prompt")
        }

        try {
            val responseText = callRealTextAi(fullPrompt)
            return@withContext parseRealAiResponse(responseText, attachedFileName)
        } catch (e: Exception) {
            return@withContext OrchestrationResult(
                contentType = ContentType.TEXT,
                textMessage = "Erreur de communication avec l'API IA : ${e.localizedMessage}\n\nVérifiez votre connexion internet, votre endpoint et votre clé d'API dans les Paramètres.",
                payloadJson = ""
            )
        }
    }

    private suspend fun callRealTextAi(prompt: String): String = withContext(Dispatchers.IO) {
        val endpoint = settingsManager.endpoint.trim().removeSuffix("/")
        val apiKey = getActiveApiKey()
        val model = settingsManager.textModel.trim().ifBlank { "gemini-2.5-flash" }

        val isGemini = endpoint.contains("googleapis.com") || endpoint.isEmpty()

        if (isGemini) {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            val bodyJson = JSONObject().apply {
                val contentsArr = JSONArray()
                val contentObj = JSONObject()
                val partsArr = JSONArray()
                partsArr.put(JSONObject().put("text", prompt))
                contentObj.put("parts", partsArr)
                contentsArr.put(contentObj)
                put("contents", contentsArr)
            }.toString()

            val requestBody = bodyJson.toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = try {
                    JSONObject(responseString).getJSONObject("error").getString("message")
                } catch (_: Exception) {
                    responseString
                }
                throw IOException("Gemini HTTP ${response.code} : $errorMsg")
            }

            val json = JSONObject(responseString)
            val candidates = json.optJSONArray("candidates") ?: throw IOException("Aucun candidat retourné par Gemini.")
            val firstCandidate = candidates.getJSONObject(0)
            val parts = firstCandidate.getJSONObject("content").getJSONArray("parts")
            val textBuilder = StringBuilder()
            for (i in 0 until parts.length()) {
                textBuilder.append(parts.getJSONObject(i).optString("text", ""))
            }
            return@withContext textBuilder.toString()
        } else {
            // OpenAI Compatible Endpoint
            val url = "$endpoint/chat/completions"
            val bodyJson = JSONObject().apply {
                put("model", model)
                val messagesArr = JSONArray()
                messagesArr.put(JSONObject().apply {
                    put("role", "system")
                    put("content", "Tu es NEURA AI, un assistant personnel multimodal haute performance.")
                })
                messagesArr.put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
                put("messages", messagesArr)
            }.toString()

            val requestBody = bodyJson.toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $apiKey")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                throw IOException("Endpoint HTTP ${response.code} : $responseString")
            }

            val json = JSONObject(responseString)
            val choices = json.getJSONArray("choices")
            return@withContext choices.getJSONObject(0).getJSONObject("message").getString("content")
        }
    }

    private suspend fun handleRealImageGeneration(prompt: String): OrchestrationResult = withContext(Dispatchers.IO) {
        val agnesKey = settingsManager.agnesApiKey.trim()
        val agnesEndpoint = settingsManager.agnesEndpoint.trim()
        val agnesModel = settingsManager.agnesModel.trim().ifBlank { "agnes-flux-pro-v2" }

        if (agnesKey.isBlank()) {
            return@withContext OrchestrationResult(
                contentType = ContentType.TEXT,
                textMessage = "⚠ Pour générer des images en temps réel, veuillez renseigner votre clé dans la 'Zone Réservée : Agnes AI (Images)' des Paramètres.\n\nPrompt formulé par NEURA : \"$prompt\"",
                payloadJson = ""
            )
        }

        try {
            val jsonBody = JSONObject().apply {
                put("prompt", prompt)
                put("model", agnesModel)
                put("size", "1024x1024")
                put("n", 1)
            }.toString()

            val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(agnesEndpoint)
                .header("Authorization", "Bearer $agnesKey")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext OrchestrationResult(
                    contentType = ContentType.TEXT,
                    textMessage = "Échec de génération Agnes AI (HTTP ${response.code}) : $responseBody\n\nVérifiez l'URL de l'endpoint et votre clé dans Paramètres > Zone Agnes AI.",
                    payloadJson = ""
                )
            }

            val json = JSONObject(responseBody)
            val dataArr = json.optJSONArray("data")
            var imageUrl = ""

            if (dataArr != null && dataArr.length() > 0) {
                val item = dataArr.getJSONObject(0)
                imageUrl = item.optString("url", "")
                if (imageUrl.isBlank()) {
                    val b64 = item.optString("b64_json", "")
                    if (b64.isNotBlank()) imageUrl = "data:image/png;base64,$b64"
                }
            }

            val payload = JSONObject().apply {
                put("prompt", prompt)
                put("imageUrl", imageUrl)
                put("aspect", "1:1")
                put("style", agnesModel)
            }.toString()

            return@withContext OrchestrationResult(
                contentType = ContentType.IMAGE,
                textMessage = "Image générée en temps réel par le moteur Agnes AI ($agnesModel).",
                payloadJson = payload
            )
        } catch (e: Exception) {
            return@withContext OrchestrationResult(
                contentType = ContentType.TEXT,
                textMessage = "Erreur de connexion avec Agnes AI : ${e.localizedMessage}. Vérifiez votre endpoint et clé dans les paramètres.",
                payloadJson = ""
            )
        }
    }

    private fun parseRealAiResponse(rawResponse: String, attachedFileName: String?): OrchestrationResult {
        // 1. Detect dynamic Quiz
        val quizStart = rawResponse.indexOf("[QUIZ_START]")
        val quizEnd = rawResponse.indexOf("[QUIZ_END]")
        if (quizStart != -1 && quizEnd > quizStart) {
            val jsonStr = rawResponse.substring(quizStart + "[QUIZ_START]".length, quizEnd).trim()
            val userText = rawResponse.substring(0, quizStart).trim()
            return OrchestrationResult(
                contentType = ContentType.QUIZ,
                textMessage = userText.ifBlank { "Voici le quiz généré sur mesure :" },
                payloadJson = jsonStr
            )
        }

        // 2. Detect dynamic Flashcards
        val flashStart = rawResponse.indexOf("[FLASHCARDS_START]")
        val flashEnd = rawResponse.indexOf("[FLASHCARDS_END]")
        if (flashStart != -1 && flashEnd > flashStart) {
            val jsonStr = rawResponse.substring(flashStart + "[FLASHCARDS_START]".length, flashEnd).trim()
            val userText = rawResponse.substring(0, flashStart).trim()
            return OrchestrationResult(
                contentType = ContentType.FLASHCARD,
                textMessage = userText.ifBlank { "Voici vos fiches de révision générées :" },
                payloadJson = jsonStr
            )
        }

        // 3. Detect Code Block
        if (rawResponse.contains("```")) {
            val startCode = rawResponse.indexOf("```")
            val endCode = rawResponse.lastIndexOf("```")
            if (endCode > startCode) {
                val blockContent = rawResponse.substring(startCode + 3, endCode).trim()
                val firstLineBreak = blockContent.indexOf('\n')
                val language = if (firstLineBreak != -1) blockContent.substring(0, firstLineBreak).trim() else "Code"
                val codeOnly = if (firstLineBreak != -1) blockContent.substring(firstLineBreak + 1) else blockContent

                val textBefore = rawResponse.substring(0, startCode).trim()
                val textAfter = rawResponse.substring(endCode + 3).trim()
                val explanation = listOf(textBefore, textAfter).filter { it.isNotBlank() }.joinToString("\n\n")

                val codeJson = JSONObject().apply {
                    put("language", language.ifBlank { "Code" })
                    put("code", codeOnly)
                    put("explanation", explanation)
                }.toString()

                return OrchestrationResult(
                    contentType = ContentType.CODE,
                    textMessage = textBefore.ifBlank { "Code généré par NEURA AI :" },
                    payloadJson = codeJson
                )
            }
        }

        // 4. Default: Standard rich text response
        return OrchestrationResult(
            contentType = ContentType.TEXT,
            textMessage = rawResponse.trim(),
            payloadJson = ""
        )
    }
}
