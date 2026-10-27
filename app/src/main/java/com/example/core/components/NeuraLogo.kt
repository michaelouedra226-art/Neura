package com.example.core.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeuraElectricBlue
import com.example.ui.theme.NeuraNeonCyan
import com.example.ui.theme.NeuraVibrantViolet
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NeuraLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    animated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "NeuraLogoTransition")

    val pulseScale by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
    }

    val rotationAngle by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(12000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotationAngle"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }

    val glowAlpha by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.75f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glowAlpha"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0.5f) }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = (this.size.minDimension / 2f) * 0.82f * pulseScale

            // 1. Ambient synaptic glow aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeuraNeonCyan.copy(alpha = glowAlpha * 0.45f),
                        NeuraVibrantViolet.copy(alpha = glowAlpha * 0.25f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.35f
                ),
                radius = radius * 1.35f,
                center = center
            )

            // 2. Rotating orbital neural ring with gradient
            rotate(rotationAngle, pivot = center) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            NeuraNeonCyan,
                            NeuraElectricBlue,
                            NeuraVibrantViolet,
                            Color.Transparent,
                            NeuraNeonCyan
                        ),
                        center = center
                    ),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )

                // Synaptic satellite nodes on ring
                for (i in 0 until 4) {
                    val angle = (i * 90) * (Math.PI / 180.0)
                    val nodeX = center.x + radius * cos(angle).toFloat()
                    val nodeY = center.y + radius * sin(angle).toFloat()
                    drawCircle(
                        color = if (i % 2 == 0) NeuraNeonCyan else NeuraVibrantViolet,
                        radius = 3.5.dp.toPx(),
                        center = Offset(nodeX, nodeY)
                    )
                }
            }

            // 3. Counter-rotating inner synapse network
            rotate(-rotationAngle * 0.7f, pivot = center) {
                val innerRadius = radius * 0.58f
                drawCircle(
                    color = NeuraElectricBlue.copy(alpha = 0.5f),
                    radius = innerRadius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Internal neural connection vertices
                val p1 = Offset(center.x - innerRadius * 0.6f, center.y - innerRadius * 0.3f)
                val p2 = Offset(center.x + innerRadius * 0.6f, center.y - innerRadius * 0.3f)
                val p3 = Offset(center.x, center.y + innerRadius * 0.7f)

                drawLine(
                    color = NeuraNeonCyan.copy(alpha = 0.8f),
                    start = p1,
                    end = center,
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = NeuraVibrantViolet.copy(alpha = 0.8f),
                    start = p2,
                    end = center,
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = NeuraElectricBlue.copy(alpha = 0.8f),
                    start = p3,
                    end = center,
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Core pulsing nucleus
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, NeuraNeonCyan),
                        center = center,
                        radius = 8.dp.toPx()
                    ),
                    radius = 5.dp.toPx() * pulseScale,
                    center = center
                )
            }
        }
    }
}
