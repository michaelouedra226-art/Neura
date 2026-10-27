package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.components.triggerHaptic
import com.example.core.icons.NeuraIcons
import com.example.data.model.ImagePayload
import com.example.ui.theme.NeuraCard
import com.example.ui.theme.NeuraCardBorder
import com.example.ui.theme.NeuraElectricBlue
import com.example.ui.theme.NeuraNeonCyan
import com.example.ui.theme.NeuraTextMuted
import com.example.ui.theme.NeuraTextPrimary
import com.example.ui.theme.NeuraTextSecondary
import com.example.ui.theme.NeuraVibrantViolet
import org.json.JSONObject

@Composable
fun AIImageCard(
    payloadJson: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imagePayload = remember(payloadJson) { parseImagePayload(payloadJson) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF14141B))
            .border(1.dp, NeuraCardBorder, RoundedCornerShape(22.dp))
            .padding(14.dp)
    ) {
        // Image Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(NeuraNeonCyan.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = NeuraIcons.Image,
                        contentDescription = null,
                        tint = NeuraNeonCyan,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Génération Agnes AI",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeuraTextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(listOf(NeuraElectricBlue.copy(alpha = 0.3f), NeuraVibrantViolet.copy(alpha = 0.3f))),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "8K Studio",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeuraNeonCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Image Preview Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF09090B))
                .border(1.dp, Color(0xFF2E2E38), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.neura_icon_fg),
                contentDescription = "Illustration générée",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Prompt Description
        Text(
            text = imagePayload?.prompt ?: "Visualisation haute définition créée par NEURA",
            fontSize = 12.sp,
            color = NeuraTextSecondary,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons: Enregistrer, Partager
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF22222E))
                    .border(1.dp, NeuraCardBorder.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .clickable {
                        triggerHaptic(context)
                        Toast.makeText(context, "Image enregistrée dans la Galerie", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 10.dp)
                    .testTag("save_image_btn"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = NeuraIcons.Check,
                        contentDescription = null,
                        tint = NeuraNeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Enregistrer",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(listOf(NeuraElectricBlue, NeuraVibrantViolet))
                    )
                    .clickable {
                        triggerHaptic(context)
                        Toast.makeText(context, "Lien de partage généré", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 10.dp)
                    .testTag("share_image_btn"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = NeuraIcons.Sparkles,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Partager",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private fun parseImagePayload(jsonStr: String): ImagePayload? {
    return try {
        val root = JSONObject(jsonStr)
        ImagePayload(
            prompt = root.optString("prompt", "Illustration studio haute résolution"),
            imageUrl = root.optString("imageUrl", ""),
            aspect = root.optString("aspect", "1:1"),
            style = root.optString("style", "Digital Art Neo-Futurism")
        )
    } catch (_: Exception) {
        null
    }
}
