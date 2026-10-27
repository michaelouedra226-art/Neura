package com.example.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.components.NeuraGradientButton
import com.example.core.components.NeuraLogo
import com.example.core.components.triggerHaptic
import com.example.core.icons.NeuraIcons
import com.example.ui.theme.NeuraBackground
import com.example.ui.theme.NeuraCard
import com.example.ui.theme.NeuraCardBorder
import com.example.ui.theme.NeuraElectricBlue
import com.example.ui.theme.NeuraNeonCyan
import com.example.ui.theme.NeuraTextMuted
import com.example.ui.theme.NeuraTextPrimary
import com.example.ui.theme.NeuraTextSecondary
import com.example.ui.theme.NeuraVibrantViolet

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    onSaveFocus: (String) -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(1) }
    var selectedFocus by remember { mutableStateOf("Études & Recherche") }

    val focusOptions = listOf(
        "Études & Recherche",
        "Travail & Entreprise",
        "Création & Design",
        "Programmation & Tech",
        "Organisation personnelle"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeuraBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Step progress dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 12.dp)
        ) {
            for (i in 1..3) {
                Box(
                    modifier = Modifier
                        .width(if (i == currentStep) 24.dp else 8.dp)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (i == currentStep) Brush.horizontalGradient(listOf(NeuraNeonCyan, NeuraVibrantViolet))
                            else Brush.horizontalGradient(listOf(Color(0xFF2E2E38), Color(0xFF2E2E38)))
                        )
                )
            }
        }

        // Screen Body depending on Step
        when (currentStep) {
            1 -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    NeuraLogo(size = 100.dp)

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Bienvenue dans NEURA",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Votre assistant personnel multimodal capable de comprendre, créer, mémoriser et exécuter.",
                        fontSize = 14.5.sp,
                        color = NeuraTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            2 -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Comment veux-tu utiliser NEURA ?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Ces préférences personnalisent la mémoire et le style de réponse.",
                        fontSize = 13.sp,
                        color = NeuraTextMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        focusOptions.forEach { opt ->
                            val isSelected = selectedFocus == opt
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected) Brush.horizontalGradient(
                                            listOf(
                                                NeuraElectricBlue.copy(alpha = 0.25f),
                                                NeuraVibrantViolet.copy(alpha = 0.25f)
                                            )
                                        ) else Brush.horizontalGradient(listOf(NeuraCard, NeuraCard))
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) NeuraNeonCyan else NeuraCardBorder,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        triggerHaptic(context)
                                        selectedFocus = opt
                                    }
                                    .padding(horizontal = 18.dp, vertical = 14.dp)
                                    .testTag("focus_$opt"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else NeuraTextSecondary
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = NeuraIcons.Check,
                                        contentDescription = null,
                                        tint = NeuraNeonCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                Brush.linearGradient(listOf(NeuraElectricBlue.copy(alpha = 0.2f), NeuraVibrantViolet.copy(alpha = 0.2f))),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = NeuraIcons.Settings,
                            contentDescription = null,
                            tint = NeuraNeonCyan,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Connecte ton intelligence",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Le moteur Gemini AI et Agnes AI sont pré-configurés avec le système de raisonnement NEURA. Tu peux connecter ton propre endpoint à tout moment.",
                        fontSize = 13.5.sp,
                        color = NeuraTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF161622), RoundedCornerShape(16.dp))
                            .border(1.dp, NeuraCardBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Moteur Principal : Gemini 2.5 Flash",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Génération d'images : Agnes AI v2.1\nStockage : Room SQLite privé et chiffré",
                                fontSize = 12.sp,
                                color = NeuraTextMuted,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Bottom Actions
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            NeuraGradientButton(
                text = if (currentStep < 3) "Continuer" else "Commencer l'expérience",
                onClick = {
                    if (currentStep < 3) {
                        if (currentStep == 2) onSaveFocus(selectedFocus)
                        currentStep++
                    } else {
                        onFinishOnboarding()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "onboarding_continue_btn"
            )

            if (currentStep == 3) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Configurer plus tard",
                    fontSize = 13.sp,
                    color = NeuraTextMuted,
                    modifier = Modifier
                        .clickable { onFinishOnboarding() }
                        .padding(8.dp)
                )
            }
        }
    }
}
