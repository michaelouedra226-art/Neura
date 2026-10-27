package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.core.components.triggerHaptic
import com.example.core.icons.NeuraIcons
import com.example.ui.theme.NeuraElectricBlue
import com.example.ui.theme.NeuraNeonCyan
import com.example.ui.theme.NeuraTextMuted
import com.example.ui.theme.NeuraTextSecondary
import com.example.ui.theme.NeuraVibrantViolet
import com.example.ui.viewmodel.NeuraViewModel
import kotlinx.coroutines.launch
import java.util.Locale

enum class ChatGPTVoiceState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    ERROR
}

@Composable
fun ChatGPTVoiceModal(
    viewModel: NeuraViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var voiceState by remember { mutableStateOf(ChatGPTVoiceState.IDLE) }
    var userTranscript by remember { mutableStateOf("") }
    var aiTranscript by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("Initialisation de NEURA Voice...") }
    var audioLevel by remember { mutableFloatStateOf(0f) }
    var isMuted by remember { mutableStateOf(false) }

    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
    var speechRecognizer: SpeechRecognizer? by remember { mutableStateOf(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    val hasAudioPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasAudioPermission.value = granted
        if (!granted) {
            statusMessage = "Autorisation micro requise pour discuter avec NEURA"
            voiceState = ChatGPTVoiceState.ERROR
        }
    }

    fun startListeningInternal() {
        if (!hasAudioPermission.value) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        if (isMuted) return

        try {
            voiceState = ChatGPTVoiceState.LISTENING
            statusMessage = "À votre écoute... (Parlez naturellement)"
            userTranscript = ""
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            speechRecognizer?.startListening(intent)
        } catch (_: Exception) {
            voiceState = ChatGPTVoiceState.ERROR
            statusMessage = "Impossible d'activer le microphone"
        }
    }

    fun stopSpeakingInternal() {
        try {
            textToSpeech?.stop()
        } catch (_: Exception) {}
    }

    fun speakAiResponse(text: String) {
        if (!isTtsReady || text.isBlank()) {
            startListeningInternal()
            return
        }

        voiceState = ChatGPTVoiceState.SPEAKING
        statusMessage = "NEURA vous répond..."
        aiTranscript = text

        val utteranceId = "neura_voice_${System.currentTimeMillis()}"
        val params = Bundle()
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun handleUserSpokenText(spokenText: String) {
        if (spokenText.isBlank()) {
            startListeningInternal()
            return
        }

        userTranscript = spokenText
        voiceState = ChatGPTVoiceState.THINKING
        statusMessage = "NEURA analyse et réfléchit..."

        coroutineScope.launch {
            try {
                val response = viewModel.sendVoiceMessage(spokenText)
                if (response.isNotBlank()) {
                    speakAiResponse(response)
                } else {
                    statusMessage = "Aucune réponse reçue"
                    startListeningInternal()
                }
            } catch (e: Exception) {
                voiceState = ChatGPTVoiceState.ERROR
                statusMessage = "Erreur : ${e.localizedMessage ?: "Problème réseau"}"
            }
        }
    }

    // Initialize TTS and SpeechRecognizer
    DisposableEffect(Unit) {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.let { tts ->
                    val result = tts.setLanguage(Locale.FRENCH)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts.language = Locale.getDefault()
                    }
                    tts.setPitch(1.05f)
                    tts.setSpeechRate(1.0f)
                    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            voiceState = ChatGPTVoiceState.SPEAKING
                        }

                        override fun onDone(utteranceId: String?) {
                            // When NEURA finishes speaking, seamlessly resume listening (ChatGPT loop)
                            coroutineScope.launch {
                                startListeningInternal()
                            }
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            coroutineScope.launch {
                                startListeningInternal()
                            }
                        }
                    })
                    textToSpeech = tts
                    isTtsReady = true
                }
            }
        }

        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                voiceState = ChatGPTVoiceState.LISTENING
                statusMessage = "À votre écoute..."
            }

            override fun onBeginningOfSpeech() {
                statusMessage = "Je vous écoute..."
            }

            override fun onRmsChanged(rmsdB: Float) {
                audioLevel = (rmsdB.coerceIn(0f, 10f) / 10f)
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                voiceState = ChatGPTVoiceState.THINKING
                statusMessage = "Compréhension en cours..."
                audioLevel = 0f
            }

            override fun onError(error: Int) {
                audioLevel = 0f
                if (voiceState == ChatGPTVoiceState.LISTENING) {
                    if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                        // Restart listening automatically
                        startListeningInternal()
                    } else {
                        statusMessage = "Touchez l'orbe pour parler"
                        voiceState = ChatGPTVoiceState.IDLE
                    }
                }
            }

            override fun onResults(results: Bundle?) {
                audioLevel = 0f
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spoken = matches?.firstOrNull()?.trim() ?: ""
                if (spoken.isNotBlank()) {
                    handleUserSpokenText(spoken)
                } else {
                    startListeningInternal()
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partial = matches?.firstOrNull() ?: ""
                if (partial.isNotBlank()) {
                    userTranscript = partial
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer = recognizer

        onDispose {
            try {
                recognizer.destroy()
            } catch (_: Exception) {}
            try {
                ttsInstance.stop()
                ttsInstance.shutdown()
            } catch (_: Exception) {}
        }
    }

    LaunchedEffect(isTtsReady, hasAudioPermission.value) {
        if (!hasAudioPermission.value) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else if (isTtsReady && voiceState == ChatGPTVoiceState.IDLE) {
            startListeningInternal()
        }
    }

    Dialog(
        onDismissRequest = {
            stopSpeakingInternal()
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF09090B))
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header: NEURA AI Voice Mode & Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    when (voiceState) {
                                        ChatGPTVoiceState.LISTENING -> NeuraNeonCyan
                                        ChatGPTVoiceState.THINKING -> NeuraVibrantViolet
                                        ChatGPTVoiceState.SPEAKING -> NeuraElectricBlue
                                        else -> Color.Gray
                                    },
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "NEURA VOICE",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = viewModel.settingsManager.textModel,
                                fontSize = 11.sp,
                                color = NeuraTextMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            triggerHaptic(context)
                            stopSpeakingInternal()
                            onDismiss()
                        },
                        modifier = Modifier.testTag("close_voice_modal")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF1C1C24), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = NeuraIcons.Close,
                                contentDescription = "Fermer le mode vocal",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Center Stage: ChatGPT Organic Pulsing Voice Orb
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            triggerHaptic(context)
                            when (voiceState) {
                                ChatGPTVoiceState.SPEAKING -> {
                                    stopSpeakingInternal()
                                    startListeningInternal()
                                }
                                ChatGPTVoiceState.LISTENING -> {
                                    speechRecognizer?.stopListening()
                                }
                                else -> {
                                    startListeningInternal()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    ChatGPTVoiceOrb(
                        state = voiceState,
                        audioLevel = audioLevel
                    )
                }

                // Subtitles & Live Transcripts
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = statusMessage,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (voiceState) {
                            ChatGPTVoiceState.LISTENING -> NeuraNeonCyan
                            ChatGPTVoiceState.THINKING -> NeuraVibrantViolet
                            ChatGPTVoiceState.SPEAKING -> Color.White
                            ChatGPTVoiceState.ERROR -> Color(0xFFEF4444)
                            else -> NeuraTextSecondary
                        },
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    AnimatedVisibility(
                        visible = userTranscript.isNotBlank() || aiTranscript.isNotBlank(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF14141C))
                                .border(1.dp, Color(0xFF262636), RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                if (userTranscript.isNotBlank()) {
                                    Text(
                                        text = "Vous : \"$userTranscript\"",
                                        fontSize = 13.5.sp,
                                        color = NeuraNeonCyan,
                                        lineHeight = 18.sp
                                    )
                                }
                                if (aiTranscript.isNotBlank()) {
                                    if (userTranscript.isNotBlank()) Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = aiTranscript.take(240) + if (aiTranscript.length > 240) "..." else "",
                                        fontSize = 13.5.sp,
                                        color = Color.White,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom Controls: Mic Mute, Interrupt, Exit
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute / Unmute Button
                    IconButton(
                        onClick = {
                            triggerHaptic(context)
                            isMuted = !isMuted
                            if (isMuted) {
                                speechRecognizer?.stopListening()
                                voiceState = ChatGPTVoiceState.IDLE
                                statusMessage = "Microphone désactivé"
                            } else {
                                startListeningInternal()
                            }
                        },
                        modifier = Modifier.testTag("voice_mute_btn")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(if (isMuted) Color(0xFF3F1B1B) else Color(0xFF1C1C26)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = NeuraIcons.Mic,
                                contentDescription = if (isMuted) "Activer micro" else "Couper micro",
                                tint = if (isMuted) Color(0xFFEF4444) else Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Interrupt / Tap to Speak Primary Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(32.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(NeuraElectricBlue, NeuraVibrantViolet)
                                )
                            )
                            .clickable {
                                triggerHaptic(context)
                                if (voiceState == ChatGPTVoiceState.SPEAKING) {
                                    stopSpeakingInternal()
                                    startListeningInternal()
                                } else {
                                    startListeningInternal()
                                }
                            }
                            .padding(horizontal = 24.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (voiceState == ChatGPTVoiceState.SPEAKING) "Interrompre" else "Parler",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    // Cancel / Stop Button
                    IconButton(
                        onClick = {
                            triggerHaptic(context)
                            stopSpeakingInternal()
                            onDismiss()
                        },
                        modifier = Modifier.testTag("voice_finish_btn")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1C1C26)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = NeuraIcons.Close,
                                contentDescription = "Quitter",
                                tint = NeuraTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatGPTVoiceOrb(
    state: ChatGPTVoiceState,
    audioLevel: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "voice_orb_anim")

    // Continuous organic breathing pulse
    val breathingPulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_pulse"
    )

    // Dynamic rotation angle
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb_rotation"
    )

    // Responsive scaling driven by audio level & state
    val scaleFactor by animateFloatAsState(
        targetValue = when (state) {
            ChatGPTVoiceState.LISTENING -> (1f + audioLevel * 0.45f) * breathingPulse
            ChatGPTVoiceState.THINKING -> 1.15f * breathingPulse
            ChatGPTVoiceState.SPEAKING -> 1.22f * breathingPulse
            else -> 1f
        },
        animationSpec = tween(120),
        label = "audio_scale"
    )

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val baseRadius = (size.minDimension / 3.4f) * scaleFactor

        // Outer Glow Waves
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    when (state) {
                        ChatGPTVoiceState.LISTENING -> NeuraNeonCyan.copy(alpha = 0.35f)
                        ChatGPTVoiceState.THINKING -> NeuraVibrantViolet.copy(alpha = 0.35f)
                        ChatGPTVoiceState.SPEAKING -> NeuraElectricBlue.copy(alpha = 0.45f)
                        else -> Color(0xFF333344).copy(alpha = 0.2f)
                    },
                    Color.Transparent
                ),
                center = center,
                radius = baseRadius * 1.6f
            ),
            center = center,
            radius = baseRadius * 1.6f
        )

        // Concentric Ripple Ring 1
        drawCircle(
            color = when (state) {
                ChatGPTVoiceState.LISTENING -> NeuraNeonCyan.copy(alpha = 0.25f)
                ChatGPTVoiceState.THINKING -> NeuraVibrantViolet.copy(alpha = 0.25f)
                ChatGPTVoiceState.SPEAKING -> NeuraElectricBlue.copy(alpha = 0.3f)
                else -> Color.DarkGray.copy(alpha = 0.15f)
            },
            center = center,
            radius = baseRadius * 1.25f,
            style = Stroke(width = 3.dp.toPx())
        )

        // Concentric Ripple Ring 2
        drawCircle(
            color = when (state) {
                ChatGPTVoiceState.LISTENING -> NeuraVibrantViolet.copy(alpha = 0.35f)
                ChatGPTVoiceState.THINKING -> NeuraNeonCyan.copy(alpha = 0.35f)
                ChatGPTVoiceState.SPEAKING -> NeuraNeonCyan.copy(alpha = 0.45f)
                else -> Color.Gray.copy(alpha = 0.2f)
            },
            center = center,
            radius = baseRadius * 1.1f,
            style = Stroke(width = 2.dp.toPx())
        )

        // Vibrant Inner Core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f),
                    when (state) {
                        ChatGPTVoiceState.LISTENING -> NeuraNeonCyan
                        ChatGPTVoiceState.THINKING -> NeuraVibrantViolet
                        ChatGPTVoiceState.SPEAKING -> NeuraElectricBlue
                        else -> Color(0xFF4A4A5A)
                    },
                    Color(0xFF0D0D14)
                ),
                center = center,
                radius = baseRadius
            ),
            center = center,
            radius = baseRadius
        )
    }
}
