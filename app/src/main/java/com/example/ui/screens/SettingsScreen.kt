package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.components.NeuraGradientButton
import com.example.core.components.triggerHaptic
import com.example.core.icons.NeuraIcons
import com.example.ui.theme.NeuraBackground
import com.example.ui.theme.NeuraCard
import com.example.ui.theme.NeuraCardBorder
import com.example.ui.theme.NeuraElectricBlue
import com.example.ui.theme.NeuraError
import com.example.ui.theme.NeuraNeonCyan
import com.example.ui.theme.NeuraSuccess
import com.example.ui.theme.NeuraTextMuted
import com.example.ui.theme.NeuraTextPrimary
import com.example.ui.theme.NeuraTextSecondary
import com.example.ui.theme.NeuraVibrantViolet
import com.example.ui.viewmodel.NeuraViewModel

@Composable
fun SettingsScreen(
    viewModel: NeuraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings = viewModel.settingsManager

    // Main API States
    var endpoint by remember { mutableStateOf(settings.endpoint) }
    var apiKey by remember { mutableStateOf(settings.apiKey) }
    var isKeyVisible by remember { mutableStateOf(false) }
    var providerName by remember { mutableStateOf(settings.providerName) }
    var textModel by remember { mutableStateOf(settings.textModel) }

    // Dedicated Agnes AI States
    var agnesEndpoint by remember { mutableStateOf(settings.agnesEndpoint) }
    var agnesApiKey by remember { mutableStateOf(settings.agnesApiKey) }
    var isAgnesKeyVisible by remember { mutableStateOf(false) }
    var agnesModel by remember { mutableStateOf(settings.agnesModel) }

    // User Profile
    var userName by remember { mutableStateOf(settings.userName) }
    var learningStyle by remember { mutableStateOf(settings.learningStyle) }

    val connectionTestResult by viewModel.connectionTestResult.collectAsState()
    val isTestingConnection by viewModel.isTestingConnection.collectAsState()

    val agnesConnectionResult by viewModel.agnesConnectionResult.collectAsState()
    val isTestingAgnes by viewModel.isTestingAgnes.collectAsState()

    val availableModels by viewModel.availableModels.collectAsState()
    val isFetchingModels by viewModel.isFetchingModels.collectAsState()
    val modelsFetchError by viewModel.modelsFetchError.collectAsState()

    val availableAgnesModels by viewModel.availableAgnesModels.collectAsState()
    val isFetchingAgnesModels by viewModel.isFetchingAgnesModels.collectAsState()
    val agnesModelsFetchError by viewModel.agnesModelsFetchError.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeuraBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Text(
            text = "Paramètres & Moteurs IA",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Connectez vos clés réelles, explorez les modèles et configurez Agnes AI",
            fontSize = 12.sp,
            color = NeuraTextMuted
        )

        Spacer(modifier = Modifier.height(18.dp))

        // ==========================================
        // 1. MAIN CONVERSATION ENGINE CARD
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF141A28), Color(0xFF0F121C)))
                )
                .border(1.dp, NeuraCardBorder, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .background(if (settings.apiKey.isNotBlank()) NeuraSuccess else NeuraTextMuted, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Moteur Principal (Conversation)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1B263B), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (settings.apiKey.isNotBlank()) "${settings.lastPingMs} ms" else "Non configuré",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (settings.apiKey.isNotBlank()) NeuraNeonCyan else NeuraTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = endpoint,
                    onValueChange = { endpoint = it },
                    label = { Text("Endpoint Conversation", color = NeuraTextMuted) },
                    placeholder = { Text("https://generativelanguage.googleapis.com", color = Color(0xFF555566)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeuraNeonCyan,
                        unfocusedBorderColor = NeuraCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_endpoint")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = if (isKeyVisible) apiKey else if (apiKey.isEmpty()) "" else settings.getMaskedApiKey(),
                    onValueChange = { apiKey = it },
                    label = { Text("Clé API Principale", color = NeuraTextMuted) },
                    placeholder = { Text("Entrez votre clé API réelle...", color = Color(0xFF555566)) },
                    trailingIcon = {
                        Text(
                            text = if (isKeyVisible) "Masquer" else "Afficher",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeuraNeonCyan,
                            modifier = Modifier
                                .clickable { isKeyVisible = !isKeyVisible }
                                .padding(horizontal = 8.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeuraNeonCyan,
                        unfocusedBorderColor = NeuraCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_apikey")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // BUTTON: Fetch Models Live
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(NeuraElectricBlue, NeuraVibrantViolet)))
                            .clickable(enabled = !isFetchingModels) {
                                triggerHaptic(context)
                                viewModel.updateSettings(
                                    endpoint = endpoint,
                                    apiKey = apiKey,
                                    provider = providerName,
                                    textModel = textModel,
                                    imageModel = agnesModel,
                                    visionModel = settings.visionModel,
                                    userName = userName,
                                    learningStyle = learningStyle
                                )
                                viewModel.fetchAvailableModels()
                            }
                            .padding(vertical = 11.dp)
                            .testTag("fetch_models_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isFetchingModels) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = if (isFetchingModels) "Recherche en direct..." else "Récupérer les modèles disponibles",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF222638))
                            .border(1.dp, NeuraCardBorder, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isTestingConnection) {
                                triggerHaptic(context)
                                viewModel.updateSettings(
                                    endpoint = endpoint,
                                    apiKey = apiKey,
                                    provider = providerName,
                                    textModel = textModel,
                                    imageModel = agnesModel,
                                    visionModel = settings.visionModel,
                                    userName = userName,
                                    learningStyle = learningStyle
                                )
                                viewModel.testConnection()
                            }
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isTestingConnection) "..." else "Test ping",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                // Display Discovered Models
                if (modelsFetchError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠ ${modelsFetchError}",
                        fontSize = 11.5.sp,
                        color = NeuraError,
                        lineHeight = 15.sp
                    )
                }

                if (availableModels.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Modèles détectés pour cette clé (${availableModels.size}) — Touchez pour choisir :",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeuraNeonCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableModels.forEach { modelName ->
                            val isChosen = textModel == modelName
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isChosen) NeuraElectricBlue else Color(0xFF1E2232))
                                    .border(1.dp, if (isChosen) NeuraNeonCyan else NeuraCardBorder, RoundedCornerShape(10.dp))
                                    .clickable {
                                        triggerHaptic(context)
                                        textModel = modelName
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = modelName,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = textModel,
                    onValueChange = { textModel = it },
                    label = { Text("Modèle conversation actif", color = NeuraTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeuraNeonCyan,
                        unfocusedBorderColor = NeuraCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_textmodel")
                )

                if (connectionTestResult != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val res = connectionTestResult!!
                    Text(
                        text = if (res.isSuccess) "✓ ${res.message} (${res.latencyMs} ms)" else "⚠ ${res.message}",
                        fontSize = 11.5.sp,
                        color = if (res.isSuccess) NeuraSuccess else NeuraError
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ==========================================
        // 2. DEDICATED AGNES AI IMAGE GENERATION ZONE
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF1D142E), Color(0xFF120C1F)))
                )
                .border(
                    1.dp,
                    Brush.horizontalGradient(listOf(NeuraVibrantViolet, NeuraNeonCyan)),
                    RoundedCornerShape(22.dp)
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(NeuraVibrantViolet.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = NeuraIcons.Image,
                                contentDescription = null,
                                tint = NeuraNeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Zone Réservée : Agnes AI (Images)",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(listOf(NeuraElectricBlue.copy(alpha = 0.4f), NeuraVibrantViolet.copy(alpha = 0.4f))),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (settings.agnesApiKey.isNotBlank()) "${settings.agnesPingMs} ms" else "En attente clé",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (settings.agnesApiKey.isNotBlank()) NeuraNeonCyan else NeuraTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Espace exclusif réservé au moteur d'images Agnes AI si votre API principale ne gère que le texte.",
                    fontSize = 12.sp,
                    color = NeuraTextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = agnesEndpoint,
                    onValueChange = { agnesEndpoint = it },
                    label = { Text("Endpoint Agnes AI Image", color = NeuraTextMuted) },
                    placeholder = { Text("https://apihub.agnes-ai.com/v1", color = Color(0xFF665577)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeuraVibrantViolet,
                        unfocusedBorderColor = Color(0xFF382C52),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_agnes_endpoint")
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💡 Normalisation auto : NEURA cible automatiquement /images/generations pour la création et /models pour lister vos modèles.",
                    fontSize = 11.sp,
                    color = Color(0xFF8877AA),
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = if (isAgnesKeyVisible) agnesApiKey else if (agnesApiKey.isEmpty()) "" else settings.getMaskedAgnesApiKey(),
                    onValueChange = { agnesApiKey = it },
                    label = { Text("Clé API Dédiée Agnes AI (sk-... ou agn_...)", color = NeuraTextMuted) },
                    placeholder = { Text("Entrez votre clé Agnes AI...", color = Color(0xFF665577)) },
                    trailingIcon = {
                        Text(
                            text = if (isAgnesKeyVisible) "Masquer" else "Afficher",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeuraNeonCyan,
                            modifier = Modifier
                                .clickable { isAgnesKeyVisible = !isAgnesKeyVisible }
                                .padding(horizontal = 8.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeuraVibrantViolet,
                        unfocusedBorderColor = Color(0xFF382C52),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_agnes_apikey")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // BUTTONS: Live Models Discovery + Ping for Agnes AI
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(NeuraVibrantViolet, NeuraElectricBlue)))
                            .clickable(enabled = !isFetchingAgnesModels) {
                                triggerHaptic(context)
                                viewModel.updateAgnesSettings(agnesEndpoint, agnesApiKey, agnesModel)
                                viewModel.fetchAgnesModels()
                            }
                            .padding(vertical = 11.dp)
                            .testTag("fetch_agnes_models_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isFetchingAgnesModels) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = if (isFetchingAgnesModels) "Recherche en direct..." else "Récupérer modèles Agnes",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2A1E3E))
                            .border(1.dp, Color(0xFF4C3570), RoundedCornerShape(12.dp))
                            .clickable(enabled = !isTestingAgnes) {
                                triggerHaptic(context)
                                viewModel.updateAgnesSettings(agnesEndpoint, agnesApiKey, agnesModel)
                                viewModel.testAgnesConnection()
                            }
                            .padding(horizontal = 14.dp, vertical = 11.dp)
                            .testTag("test_agnes_api_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isTestingAgnes) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = if (isTestingAgnes) "..." else "Test ping",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }

                // Display Error if any
                if (agnesModelsFetchError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠ ${agnesModelsFetchError}",
                        fontSize = 11.5.sp,
                        color = NeuraError,
                        lineHeight = 15.sp
                    )
                }

                // Display Discovered Agnes Models in Real Time
                if (availableAgnesModels.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Modèles disponibles sur votre compte Agnes (${availableAgnesModels.size}) :",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeuraNeonCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableAgnesModels.forEach { modelName ->
                            val isChosen = agnesModel == modelName
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isChosen) NeuraVibrantViolet else Color(0xFF281C3E))
                                    .border(1.dp, if (isChosen) NeuraNeonCyan else Color(0xFF453066), RoundedCornerShape(10.dp))
                                    .clickable {
                                        triggerHaptic(context)
                                        agnesModel = modelName
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = modelName,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Presets for Agnes AI Model
                Text(
                    text = "Modèles d'image recommandés :",
                    fontSize = 11.sp,
                    color = NeuraTextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("dall-e-3", "flux-pro", "flux-schnell", "stable-diffusion-xl", "agnes-flux-pro-v2").forEach { m ->
                        val isChosen = agnesModel == m
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isChosen) NeuraVibrantViolet else Color(0xFF281C3E))
                                .border(1.dp, if (isChosen) NeuraNeonCyan else Color(0xFF453066), RoundedCornerShape(8.dp))
                            .clickable {
                                triggerHaptic(context)
                                agnesModel = m
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = m,
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = agnesModel,
                    onValueChange = { agnesModel = it },
                    label = { Text("Nom du modèle image actif", color = NeuraTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeuraVibrantViolet,
                        unfocusedBorderColor = Color(0xFF382C52),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_agnes_model")
                )

                if (agnesConnectionResult != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val res = agnesConnectionResult!!
                    Text(
                        text = if (res.isSuccess) "✓ ${res.message} (${res.latencyMs} ms)" else "⚠ ${res.message}",
                        fontSize = 11.5.sp,
                        color = if (res.isSuccess) NeuraSuccess else NeuraError
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Profile & Preferences
        Text(
            text = "Profil Utilisateur",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Votre prénom ou pseudonyme", color = NeuraTextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeuraNeonCyan,
                    unfocusedBorderColor = NeuraCardBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = learningStyle,
                onValueChange = { learningStyle = it },
                label = { Text("Directives d'apprentissage / ton souhaité", color = NeuraTextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeuraNeonCyan,
                    unfocusedBorderColor = NeuraCardBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Save All Button
        NeuraGradientButton(
            text = "Enregistrer toutes les configurations",
            onClick = {
                viewModel.updateSettings(
                    endpoint = endpoint,
                    apiKey = apiKey,
                    provider = providerName,
                    textModel = textModel,
                    imageModel = agnesModel,
                    visionModel = settings.visionModel,
                    userName = userName,
                    learningStyle = learningStyle
                )
                viewModel.updateAgnesSettings(
                    endpoint = agnesEndpoint,
                    apiKey = agnesApiKey,
                    model = agnesModel
                )
                Toast.makeText(context, "Configurations sauvegardées avec succès", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            testTag = "save_all_settings_btn"
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}
