package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.data.local.entities.CreationEntity
import org.json.JSONObject
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
import com.example.data.model.ContentType
import com.example.ui.components.AICodeBlock
import com.example.ui.components.AIFileCard
import com.example.ui.components.AIFlashcard
import com.example.ui.components.AIImageCard
import com.example.ui.components.AIQuizCard
import com.example.ui.theme.NeuraBackground
import com.example.ui.theme.NeuraCard
import com.example.ui.theme.NeuraCardBorder
import com.example.ui.theme.NeuraElectricBlue
import com.example.ui.theme.NeuraError
import com.example.ui.theme.NeuraNeonCyan
import com.example.ui.theme.NeuraTextMuted
import com.example.ui.theme.NeuraTextPrimary
import com.example.ui.theme.NeuraTextSecondary
import com.example.ui.theme.NeuraVibrantViolet
import com.example.ui.viewmodel.NeuraViewModel

@Composable
fun CreationsScreen(
    viewModel: NeuraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val creations by viewModel.creations.collectAsState()
    val activeFilter by viewModel.creationsFilter.collectAsState()

    val filterTabs = listOf("TOUS", "IMAGE", "FLASHCARD", "QUIZ", "FILE", "CODE")

    val filteredCreations = remember(creations, activeFilter) {
        if (activeFilter == "TOUS") creations
        else creations.filter { it.type == activeFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeuraBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Column {
            Text(
                text = "Créations NEURA",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Bibliothèque des contenus générés automatiquement",
                fontSize = 12.sp,
                color = NeuraTextMuted
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterTabs.forEach { tabKey ->
                val isSelected = tabKey == activeFilter
                val label = when (tabKey) {
                    "TOUS" -> "Tous"
                    "IMAGE" -> "Images"
                    "FLASHCARD" -> "Fiches"
                    "QUIZ" -> "Quiz"
                    "FILE" -> "Documents"
                    "CODE" -> "Codes"
                    else -> tabKey
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) Brush.horizontalGradient(listOf(NeuraElectricBlue, NeuraVibrantViolet))
                            else Brush.horizontalGradient(listOf(Color(0xFF1E1E2A), Color(0xFF1E1E2A)))
                        )
                        .border(
                            1.dp,
                            if (isSelected) NeuraNeonCyan else NeuraCardBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            triggerHaptic(context)
                            viewModel.setCreationsFilter(tabKey)
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("filter_$tabKey"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) Color.White else NeuraTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Creations List
        if (filteredCreations.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = NeuraIcons.Creations,
                        contentDescription = null,
                        tint = NeuraTextMuted,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aucune création dans cette catégorie.",
                        fontSize = 14.sp,
                        color = NeuraTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Demande à NEURA de générer un quiz, une fiche ou une image !",
                        fontSize = 12.sp,
                        color = NeuraTextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredCreations, key = { it.id }) { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF15151F))
                            .border(1.dp, NeuraCardBorder, RoundedCornerShape(20.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = item.title,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = item.subtitle,
                                    fontSize = 11.5.sp,
                                    color = NeuraTextMuted
                                )
                            }

                            IconButton(
                                onClick = {
                                    triggerHaptic(context)
                                    viewModel.deleteCreation(item.id)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = NeuraIcons.Trash,
                                    contentDescription = "Supprimer",
                                    tint = NeuraError.copy(alpha = 0.6f),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Render Content
                        when (item.type) {
                            ContentType.IMAGE.name -> AIImageCard(payloadJson = item.contentJson)
                            ContentType.QUIZ.name -> AIQuizCard(payloadJson = item.contentJson)
                            ContentType.FLASHCARD.name -> AIFlashcard(payloadJson = item.contentJson)
                            ContentType.FILE.name -> AIFileCard(payloadJson = item.contentJson)
                            ContentType.CODE.name -> {
                                val lang = try { org.json.JSONObject(item.contentJson).optString("language", "Code") } catch (_: Exception) { "Code" }
                                val code = try { org.json.JSONObject(item.contentJson).optString("code", "") } catch (_: Exception) { "" }
                                val expl = try { org.json.JSONObject(item.contentJson).optString("explanation", "") } catch (_: Exception) { "" }
                                AICodeBlock(language = lang, code = code, explanation = expl)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
