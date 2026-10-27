package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.components.NeuraLogo
import com.example.core.components.NeuraTab
import com.example.core.components.NeuralThinkingIndicator
import com.example.core.components.triggerHaptic
import com.example.core.icons.NeuraIcons
import com.example.data.local.entities.MessageEntity
import com.example.data.model.ContentType
import com.example.ui.components.AICodeBlock
import com.example.ui.components.AIFileCard
import com.example.ui.components.AIFlashcard
import com.example.ui.components.AIImageCard
import com.example.ui.components.AIQuizCard
import com.example.ui.components.AITextBlock
import com.example.ui.theme.NeuraAccentGradient
import com.example.ui.theme.NeuraBackground
import com.example.ui.theme.NeuraCard
import com.example.ui.theme.NeuraCardBorder
import com.example.ui.theme.NeuraElectricBlue
import com.example.ui.theme.NeuraNeonCyan
import com.example.ui.theme.NeuraSuccess
import com.example.ui.theme.NeuraTextMuted
import com.example.ui.theme.NeuraTextPrimary
import com.example.ui.theme.NeuraTextSecondary
import com.example.ui.theme.NeuraVibrantViolet
import com.example.ui.viewmodel.NeuraViewModel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale

@Composable
fun ChatScreen(
    viewModel: NeuraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages by viewModel.currentMessages.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val attachedFile by viewModel.attachedFile.collectAsState()
    val userName = viewModel.settingsManager.userName

    val listState = rememberLazyListState()

    // Real Speech-to-Text launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.setInputText(spokenText)
                triggerHaptic(context)
            }
        }
    }

    // Real File Picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val fileName = queryFileName(context, uri) ?: "document_importe.txt"
                val fileContent = readUriContent(context, uri)
                viewModel.attachRealFile(fileName, "DOCUMENT", fileContent)
                triggerHaptic(context)
                Toast.makeText(context, "Fichier chargé : $fileName", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Erreur lecture : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeuraBackground)
            .statusBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NeuraLogo(size = 34.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "NEURA AI",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(NeuraSuccess, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = viewModel.settingsManager.textModel,
                            fontSize = 11.sp,
                            color = NeuraTextMuted
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        triggerHaptic(context)
                        viewModel.startNewConversation()
                    },
                    modifier = Modifier.testTag("new_chat_btn")
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFF1E1E28), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = NeuraIcons.Plus,
                            contentDescription = "Nouvelle session",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                IconButton(
                    onClick = {
                        triggerHaptic(context)
                        viewModel.setTab(NeuraTab.HISTORY)
                    },
                    modifier = Modifier.testTag("history_btn")
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color(0xFF1E1E28), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = NeuraIcons.Sparkles,
                            contentDescription = "Historique",
                            tint = NeuraNeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Messages area or Clean Empty State
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.isEmpty() && !isThinking) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    NeuraLogo(size = 80.dp)

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Bonjour $userName",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Posez une question ou importez vos données en temps réel",
                        fontSize = 13.5.sp,
                        color = NeuraTextSecondary
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Dynamic prompts (no fake pre-baked data)
                    val quickPrompts = listOf(
                        "Explique-moi une notion complexe avec des analogies",
                        "Génère un quiz interactif pour évaluer mes acquis",
                        "Rédige une fonction optimisée avec son architecture",
                        "Génère une illustration via le moteur Agnes AI"
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        quickPrompts.forEach { promptItem ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF14141C))
                                    .border(1.dp, NeuraCardBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                    .clickable {
                                        triggerHaptic(context)
                                        viewModel.sendMessage(promptItem)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = promptItem,
                                        fontSize = 13.sp,
                                        color = NeuraTextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = NeuraIcons.ArrowForward,
                                        contentDescription = null,
                                        tint = NeuraNeonCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        MessageItem(msg)
                    }

                    if (isThinking) {
                        item {
                            NeuralThinkingIndicator(
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Pinned Input Capsule strictly glued above the keyboard without double offset
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // Attached real file badge if active
            if (attachedFile != null) {
                Row(
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1D1D28))
                        .border(1.dp, NeuraNeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = NeuraIcons.FileDocument,
                        contentDescription = null,
                        tint = NeuraNeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = attachedFile!!.first,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = NeuraIcons.Close,
                        contentDescription = "Supprimer",
                        tint = NeuraTextMuted,
                        modifier = Modifier
                            .size(14.dp)
                            .clickable {
                                triggerHaptic(context)
                                viewModel.removeAttachment()
                            }
                    )
                }
            }

            // Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clip(RoundedCornerShape(29.dp))
                    .background(Color(0xFF171720))
                    .border(
                        1.dp,
                        if (inputText.isNotBlank() || attachedFile != null) Brush.horizontalGradient(
                            listOf(NeuraNeonCyan, NeuraVibrantViolet)
                        ) else Brush.horizontalGradient(listOf(NeuraCardBorder, NeuraCardBorder)),
                        RoundedCornerShape(29.dp)
                    )
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Real Attachment Picker button
                IconButton(
                    onClick = {
                        triggerHaptic(context)
                        try {
                            filePickerLauncher.launch("*/*")
                        } catch (e: Exception) {
                            Toast.makeText(context, "Impossible d'ouvrir le sélecteur : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("attachment_btn")
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF22222E), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = NeuraIcons.Paperclip,
                            contentDescription = "Importer un fichier réel",
                            tint = if (attachedFile != null) NeuraNeonCyan else NeuraTextSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Text Field
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (inputText.isEmpty()) {
                        Text(
                            text = "Pose une question à NEURA...",
                            fontSize = 14.sp,
                            color = NeuraTextMuted
                        )
                    }
                    BasicTextField(
                        value = inputText,
                        onValueChange = { viewModel.setInputText(it) },
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = Color.White
                        ),
                        cursorBrush = SolidColor(NeuraNeonCyan),
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chat_input_field")
                    )
                }

                // Real Voice Recognition Button
                IconButton(
                    onClick = {
                        triggerHaptic(context)
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez à NEURA AI...")
                        }
                        try {
                            speechLauncher.launch(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Saisie vocale non disponible sur cet appareil", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("voice_btn")
                ) {
                    Icon(
                        imageVector = NeuraIcons.Mic,
                        contentDescription = "Dictée vocale réelle",
                        tint = NeuraTextSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Send button
                val canSend = inputText.isNotBlank() || attachedFile != null
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (canSend) NeuraAccentGradient else Brush.linearGradient(listOf(Color(0xFF262632), Color(0xFF262632)))
                        )
                        .clickable(enabled = canSend) {
                            triggerHaptic(context)
                            viewModel.sendMessage()
                        }
                        .testTag("send_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = NeuraIcons.Send,
                        contentDescription = "Envoyer",
                        tint = if (canSend) Color.White else NeuraTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private fun queryFileName(context: Context, uri: Uri): String? {
    var name: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) name = it.getString(nameIndex)
        }
    }
    return name ?: uri.lastPathSegment
}

private fun readUriContent(context: Context, uri: Uri): String {
    return context.contentResolver.openInputStream(uri)?.use { stream ->
        BufferedReader(InputStreamReader(stream)).use { it.readText() }
    } ?: ""
}

@Composable
fun MessageItem(message: MessageEntity) {
    if (message.isUser) {
        // User message bubble (right-aligned, gradient)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = 20.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .background(NeuraAccentGradient)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = message.textContent,
                    fontSize = 14.5.sp,
                    color = Color.White,
                    lineHeight = 20.sp
                )
            }
        }
    } else {
        // Assistant Multimodal Block (left-aligned)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            when (message.contentType) {
                ContentType.TEXT.name -> {
                    AITextBlock(text = message.textContent)
                }
                ContentType.IMAGE.name -> {
                    if (message.textContent.isNotBlank()) {
                        AITextBlock(text = message.textContent)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    AIImageCard(payloadJson = message.payloadJson)
                }
                ContentType.QUIZ.name -> {
                    if (message.textContent.isNotBlank()) {
                        AITextBlock(text = message.textContent)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    AIQuizCard(payloadJson = message.payloadJson)
                }
                ContentType.FLASHCARD.name -> {
                    if (message.textContent.isNotBlank()) {
                        AITextBlock(text = message.textContent)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    AIFlashcard(payloadJson = message.payloadJson)
                }
                ContentType.CODE.name -> {
                    if (message.textContent.isNotBlank()) {
                        AITextBlock(text = message.textContent)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    val lang = try {
                        org.json.JSONObject(message.payloadJson).optString("language", "Code")
                    } catch (_: Exception) { "Code" }
                    val code = try {
                        org.json.JSONObject(message.payloadJson).optString("code", "")
                    } catch (_: Exception) { "" }
                    val expl = try {
                        org.json.JSONObject(message.payloadJson).optString("explanation", "")
                    } catch (_: Exception) { "" }

                    AICodeBlock(language = lang, code = code, explanation = expl)
                }
                ContentType.FILE.name -> {
                    if (message.textContent.isNotBlank()) {
                        AITextBlock(text = message.textContent)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    AIFileCard(payloadJson = message.payloadJson)
                }
                else -> {
                    AITextBlock(text = message.textContent)
                }
            }
        }
    }
}
