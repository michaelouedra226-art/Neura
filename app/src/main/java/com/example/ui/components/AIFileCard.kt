package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.runtime.remember
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
import com.example.core.components.triggerHaptic
import com.example.core.icons.NeuraIcons
import com.example.data.model.FilePayload
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
fun AIFileCard(
    payloadJson: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val payload = remember(payloadJson) { parseFilePayload(payloadJson) } ?: return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0xFF191B24), Color(0xFF10121A)))
            )
            .border(1.dp, NeuraCardBorder, RoundedCornerShape(22.dp))
            .padding(16.dp)
    ) {
        // Document Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        imageVector = NeuraIcons.FileDocument,
                        contentDescription = null,
                        tint = NeuraNeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = payload.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "${payload.pageCount} pages analysées • Format ${payload.fileType}",
                        fontSize = 11.5.sp,
                        color = NeuraTextMuted
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(Color(0xFF222432), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Indexé",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeuraNeonCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Summary text
        Text(
            text = payload.summary,
            fontSize = 13.5.sp,
            color = NeuraTextSecondary,
            lineHeight = 19.sp
        )

        if (payload.keyPoints.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Points clés extraits :",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = NeuraTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                payload.keyPoints.forEach { pt ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "• ",
                            fontSize = 12.sp,
                            color = NeuraNeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = pt,
                            fontSize = 12.sp,
                            color = NeuraTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF252736))
                    .clickable {
                        triggerHaptic(context)
                        Toast.makeText(context, "Synthèse complète ouverte", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 10.dp)
                    .testTag("open_file_summary_btn"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Consulter",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(NeuraElectricBlue)
                    .clickable {
                        triggerHaptic(context)
                        Toast.makeText(context, "Document exporté en PDF", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 10.dp)
                    .testTag("export_doc_btn"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Exporter (PDF)",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

private fun parseFilePayload(jsonStr: String): FilePayload? {
    return try {
        val root = JSONObject(jsonStr)
        val title = root.optString("title", "Document")
        val fileType = root.optString("fileType", "PDF")
        val pageCount = root.optInt("pageCount", 1)
        val summary = root.optString("summary", "")
        val keyPoints = mutableListOf<String>()
        val arr = root.optJSONArray("keyPoints")
        if (arr != null) {
            for (i in 0 until arr.length()) {
                keyPoints.add(arr.getString(i))
            }
        }
        FilePayload(title, fileType, pageCount, summary, keyPoints)
    } catch (_: Exception) {
        null
    }
}
