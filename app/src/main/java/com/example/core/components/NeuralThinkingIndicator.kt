package com.example.core.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeuraCard
import com.example.ui.theme.NeuraCardBorder
import com.example.ui.theme.NeuraElectricBlue
import com.example.ui.theme.NeuraNeonCyan
import com.example.ui.theme.NeuraTextSecondary
import com.example.ui.theme.NeuraVibrantViolet
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NeuralThinkingIndicator(
    modifier: Modifier = Modifier,
    customStatusText: String? = null
) {
    val states = listOf("NEURA analyse l'intention...", "Exploration neuronale...", "Synthèse cognitive...", "Finalisation...")
    var stateIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1200)
            stateIndex = (stateIndex + 1) % states.size
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "NeuralThinking")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val spin by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    Box(
        modifier = modifier
            .background(NeuraCard.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
            .border(1.dp, NeuraCardBorder.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Neural nucleus graphic
            Canvas(modifier = Modifier.size(24.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)

                // Rotating synaptic particles
                rotate(spin, pivot = center) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = listOf(NeuraNeonCyan, NeuraElectricBlue, NeuraVibrantViolet, Color.Transparent),
                            center = center
                        ),
                        radius = 10.dp.toPx(),
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    for (i in 0 until 3) {
                        val angle = (i * 120) * (Math.PI / 180.0)
                        val px = center.x + 10.dp.toPx() * cos(angle).toFloat()
                        val py = center.y + 10.dp.toPx() * sin(angle).toFloat()
                        drawCircle(
                            color = NeuraNeonCyan,
                            radius = 2.dp.toPx(),
                            center = Offset(px, py)
                        )
                    }
                }

                // Pulsing central node
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, NeuraVibrantViolet),
                        center = center,
                        radius = 6.dp.toPx()
                    ),
                    radius = 3.5.dp.toPx() * pulse,
                    center = center
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Dynamic status text
            AnimatedContent(
                targetState = customStatusText ?: states[stateIndex],
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                label = "statusAnimation"
            ) { text ->
                Text(
                    text = text,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeuraTextSecondary
                )
            }
        }
    }
}
