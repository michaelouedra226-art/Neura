package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import com.example.core.components.triggerHaptic
import com.example.core.icons.NeuraIcons
import com.example.ui.theme.NeuraCard
import com.example.ui.theme.NeuraCardBorder
import com.example.ui.theme.NeuraElectricBlue
import com.example.ui.theme.NeuraNeonCyan
import com.example.ui.theme.NeuraTextMuted
import com.example.ui.theme.NeuraTextPrimary
import com.example.ui.theme.NeuraTextSecondary
import com.example.ui.theme.NeuraVibrantViolet
import kotlinx.coroutines.delay

@Composable
fun AttachmentDialog(
    onDismiss: () -> Unit,
    onFileSelected: (name: String, type: String) -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF16161F))
                .border(1.dp, NeuraCardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ajouter à NEURA",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = NeuraIcons.Close,
                        contentDescription = "Fermer",
                        tint = NeuraTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val options = listOf(
                Triple("Cours_Geographie_SIG.pdf", "Document de cours (38 pages)", NeuraIcons.FileDocument),
                Triple("Schema_Architecture_Systeme.png", "Illustration / Image technique", NeuraIcons.Image),
                Triple("Donnees_Python_Pipeline.txt", "Script & données brutes", NeuraIcons.Code)
            )

            options.forEach { (title, subtitle, icon) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E1E2A))
                        .border(1.dp, NeuraCardBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .clickable {
                            triggerHaptic(context)
                            onFileSelected(title, if (title.endsWith(".pdf")) "PDF" else "IMAGE")
                            onDismiss()
                        }
                        .padding(14.dp)
                        .testTag("attach_${title.take(8)}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Brush.linearGradient(listOf(NeuraElectricBlue.copy(alpha = 0.3f), NeuraVibrantViolet.copy(alpha = 0.3f))),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = NeuraNeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = subtitle,
                            fontSize = 11.5.sp,
                            color = NeuraTextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceInputDialog(
    onDismiss: () -> Unit,
    onTranscribed: (String) -> Unit
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "voiceWave")

    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w3"
    )

    // Automatically simulate transcription after 2.4s
    LaunchedEffect(Unit) {
        delay(2400)
        triggerHaptic(context)
        onTranscribed("Analyse ce cours et prépare-moi un plan de révision avec des fiches")
        onDismiss()
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFF13131A))
                .border(
                    1.dp,
                    Brush.linearGradient(listOf(NeuraNeonCyan.copy(alpha = 0.5f), NeuraVibrantViolet.copy(alpha = 0.5f))),
                    RoundedCornerShape(26.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NEURA Écoute...",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Voice Waves
            Row(
                modifier = Modifier.height(60.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(wave1, wave2, wave3, wave2, wave1).forEach { w ->
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height((48 * w).dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.verticalGradient(listOf(NeuraNeonCyan, NeuraElectricBlue, NeuraVibrantViolet))
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Parle naturellement. NEURA comprend ton intention en temps réel.",
                fontSize = 12.sp,
                color = NeuraTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF22222E))
                    .clickable { onDismiss() }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .testTag("cancel_voice_btn")
            ) {
                Text(
                    text = "Annuler",
                    fontSize = 12.sp,
                    color = NeuraTextMuted
                )
            }
        }
    }
}
