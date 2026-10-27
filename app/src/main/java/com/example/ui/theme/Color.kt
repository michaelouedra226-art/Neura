package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// NEURA Dark Premium Color Palette
val NeuraBackground = Color(0xFF09090B)
val NeuraSurface = Color(0xFF18181B)
val NeuraCard = Color(0xFF27272A)
val NeuraCardBorder = Color(0xFF3F3F46)
val NeuraSurfaceElevated = Color(0xFF202025)

// Text Colors
val NeuraTextPrimary = Color(0xFFFFFFFF)
val NeuraTextSecondary = Color(0xFFA1A1AA)
val NeuraTextMuted = Color(0xFF71717A)

// Accent Colors
val NeuraElectricBlue = Color(0xFF2563EB)
val NeuraDeepPurple = Color(0xFF7C3AED)
val NeuraNeonCyan = Color(0xFF06B6D4)
val NeuraVibrantViolet = Color(0xFF8B5CF6)
val NeuraGlowPink = Color(0xFFEC4899)
val NeuraSuccess = Color(0xFF10B981)
val NeuraWarning = Color(0xFFF59E0B)
val NeuraError = Color(0xFFEF4444)

// Gradient Brushes
val NeuraAccentGradient = Brush.linearGradient(
    colors = listOf(NeuraElectricBlue, NeuraDeepPurple)
)

val NeuraCyanPurpleGradient = Brush.linearGradient(
    colors = listOf(NeuraNeonCyan, NeuraVibrantViolet)
)

val NeuraGlowGradient = Brush.radialGradient(
    colors = listOf(
        Color(0x332563EB),
        Color(0x227C3AED),
        Color.Transparent
    )
)

val NeuraCardGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF27272A),
        Color(0xFF1C1C20)
    )
)
