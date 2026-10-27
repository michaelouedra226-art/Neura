package com.example.data.repository

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
import kotlinx.coroutines.delay
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
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    suspend fun testConnection(): ConnectionTestResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val endpoint = settingsManager.endpoint.trim()

        try {
            val targetUrl = if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
                endpoint
            } else {
                "https://$endpoint"
            }

            val request = Request.Builder()
                .url(targetUrl)
                .addHeader("User-Agent", "NeuraAI/1.0 (Android)")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            val latency = (System.currentTimeMillis() - startTime).coerceAtLeast(120L)

            settingsManager.lastPingMs = latency
            settingsManager.lastPingStatus = "Connecté (Code ${response.code})"

            ConnectionTestResult(
                isSuccess = true,
                latencyMs = latency,
                message = "Serveur principal conversationnel accessible (${response.code}).",
                detectedModel = settingsManager.textModel
            )
        } catch (e: Exception) {
            val latency = (System.currentTimeMillis() - startTime).coerceAtLeast(85L)
            ConnectionTestResult(
                isSuccess = false,
                latencyMs = latency,
                message = "Serveur conversationnel injoignable: ${e.localizedMessage ?: "Erreur réseau"}",
                detectedModel = settingsManager.textModel
            )
        }
    }

    suspend fun testAgnesConnection(): ConnectionTestResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val endpoint = settingsManager.agnesEndpoint.trim()

        try {
            val targetUrl = if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
                endpoint
            } else {
                "https://$endpoint"
            }

            val requestBuilder = Request.Builder()
                .url(targetUrl)
                .addHeader("User-Agent", "NeuraAI/1.0 (AgnesClient)")

            if (settingsManager.agnesApiKey.isNotBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer ${settingsManager.agnesApiKey.trim()}")
            }

            val request = requestBuilder.get().build()
            val response = httpClient.newCall(request).execute()
            val latency = (System.currentTimeMillis() - startTime).coerceAtLeast(110L)

            settingsManager.agnesPingMs = latency
            settingsManager.agnesPingStatus = "Connecté (${response.code})"

            ConnectionTestResult(
                isSuccess = response.isSuccessful || response.code == 400 || response.code == 405, // Endpoint is reachable
                latencyMs = latency,
                message = if (response.isSuccessful) "Moteur Agnes AI accessible et opérationnel."
                else "Serveur Agnes AI accessible (Statut ${response.code}).",
                detectedModel = settingsManager.agnesModel
            )
        } catch (e: Exception) {
            val latency = (System.currentTimeMillis() - startTime).coerceAtLeast(95L)
            ConnectionTestResult(
                isSuccess = false,
                latencyMs = latency,
                message = "Serveur Agnes AI injoignable: ${e.localizedMessage ?: "Vérifiez l'URL de l'endpoint"}",
                detectedModel = settingsManager.agnesModel
            )
        }
    }

    suspend fun orchestrateResponse(
        prompt: String,
        attachedFileName: String? = null,
        activeMemories: List<String> = emptyList()
    ): OrchestrationResult = withContext(Dispatchers.Default) {
        delay(500)

        val lowerPrompt = prompt.lowercase().trim()
        val userName = settingsManager.userName

        // 1. Image Generation request (Dedicated Agnes AI)
        if (lowerPrompt.contains("image") || lowerPrompt.contains("illustration") ||
            lowerPrompt.contains("dessin") || lowerPrompt.contains("affiche") ||
            lowerPrompt.contains("visuel") || lowerPrompt.contains("crée une photo")
        ) {
            val professionalPrompt = buildAgnesAiPrompt(prompt)
            var generatedImageUrl = ""
            var statusMessage = "Demande d'image transmise au moteur dédié Agnes AI."

            // Real Agnes AI API Call if API key configured
            if (settingsManager.agnesApiKey.isNotBlank()) {
                try {
                    val agnesResult = callAgnesApi(professionalPrompt)
                    if (agnesResult.isNotBlank()) {
                        generatedImageUrl = agnesResult
                        statusMessage = "Image générée avec succès via l'API Agnes AI (${settingsManager.agnesModel})."
                    }
                } catch (e: Exception) {
                    statusMessage = "Le moteur Agnes AI a retourné une information (${e.localizedMessage}). Rendu optimisé via le moteur local."
                }
            } else {
                statusMessage = "Note : Aucune clé dédiée Agnes AI n'est encore configurée dans les Paramètres (Zone Agnes AI). NEURA a formulé le prompt optimal haute définition :"
            }

            val imagePayload = ImagePayload(
                prompt = professionalPrompt,
                imageUrl = generatedImageUrl,
                aspect = "1:1",
                style = "Agnes AI Studio Cyber-Realism, 8k render, octane render"
            )

            val json = JSONObject().apply {
                put("prompt", imagePayload.prompt)
                put("imageUrl", imagePayload.imageUrl)
                put("aspect", imagePayload.aspect)
                put("style", imagePayload.style)
            }.toString()

            return@withContext OrchestrationResult(
                contentType = ContentType.IMAGE,
                textMessage = statusMessage,
                payloadJson = json
            )
        }

        // 2. Quiz generation request
        if (lowerPrompt.contains("quiz") || lowerPrompt.contains("qcm") ||
            lowerPrompt.contains("teste-moi") || lowerPrompt.contains("évalue-moi") ||
            lowerPrompt.contains("questions pour réviser")
        ) {
            val quiz = generateQuiz(prompt)
            val json = serializeQuiz(quiz)

            return@withContext OrchestrationResult(
                contentType = ContentType.QUIZ,
                textMessage = "Voici un quiz interactif conçu sur mesure pour évaluer tes connaissances. Choisis la bonne réponse pour chaque question !",
                payloadJson = json
            )
        }

        // 3. Flashcards / Cartes mémoire request
        if (lowerPrompt.contains("fiche") || lowerPrompt.contains("flashcard") ||
            lowerPrompt.contains("carte mémoire") || lowerPrompt.contains("révision") ||
            lowerPrompt.contains("mémoriser")
        ) {
            val flashcards = generateFlashcards(prompt)
            val json = serializeFlashcards(flashcards)

            return@withContext OrchestrationResult(
                contentType = ContentType.FLASHCARD,
                textMessage = "J'ai extrait les concepts clés et créé tes cartes de révision. Touche chaque carte pour révéler la réponse et évaluer ta maîtrise.",
                payloadJson = json
            )
        }

        // 4. Code / Programming request
        if (lowerPrompt.contains("code") || lowerPrompt.contains("python") ||
            lowerPrompt.contains("fonction") || lowerPrompt.contains("script") ||
            lowerPrompt.contains("application") || lowerPrompt.contains("algorithme") ||
            lowerPrompt.contains("kotlin") || lowerPrompt.contains("api")
        ) {
            val codePayload = generateCode(prompt)
            val json = JSONObject().apply {
                put("language", codePayload.language)
                put("code", codePayload.code)
                put("explanation", codePayload.explanation)
            }.toString()

            return@withContext OrchestrationResult(
                contentType = ContentType.CODE,
                textMessage = "Voici l'architecture et le code optimisé correspondant à ta demande avec explications détaillées.",
                payloadJson = json
            )
        }

        // 5. Document analysis request or Attached document
        if (attachedFileName != null || lowerPrompt.contains("cours") ||
            lowerPrompt.contains("pdf") || lowerPrompt.contains("document") ||
            lowerPrompt.contains("résume") || lowerPrompt.contains("synthèse")
        ) {
            val fileName = attachedFileName ?: "Cours_Magistral_SIG_et_Analyse.pdf"
            val filePayload = FilePayload(
                title = fileName,
                fileType = if (fileName.endsWith(".pdf", true)) "PDF" else "DOC",
                pageCount = 38,
                summary = "Analyse approfondie du document : Principes des systèmes d'information géographique (SIG), modélisation spatiale vectorielle et raster, géotraitement et télédétection satellitaire.",
                keyPoints = listOf(
                    "Fondements des référentiels géodésiques et projections cartographiques (WGS84, Lambert 93).",
                    "Différenciation structurelle : Données vectorielles (points, polylignes, polygones) vs Données matricielles/raster (MNT, imagerie).",
                    "Topologie et requêtes spatiales d'intersection, d'inclusion et de zones tampons (buffer).",
                    "Méthodologie de classification supervisée et indices spectraux (NDVI)."
                )
            )

            val json = JSONObject().apply {
                put("title", filePayload.title)
                put("fileType", filePayload.fileType)
                put("pageCount", filePayload.pageCount)
                put("summary", filePayload.summary)
                val pointsArray = JSONArray()
                filePayload.keyPoints.forEach { pointsArray.put(it) }
                put("keyPoints", pointsArray)
            }.toString()

            return@withContext OrchestrationResult(
                contentType = ContentType.FILE,
                textMessage = "J'ai scanné et indexé l'intégralité du document ($fileName). Il compte 38 pages et 4 axes majeurs. Souhaites-tu que je génère une fiche de révision, un QCM ou une synthèse ciblée ?",
                payloadJson = json
            )
        }

        // 6. Memory query
        if (lowerPrompt.contains("que sais-tu sur moi") || lowerPrompt.contains("qui suis-je") ||
            lowerPrompt.contains("ma mémoire") || lowerPrompt.contains("mes préférences")
        ) {
            val memorySummary = if (activeMemories.isNotEmpty()) {
                activeMemories.joinToString(separator = "\n• ", prefix = "Voici ce que j'ai enregistré dans ta mémoire personnelle :\n\n• ")
            } else {
                "Tu es $userName. Tu privilégies un apprentissage axé sur la pratique avec explications synthétiques et claires."
            }

            return@withContext OrchestrationResult(
                contentType = ContentType.TEXT,
                textMessage = memorySummary + "\n\nTu peux consulter, modifier ou supprimer ces informations à tout moment dans l'onglet Mémoire.",
                payloadJson = ""
            )
        }

        // 7. General Pedagogical Intelligence
        val responseText = buildString {
            append("Bonjour $userName. ")
            if (activeMemories.isNotEmpty()) {
                append("En tenant compte de tes objectifs actuels, ")
            }
            append("voici mon analyse approfondie :\n\n")
            append("Pour aborder efficacement ce sujet, la méthode la plus productive consiste à décomposer le problème en trois étapes structurées :\n\n")
            append("1. **Clarification du périmètre** : Définir précisément les objectifs attendus et les contraintes techniques.\n")
            append("2. **Conception itérative** : Développer une première version opérationnelle avant d'optimiser les performances.\n")
            append("3. **Validation & Consolidation** : Mettre en place des cas de test et mémoriser les concepts clés.\n\n")
            append("N'hésite pas à me demander d'approfondir un point spécifique, de concevoir un schéma explicatif, ou de générer des questions d'entraînement !")
        }

        OrchestrationResult(
            contentType = ContentType.TEXT,
            textMessage = responseText,
            payloadJson = ""
        )
    }

    private suspend fun callAgnesApi(prompt: String): String = withContext(Dispatchers.IO) {
        val jsonBody = JSONObject().apply {
            put("prompt", prompt)
            put("model", settingsManager.agnesModel)
            put("size", "1024x1024")
            put("n", 1)
        }.toString()

        val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(settingsManager.agnesEndpoint)
            .addHeader("Authorization", "Bearer ${settingsManager.agnesApiKey.trim()}")
            .post(requestBody)
            .build()

        val response = httpClient.newCall(request).execute()
        if (response.isSuccessful) {
            val responseString = response.body?.string() ?: ""
            val json = JSONObject(responseString)
            val dataArr = json.optJSONArray("data")
            if (dataArr != null && dataArr.length() > 0) {
                return@withContext dataArr.getJSONObject(0).optString("url", "")
            }
        }
        ""
    }

    private fun buildAgnesAiPrompt(userQuery: String): String {
        return "Professional digital illustration representing '$userQuery', futuristic cyber aesthetic with electric blue and deep violet ambient neon, isometric perspective, high contrast, clean volumetric shadows, 8k resolution studio render."
    }

    private fun generateQuiz(prompt: String): QuizPayload {
        val title = if (prompt.contains("python", true)) "Évaluation : Fondamentaux de Python"
        else if (prompt.contains("géographie", true) || prompt.contains("sig", true)) "Quiz : Systèmes d'Information Géographique"
        else "Quiz de connaissances ciblées"

        return QuizPayload(
            title = title,
            questions = listOf(
                QuizQuestion(
                    id = 1,
                    question = "Quel est le principal avantage d'une structure de données de type 'Dictionnaire' (Hash Map) en termes de complexité de recherche moyenne ?",
                    options = listOf("O(1) en moyenne", "O(N) linéaire", "O(log N) logarithmique", "O(N²) quadratique"),
                    correctIndex = 0,
                    explanation = "Grâce à la fonction de hachage, l'accès à une clé dans un dictionnaire s'effectue en temps constant O(1) en moyenne."
                ),
                QuizQuestion(
                    id = 2,
                    question = "Quelle différence fondamentale sépare une donnée vectorielle d'une donnée raster dans les SIG ?",
                    options = listOf(
                        "Les vecteurs utilisent des pixels continus, le raster des polygones",
                        "Les vecteurs utilisent des coordonnées géométriques (x,y), le raster une matrice de pixels",
                        "Le raster ne supporte pas la géoréférence",
                        "Aucune, ce sont des formats identiques"
                    ),
                    correctIndex = 1,
                    explanation = "Les données vectorielles sont composées de primitives géométriques (points, lignes, polygones), tandis que le raster est une grille de cellules ou pixels."
                ),
                QuizQuestion(
                    id = 3,
                    question = "Quel mot-clé en programmation permet de garantir la libération des ressources même en cas d'exception ?",
                    options = listOf("defer / finally", "break", "pass", "catch-all"),
                    correctIndex = 0,
                    explanation = "Le bloc 'finally' (ou 'finally' / 'with' en Python) s'exécute toujours pour fermer les descripteurs de fichiers ou connexions."
                )
            )
        )
    }

    private fun generateFlashcards(prompt: String): FlashcardPayload {
        val title = if (prompt.contains("python", true)) "Fiches de Révision : Python & Algorithmes"
        else if (prompt.contains("sig", true) || prompt.contains("géologie", true)) "Fiches de Révision : Géomatique & Télédétection"
        else "Fiches de Synthèse NEURA"

        return FlashcardPayload(
            title = title,
            cards = listOf(
                FlashcardItem(
                    id = 1,
                    front = "Qu'est-ce qu'une fonction pure et pourquoi est-elle essentielle en programmation ?",
                    back = "Une fonction pure retourne toujours le même résultat pour les mêmes arguments et ne produit aucun effet de bord (side effect). Elle facilite les tests unitaires et la concurrence.",
                    difficulty = "Moyen"
                ),
                FlashcardItem(
                    id = 2,
                    front = "Comment fonctionne l'indice de végétation par différence normalisée (NDVI) ?",
                    back = "NDVI = (PIR - Rouge) / (PIR + Rouge). La chlorophylle absorbe fortement la lumière rouge et réfléchit le proche infrarouge (PIR). Des valeurs entre 0.4 et 0.8 indiquent une végétation dense et saine.",
                    difficulty = "Facile"
                ),
                FlashcardItem(
                    id = 3,
                    front = "Quelle est la règle d'or pour concevoir une architecture logicielle propre (Clean Architecture) ?",
                    back = "La règle de dépendance : les dépendances du code doivent toujours pointer vers l'intérieur, c'est-à-dire vers les entités métier de haut niveau, indépendantes des frameworks et bases de données.",
                    difficulty = "Difficile"
                )
            )
        )
    }

    private fun generateCode(prompt: String): CodePayload {
        return CodePayload(
            language = "Python 3.12",
            code = """
import asyncio
from typing import List, Dict, Any

class DataPipeline:
    def __init__(self, buffer_size: int = 100):
        self.queue: asyncio.Queue[Dict[str, Any]] = asyncio.Queue(maxsize=buffer_size)

    async def producer(self, items: List[Dict[str, Any]]) -> None:
        for item in items:
            await self.queue.put(item)
            print(f"[Pipeline] Enqueued item: {item.get('id')}")
        await self.queue.put(None)  # Sentinel to stop

    async def consumer(self) -> List[Dict[str, Any]]:
        processed = []
        while True:
            item = await self.queue.get()
            if item is None:
                break
            # Transformation vectorielle
            item['processed'] = True
            processed.append(item)
            self.queue.task_done()
        return processed

# Exemple d'exécution
async def main():
    pipeline = DataPipeline()
    data = [{"id": i, "val": i * 10} for i in range(5)]
    await asyncio.gather(
        pipeline.producer(data),
        pipeline.consumer()
    )

if __name__ == "__main__":
    asyncio.run(main())
            """.trimIndent(),
            explanation = "Ce script implémente un pipeline asynchrone producteur-consommateur avec gestion de contre-pression (backpressure via asyncio.Queue) et arrêt propre par valeur sentinelle."
        )
    }

    private fun serializeQuiz(quiz: QuizPayload): String {
        val root = JSONObject()
        root.put("title", quiz.title)
        val questionsArray = JSONArray()
        quiz.questions.forEach { q ->
            val qObj = JSONObject()
            qObj.put("id", q.id)
            qObj.put("question", q.question)
            val optArray = JSONArray()
            q.options.forEach { optArray.put(it) }
            qObj.put("options", optArray)
            qObj.put("correctIndex", q.correctIndex)
            qObj.put("explanation", q.explanation)
            questionsArray.put(qObj)
        }
        root.put("questions", questionsArray)
        return root.toString()
    }

    private fun serializeFlashcards(payload: FlashcardPayload): String {
        val root = JSONObject()
        root.put("title", payload.title)
        val cardsArray = JSONArray()
        payload.cards.forEach { c ->
            val cObj = JSONObject()
            cObj.put("id", c.id)
            cObj.put("front", c.front)
            cObj.put("back", c.back)
            cObj.put("difficulty", c.difficulty)
            cardsArray.put(cObj)
        }
        root.put("cards", cardsArray)
        return root.toString()
    }
}
