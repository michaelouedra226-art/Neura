package com.example.core.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object NeuraIcons {

    val Brain: ImageVector by lazy {
        ImageVector.Builder(
            name = "Brain",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Left hemisphere lobes
                moveTo(9.5f, 4.5f)
                curveTo(7.5f, 4.5f, 5.5f, 6.0f, 5.5f, 8.5f)
                curveTo(4.0f, 9.5f, 3.5f, 11.5f, 4.0f, 13.5f)
                curveTo(3.5f, 15.5f, 4.5f, 17.5f, 6.5f, 18.5f)
                curveTo(7.5f, 20.0f, 9.5f, 20.5f, 11.0f, 20.0f)

                // Right hemisphere lobes
                moveTo(14.5f, 4.5f)
                curveTo(16.5f, 4.5f, 18.5f, 6.0f, 18.5f, 8.5f)
                curveTo(20.0f, 9.5f, 20.5f, 11.5f, 20.0f, 13.5f)
                curveTo(20.5f, 15.5f, 19.5f, 17.5f, 17.5f, 18.5f)
                curveTo(16.5f, 20.0f, 14.5f, 20.5f, 13.0f, 20.0f)

                // Central fissure
                moveTo(12.0f, 4.0f)
                lineTo(12.0f, 20.5f)

                // Synaptic connection tracks
                moveTo(12.0f, 8.0f)
                curveTo(10.0f, 7.5f, 8.5f, 9.0f, 7.0f, 9.0f)
                moveTo(12.0f, 13.0f)
                curveTo(10.0f, 12.5f, 8.0f, 14.0f, 6.5f, 14.5f)
                moveTo(12.0f, 16.5f)
                curveTo(10.5f, 16.5f, 9.5f, 17.5f, 8.5f, 18.0f)

                moveTo(12.0f, 8.0f)
                curveTo(14.0f, 7.5f, 15.5f, 9.0f, 17.0f, 9.0f)
                moveTo(12.0f, 13.0f)
                curveTo(14.0f, 12.5f, 16.0f, 14.0f, 17.5f, 14.5f)
                moveTo(12.0f, 16.5f)
                curveTo(13.5f, 16.5f, 14.5f, 17.5f, 15.5f, 18.0f)
            }
        }.build()
    }

    val Chat: ImageVector by lazy {
        ImageVector.Builder(
            name = "Chat",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(21.0f, 11.5f)
                curveTo(21.0f, 16.19f, 16.97f, 20.0f, 12.0f, 20.0f)
                curveTo(10.45f, 20.0f, 8.98f, 19.63f, 7.7f, 18.98f)
                lineTo(3.0f, 20.0f)
                lineTo(4.35f, 16.15f)
                curveTo(3.5f, 14.8f, 3.0f, 13.2f, 3.0f, 11.5f)
                curveTo(3.0f, 6.81f, 7.03f, 3.0f, 12.0f, 3.0f)
                curveTo(16.97f, 3.0f, 21.0f, 6.81f, 21.0f, 11.5f)
                close()
            }
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.0f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(8.0f, 11.5f)
                lineTo(8.01f, 11.5f)
                moveTo(12.0f, 11.5f)
                lineTo(12.01f, 11.5f)
                moveTo(16.0f, 11.5f)
                lineTo(16.01f, 11.5f)
            }
        }.build()
    }

    val Memory: ImageVector by lazy {
        ImageVector.Builder(
            name = "Memory",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Chip body
                moveTo(6.0f, 4.0f)
                lineTo(18.0f, 4.0f)
                curveTo(19.1f, 4.0f, 20.0f, 4.9f, 20.0f, 6.0f)
                lineTo(20.0f, 18.0f)
                curveTo(20.0f, 19.1f, 19.1f, 20.0f, 18.0f, 20.0f)
                lineTo(6.0f, 20.0f)
                curveTo(4.9f, 20.0f, 4.0f, 19.1f, 4.0f, 18.0f)
                lineTo(4.0f, 6.0f)
                curveTo(4.0f, 4.9f, 4.9f, 4.0f, 6.0f, 4.0f)
                close()

                // Inner core
                moveTo(9.0f, 9.0f)
                lineTo(15.0f, 9.0f)
                lineTo(15.0f, 15.0f)
                lineTo(9.0f, 15.0f)
                close()

                // Top pins
                moveTo(9.0f, 1.5f)
                lineTo(9.0f, 4.0f)
                moveTo(15.0f, 1.5f)
                lineTo(15.0f, 4.0f)

                // Bottom pins
                moveTo(9.0f, 20.0f)
                lineTo(9.0f, 22.5f)
                moveTo(15.0f, 20.0f)
                lineTo(15.0f, 22.5f)

                // Left pins
                moveTo(1.5f, 9.0f)
                lineTo(4.0f, 9.0f)
                moveTo(1.5f, 15.0f)
                lineTo(4.0f, 15.0f)

                // Right pins
                moveTo(20.0f, 9.0f)
                lineTo(22.5f, 9.0f)
                moveTo(20.0f, 15.0f)
                lineTo(22.5f, 15.0f)
            }
        }.build()
    }

    val Creations: ImageVector by lazy {
        ImageVector.Builder(
            name = "Creations",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Layer stack
                moveTo(12.0f, 3.0f)
                lineTo(21.0f, 7.5f)
                lineTo(12.0f, 12.0f)
                lineTo(3.0f, 7.5f)
                close()

                moveTo(3.0f, 12.0f)
                lineTo(12.0f, 16.5f)
                lineTo(21.0f, 12.0f)

                moveTo(3.0f, 16.5f)
                lineTo(12.0f, 21.0f)
                lineTo(21.0f, 16.5f)
            }
        }.build()
    }

    val Settings: ImageVector by lazy {
        ImageVector.Builder(
            name = "Settings",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12.0f, 15.0f)
                curveTo(13.657f, 15.0f, 15.0f, 13.657f, 15.0f, 12.0f)
                curveTo(15.0f, 10.343f, 13.657f, 9.0f, 12.0f, 9.0f)
                curveTo(10.343f, 9.0f, 9.0f, 10.343f, 9.0f, 12.0f)
                curveTo(9.0f, 13.657f, 10.343f, 15.0f, 12.0f, 15.0f)
                close()

                moveTo(19.4f, 15.0f)
                curveTo(19.5f, 14.5f, 19.5f, 14.0f, 19.5f, 13.5f)
                curveTo(19.5f, 13.0f, 19.5f, 12.5f, 19.4f, 12.0f)
                lineTo(21.5f, 10.4f)
                lineTo(19.5f, 7.0f)
                lineTo(17.0f, 8.0f)
                curveTo(16.1f, 7.3f, 15.2f, 6.8f, 14.2f, 6.4f)
                lineTo(13.8f, 3.8f)
                lineTo(10.2f, 3.8f)
                lineTo(9.8f, 6.4f)
                curveTo(8.8f, 6.8f, 7.9f, 7.3f, 7.0f, 8.0f)
                lineTo(4.5f, 7.0f)
                lineTo(2.5f, 10.4f)
                lineTo(4.6f, 12.0f)
                curveTo(4.5f, 12.5f, 4.5f, 13.0f, 4.5f, 13.5f)
                curveTo(4.5f, 14.0f, 4.5f, 14.5f, 4.6f, 15.0f)
                lineTo(2.5f, 16.6f)
                lineTo(4.5f, 20.0f)
                lineTo(7.0f, 19.0f)
                curveTo(7.9f, 19.7f, 8.8f, 20.2f, 9.8f, 20.6f)
                lineTo(10.2f, 23.2f)
                lineTo(13.8f, 23.2f)
                lineTo(14.2f, 20.6f)
                curveTo(15.2f, 20.2f, 16.1f, 19.7f, 17.0f, 19.0f)
                lineTo(19.5f, 20.0f)
                lineTo(21.5f, 16.6f)
                close()
            }
        }.build()
    }

    val Sparkles: ImageVector by lazy {
        ImageVector.Builder(
            name = "Sparkles",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12.0f, 2.0f)
                curveTo(12.0f, 7.5f, 16.5f, 12.0f, 22.0f, 12.0f)
                curveTo(16.5f, 12.0f, 12.0f, 16.5f, 12.0f, 22.0f)
                curveTo(12.0f, 16.5f, 7.5f, 12.0f, 2.0f, 12.0f)
                curveTo(7.5f, 12.0f, 12.0f, 7.5f, 12.0f, 2.0f)
                close()

                moveTo(19.0f, 3.0f)
                curveTo(19.0f, 5.0f, 20.5f, 6.5f, 22.5f, 6.5f)
                curveTo(20.5f, 6.5f, 19.0f, 8.0f, 19.0f, 10.0f)
                curveTo(19.0f, 8.0f, 17.5f, 6.5f, 15.5f, 6.5f)
                curveTo(17.5f, 6.5f, 19.0f, 5.0f, 19.0f, 3.0f)
                close()
            }
        }.build()
    }

    val Send: ImageVector by lazy {
        ImageVector.Builder(
            name = "Send",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(2.01f, 21.0f)
                lineTo(23.0f, 12.0f)
                lineTo(2.01f, 3.0f)
                lineTo(2.0f, 10.0f)
                lineTo(17.0f, 12.0f)
                lineTo(2.0f, 14.0f)
                close()
            }
        }.build()
    }

    val Mic: ImageVector by lazy {
        ImageVector.Builder(
            name = "Mic",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Mic capsule
                moveTo(12.0f, 3.0f)
                curveTo(10.34f, 3.0f, 9.0f, 4.34f, 9.0f, 6.0f)
                lineTo(9.0f, 12.0f)
                curveTo(9.0f, 13.66f, 10.34f, 15.0f, 12.0f, 15.0f)
                curveTo(13.66f, 15.0f, 15.0f, 13.66f, 15.0f, 12.0f)
                lineTo(15.0f, 6.0f)
                curveTo(15.0f, 4.34f, 13.66f, 3.0f, 12.0f, 3.0f)
                close()

                // Cradle
                moveTo(19.0f, 10.0f)
                lineTo(19.0f, 12.0f)
                curveTo(19.0f, 15.87f, 15.87f, 19.0f, 12.0f, 19.0f)
                curveTo(8.13f, 19.0f, 5.0f, 15.87f, 5.0f, 12.0f)
                lineTo(5.0f, 10.0f)

                // Stand
                moveTo(12.0f, 19.0f)
                lineTo(12.0f, 22.0f)
                moveTo(8.0f, 22.0f)
                lineTo(16.0f, 22.0f)
            }
        }.build()
    }

    val Paperclip: ImageVector by lazy {
        ImageVector.Builder(
            name = "Paperclip",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(16.5f, 6.0f)
                lineTo(8.5f, 14.0f)
                curveTo(7.12f, 15.38f, 7.12f, 17.62f, 8.5f, 19.0f)
                curveTo(9.88f, 20.38f, 12.12f, 20.38f, 13.5f, 19.0f)
                lineTo(20.0f, 12.5f)
                curveTo(22.07f, 10.43f, 22.07f, 7.07f, 20.0f, 5.0f)
                curveTo(17.93f, 2.93f, 14.57f, 2.93f, 12.5f, 5.0f)
                lineTo(6.0f, 11.5f)
                curveTo(3.24f, 14.26f, 3.24f, 18.74f, 6.0f, 21.5f)
            }
        }.build()
    }

    val Plus: ImageVector by lazy {
        ImageVector.Builder(
            name = "Plus",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.0f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(12.0f, 5.0f)
                lineTo(12.0f, 19.0f)
                moveTo(5.0f, 12.0f)
                lineTo(19.0f, 12.0f)
            }
        }.build()
    }

    val FileDocument: ImageVector by lazy {
        ImageVector.Builder(
            name = "FileDocument",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(14.0f, 2.0f)
                lineTo(6.0f, 2.0f)
                curveTo(4.9f, 2.0f, 4.0f, 2.9f, 4.0f, 4.0f)
                lineTo(4.0f, 20.0f)
                curveTo(4.0f, 21.1f, 4.9f, 22.0f, 6.0f, 22.0f)
                lineTo(18.0f, 22.0f)
                curveTo(19.1f, 22.0f, 20.0f, 21.1f, 20.0f, 20.0f)
                lineTo(20.0f, 8.0f)
                lineTo(14.0f, 2.0f)
                close()

                moveTo(14.0f, 2.0f)
                lineTo(14.0f, 8.0f)
                lineTo(20.0f, 8.0f)

                moveTo(8.0f, 13.0f)
                lineTo(16.0f, 13.0f)

                moveTo(8.0f, 17.0f)
                lineTo(13.0f, 17.0f)
            }
        }.build()
    }

    val Image: ImageVector by lazy {
        ImageVector.Builder(
            name = "Image",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(5.0f, 4.0f)
                lineTo(19.0f, 4.0f)
                curveTo(20.1f, 4.0f, 21.0f, 4.9f, 21.0f, 6.0f)
                lineTo(21.0f, 18.0f)
                curveTo(21.0f, 19.1f, 20.1f, 20.0f, 19.0f, 20.0f)
                lineTo(5.0f, 20.0f)
                curveTo(3.9f, 20.0f, 3.0f, 19.1f, 3.0f, 18.0f)
                lineTo(3.0f, 6.0f)
                curveTo(3.0f, 4.9f, 3.9f, 4.0f, 5.0f, 4.0f)
                close()

                // Sun
                moveTo(8.5f, 9.0f)
                curveTo(8.5f, 9.55f, 8.05f, 10.0f, 7.5f, 10.0f)
                curveTo(6.95f, 10.0f, 6.5f, 9.55f, 6.5f, 9.0f)
                curveTo(6.5f, 8.45f, 6.95f, 8.0f, 7.5f, 8.0f)
                curveTo(8.05f, 8.0f, 8.5f, 8.45f, 8.5f, 9.0f)
                close()

                // Mountains
                moveTo(21.0f, 15.0f)
                lineTo(16.0f, 10.0f)
                lineTo(5.0f, 19.0f)

                moveTo(14.0f, 14.0f)
                lineTo(17.0f, 17.0f)
            }
        }.build()
    }

    val Code: ImageVector by lazy {
        ImageVector.Builder(
            name = "Code",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(8.0f, 7.0f)
                lineTo(3.0f, 12.0f)
                lineTo(8.0f, 17.0f)

                moveTo(16.0f, 7.0f)
                lineTo(21.0f, 12.0f)
                lineTo(16.0f, 17.0f)

                moveTo(14.0f, 4.0f)
                lineTo(10.0f, 20.0f)
            }
        }.build()
    }

    val Quiz: ImageVector by lazy {
        ImageVector.Builder(
            name = "Quiz",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(9.0f, 9.0f)
                curveTo(9.0f, 7.34f, 10.34f, 6.0f, 12.0f, 6.0f)
                curveTo(13.66f, 6.0f, 15.0f, 7.34f, 15.0f, 9.0f)
                curveTo(15.0f, 10.5f, 13.5f, 11.5f, 12.0f, 12.5f)
                lineTo(12.0f, 14.5f)

                moveTo(12.0f, 18.0f)
                lineTo(12.01f, 18.0f)

                moveTo(12.0f, 2.0f)
                curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                curveTo(2.0f, 17.52f, 6.48f, 22.0f, 12.0f, 22.0f)
                curveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
                curveTo(22.0f, 6.48f, 17.52f, 2.0f, 12.0f, 2.0f)
                close()
            }
        }.build()
    }

    val Flashcards: ImageVector by lazy {
        ImageVector.Builder(
            name = "Flashcards",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Back card
                moveTo(7.0f, 4.0f)
                lineTo(19.0f, 4.0f)
                curveTo(20.1f, 4.0f, 21.0f, 4.9f, 21.0f, 6.0f)
                lineTo(21.0f, 16.0f)

                // Front card
                moveTo(5.0f, 7.0f)
                lineTo(17.0f, 7.0f)
                curveTo(18.1f, 7.0f, 19.0f, 7.9f, 19.0f, 9.0f)
                lineTo(19.0f, 19.0f)
                curveTo(19.0f, 20.1f, 18.1f, 21.0f, 17.0f, 21.0f)
                lineTo(5.0f, 21.0f)
                curveTo(3.9f, 21.0f, 3.0f, 20.1f, 3.0f, 19.0f)
                lineTo(3.0f, 9.0f)
                curveTo(3.0f, 7.9f, 3.9f, 7.0f, 5.0f, 7.0f)
                close()

                // Question mark hint
                moveTo(10.0f, 12.0f)
                lineTo(12.0f, 12.0f)
                lineTo(12.0f, 14.0f)
                moveTo(12.0f, 16.5f)
                lineTo(12.01f, 16.5f)
            }
        }.build()
    }

    val Check: ImageVector by lazy {
        ImageVector.Builder(
            name = "Check",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.0f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(20.0f, 6.0f)
                lineTo(9.0f, 17.0f)
                lineTo(4.0f, 12.0f)
            }
        }.build()
    }

    val Close: ImageVector by lazy {
        ImageVector.Builder(
            name = "Close",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.0f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(18.0f, 6.0f)
                lineTo(6.0f, 18.0f)
                moveTo(6.0f, 6.0f)
                lineTo(18.0f, 18.0f)
            }
        }.build()
    }

    val Copy: ImageVector by lazy {
        ImageVector.Builder(
            name = "Copy",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(9.0f, 9.0f)
                lineTo(19.0f, 9.0f)
                curveTo(20.1f, 9.0f, 21.0f, 9.9f, 21.0f, 11.0f)
                lineTo(21.0f, 20.0f)
                curveTo(21.0f, 21.1f, 20.1f, 22.0f, 19.0f, 22.0f)
                lineTo(9.0f, 22.0f)
                curveTo(7.9f, 22.0f, 7.0f, 21.1f, 7.0f, 20.0f)
                lineTo(7.0f, 11.0f)
                curveTo(7.0f, 9.9f, 7.9f, 9.0f, 9.0f, 9.0f)
                close()

                moveTo(5.0f, 15.0f)
                lineTo(4.0f, 15.0f)
                curveTo(3.45f, 15.0f, 3.0f, 14.55f, 3.0f, 14.0f)
                lineTo(3.0f, 4.0f)
                curveTo(3.0f, 3.45f, 3.45f, 3.0f, 4.0f, 3.0f)
                lineTo(14.0f, 3.0f)
                curveTo(14.55f, 3.0f, 15.0f, 3.45f, 15.0f, 4.0f)
                lineTo(15.0f, 5.0f)
            }
        }.build()
    }

    val Trash: ImageVector by lazy {
        ImageVector.Builder(
            name = "Trash",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3.0f, 6.0f)
                lineTo(21.0f, 6.0f)
                moveTo(19.0f, 6.0f)
                lineTo(18.0f, 20.0f)
                curveTo(17.9f, 21.1f, 17.0f, 22.0f, 16.0f, 22.0f)
                lineTo(8.0f, 22.0f)
                curveTo(7.0f, 22.0f, 6.1f, 21.1f, 6.0f, 20.0f)
                lineTo(5.0f, 6.0f)

                moveTo(9.0f, 6.0f)
                lineTo(9.0f, 4.0f)
                curveTo(9.0f, 3.0f, 10.0f, 2.0f, 11.0f, 2.0f)
                lineTo(13.0f, 2.0f)
                curveTo(14.0f, 2.0f, 15.0f, 3.0f, 15.0f, 4.0f)
                lineTo(15.0f, 6.0f)
            }
        }.build()
    }

    val Search: ImageVector by lazy {
        ImageVector.Builder(
            name = "Search",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(11.0f, 19.0f)
                curveTo(15.42f, 19.0f, 19.0f, 15.42f, 19.0f, 11.0f)
                curveTo(19.0f, 6.58f, 15.42f, 3.0f, 11.0f, 3.0f)
                curveTo(6.58f, 3.0f, 3.0f, 6.58f, 3.0f, 11.0f)
                curveTo(3.0f, 15.42f, 6.58f, 19.0f, 11.0f, 19.0f)
                close()

                moveTo(21.0f, 21.0f)
                lineTo(16.65f, 16.65f)
            }
        }.build()
    }

    val WifiOnline: ImageVector by lazy {
        ImageVector.Builder(
            name = "WifiOnline",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 1.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(2.0f, 8.82f)
                curveTo(7.52f, 3.73f, 16.48f, 3.73f, 22.0f, 8.82f)
                moveTo(5.0f, 12.86f)
                curveTo(8.87f, 9.29f, 15.13f, 9.29f, 19.0f, 12.86f)
                moveTo(8.5f, 16.4f)
                curveTo(10.43f, 14.62f, 13.57f, 14.62f, 15.5f, 16.4f)
                moveTo(12.0f, 20.0f)
                lineTo(12.01f, 20.0f)
            }
        }.build()
    }

    val ArrowForward: ImageVector by lazy {
        ImageVector.Builder(
            name = "ArrowForward",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 2.0f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(5.0f, 12.0f)
                lineTo(19.0f, 12.0f)
                moveTo(13.0f, 6.0f)
                lineTo(19.0f, 12.0f)
                lineTo(13.0f, 18.0f)
            }
        }.build()
    }
}
