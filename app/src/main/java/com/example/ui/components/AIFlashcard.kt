package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.components.triggerHaptic
import com.example.core.icons.NeuraIcons
import com.example.data.model.FlashcardItem
import com.example.data.model.FlashcardPayload
import com.example.ui.theme.NeuraCard
import com.example.ui.theme.NeuraCardBorder
import com.example.ui.theme.NeuraElectricBlue
import com.example.ui.theme.NeuraNeonCyan
import com.example.ui.theme.NeuraSuccess
import com.example.ui.theme.NeuraTextMuted
import com.example.ui.theme.NeuraTextPrimary
import com.example.ui.theme.NeuraTextSecondary
import com.example.ui.theme.NeuraVibrantViolet
import com.example.ui.theme.NeuraWarning
import org.json.JSONObject

@Composable
fun AIFlashcard(
    payloadJson: String,
    modifier: Modifier = Modifier
) {
    val payload = remember(payloadJson) { parseFlashcardPayload(payloadJson) } ?: return
    val context = LocalContext.current

    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val currentCard = payload.cards.getOrNull(currentIndex) ?: return

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "flashcard_flip"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF161622),
                        Color(0xFF0F0F16)
                    )
                )
            )
            .border(1.dp, NeuraCardBorder, RoundedCornerShape(22.dp))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(NeuraElectricBlue.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = NeuraIcons.Flashcards,
                        contentDescription = null,
                        tint = NeuraElectricBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = payload.title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeuraTextPrimary
                )
            }

            Text(
                text = "${currentIndex + 1} / ${payload.cards.size}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = NeuraNeonCyan
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3D Flippable Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (rotation <= 90f) {
                        Brush.verticalGradient(listOf(Color(0xFF222230), Color(0xFF1A1A24)))
                    } else {
                        Brush.verticalGradient(listOf(Color(0xFF1E2838), Color(0xFF121A28)))
                    }
                )
                .border(
                    width = 1.dp,
                    color = if (rotation <= 90f) NeuraCardBorder else NeuraNeonCyan.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable {
                    triggerHaptic(context)
                    isFlipped = !isFlipped
                }
                .padding(18.dp)
                .testTag("flashcard_body"),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                // Front: Question
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "QUESTION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeuraVibrantViolet,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentCard.front,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 21.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Touche pour retourner ↺",
                        fontSize = 11.sp,
                        color = NeuraTextMuted
                    )
                }
            } else {
                // Back: Answer (counter-rotated so text is not mirrored)
                Column(
                    modifier = Modifier.graphicsLayer { rotationY = 180f },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "RÉPONSE CLÉ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeuraNeonCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentCard.back,
                        fontSize = 13.5.sp,
                        color = NeuraTextPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Navigation between cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF20202A))
                    .clickable(enabled = currentIndex > 0) {
                        triggerHaptic(context)
                        isFlipped = false
                        currentIndex--
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("flashcard_prev")
            ) {
                Text(
                    text = "← Précédente",
                    fontSize = 12.sp,
                    color = if (currentIndex > 0) NeuraTextPrimary else NeuraTextMuted
                )
            }

            // Difficulty tag
            Box(
                modifier = Modifier
                    .background(
                        when (currentCard.difficulty.lowercase()) {
                            "facile" -> NeuraSuccess.copy(alpha = 0.2f)
                            "difficile" -> NeuraWarning.copy(alpha = 0.2f)
                            else -> NeuraElectricBlue.copy(alpha = 0.2f)
                        },
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = currentCard.difficulty,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = when (currentCard.difficulty.lowercase()) {
                        "facile" -> NeuraSuccess
                        "difficile" -> NeuraWarning
                        else -> NeuraNeonCyan
                    }
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF20202A))
                    .clickable(enabled = currentIndex < payload.cards.size - 1) {
                        triggerHaptic(context)
                        isFlipped = false
                        currentIndex++
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("flashcard_next")
            ) {
                Text(
                    text = "Suivante →",
                    fontSize = 12.sp,
                    color = if (currentIndex < payload.cards.size - 1) NeuraTextPrimary else NeuraTextMuted
                )
            }
        }
    }
}

private fun parseFlashcardPayload(jsonStr: String): FlashcardPayload? {
    return try {
        val root = JSONObject(jsonStr)
        val title = root.optString("title", "Fiche de Révision")
        val cardsArray = root.optJSONArray("cards") ?: return null
        val cards = mutableListOf<FlashcardItem>()
        for (i in 0 until cardsArray.length()) {
            val c = cardsArray.getJSONObject(i)
            cards.add(
                FlashcardItem(
                    id = c.optInt("id", i + 1),
                    front = c.getString("front"),
                    back = c.getString("back"),
                    difficulty = c.optString("difficulty", "Moyen")
                )
            )
        }
        FlashcardPayload(title, cards)
    } catch (_: Exception) {
        null
    }
}
