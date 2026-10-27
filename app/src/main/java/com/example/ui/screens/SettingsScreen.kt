package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
            text = "Configurez indépendamment votre moteur conversationnel et Agnes AI pour les images",
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
                                .background(NeuraSuccess, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Moteur Principal (Conversation & Textes)",
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
                            text = "${settings.lastPingMs} ms",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeuraNeonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = endpoint,
                    onValueChange = { endpoint = it },
                    label = { Text("Endpoint Conversation", color = NeuraTextMuted) },
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

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = textModel,
                    onValueChange = { textModel = it },
                    label = { Text("Modèle conversation (ex: gemini-2.5-flash)", color = NeuraTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeuraNeonCyan,
                        unfocusedBorderColor = NeuraCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_textmodel")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Ping Button Main API
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF222638))
                        .border(1.dp, NeuraCardBorder, RoundedCornerShape(12.dp))
                        .clickable(enabled = !isTestingConnection) {
                            triggerHaptic(context)
                            viewModel.testConnection()
                        }
                        .padding(vertical = 10.dp)
                        .testTag("test_main_api_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isTestingConnection) "Test conversationnel en cours..." else "Tester la connexion API Conversation",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

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
                            text = "${settings.agnesPingMs} ms",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeuraNeonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Zone dédiée uniquement à la clé Agnes AI pour la génération d'images haute définition, au cas où l'API principale ci-dessus ne prend pas en charge la création visuelle.",
                    fontSize = 12.sp,
                    color = NeuraTextSecondary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = agnesEndpoint,
                    onValueChange = { agnesEndpoint = it },
                    label = { Text("Endpoint Agnes AI Image", color = NeuraTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeuraVibrantViolet,
                        unfocusedBorderColor = Color(0xFF382C52),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_agnes_endpoint")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = if (isAgnesKeyVisible) agnesApiKey else if (agnesApiKey.isEmpty()) "" else settings.getMaskedAgnesApiKey(),
                    onValueChange = { agnesApiKey = it },
                    label = { Text("Clé API Dédiée Agnes AI (agn_...)", color = NeuraTextMuted) },
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

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = agnesModel,
                    onValueChange = { agnesModel = it },
                    label = { Text("Modèle Image Agnes (ex: agnes-flux-pro-v2)", color = NeuraTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeuraVibrantViolet,
                        unfocusedBorderColor = Color(0xFF382C52),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_agnes_model")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Test Agnes AI Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(listOf(NeuraVibrantViolet, NeuraElectricBlue))
                        )
                        .clickable(enabled = !isTestingAgnes) {
                            triggerHaptic(context)
                            viewModel.updateAgnesSettings(agnesEndpoint, agnesApiKey, agnesModel)
                            viewModel.testAgnesConnection()
                        }
                        .padding(vertical = 12.dp)
                        .testTag("test_agnes_api_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = NeuraIcons.Sparkles,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isTestingAgnes) "Vérification Agnes AI en cours..." else "Tester la clé & l'API Agnes AI (Image)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                if (agnesConnectionResult != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    val res = agnesConnectionResult!!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (res.isSuccess) NeuraSuccess.copy(alpha = 0.15f) else NeuraError.copy(alpha = 0.15f),
                                RoundedCornerShape(10.dp)
                            )
                            .border(1.dp, if (res.isSuccess) NeuraSuccess else NeuraError, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = if (res.isSuccess) "✓ ${res.message} (Latence: ${res.latencyMs} ms)" else "⚠ ${res.message}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (res.isSuccess) NeuraSuccess else NeuraError,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ==========================================
        // 3. PROFILE & PREFERENCES
        // ==========================================
        Text(
            text = "Profil Utilisateur",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Nom de l'utilisateur", color = NeuraTextMuted) },
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
                label = { Text("Méthode & style d'apprentissage", color = NeuraTextMuted) },
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

        Spacer(modifier = Modifier.height(24.dp))

        // Global Save Button
        NeuraGradientButton(
            text = "Sauvegarder toutes les clés & configurations",
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
                Toast.makeText(context, "Configurations enregistrées avec succès", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            testTag = "save_all_settings_btn"
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}
