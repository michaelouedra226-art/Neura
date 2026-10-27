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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.components.NeuraGradientButton
import com.example.core.components.NeuraTab
import com.example.core.components.triggerHaptic
import com.example.core.icons.NeuraIcons
import com.example.ui.theme.NeuraAccentGradient
import com.example.ui.theme.NeuraBackground
import com.example.ui.theme.NeuraCard
import com.example.ui.theme.NeuraCardBorder
import com.example.ui.theme.NeuraElectricBlue
import com.example.ui.theme.NeuraError
import com.example.ui.theme.NeuraNeonCyan
import com.example.ui.theme.NeuraSuccess
import com.example.ui.theme.NeuraTextMuted
import com.example.ui.theme.NeuraTextPrimary
import com.example.ui.theme.NeuraTextSecondary
import com.example.ui.theme.NeuraVibrantViolet
import com.example.ui.viewmodel.NeuraViewModel

@Composable
fun MemoryScreen(
    viewModel: NeuraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val memories by viewModel.memories.collectAsState()
    var selectedCategory by remember { mutableStateOf("TOUTES") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("TOUTES", "Profil", "Préférences", "Objectifs", "Projets", "Méthode")

    val filteredMemories = remember(memories, selectedCategory) {
        if (selectedCategory == "TOUTES") memories
        else memories.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeuraBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Mémoire NEURA",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Stockage local privé et contrôlé",
                    fontSize = 12.sp,
                    color = NeuraTextMuted
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(NeuraAccentGradient)
                    .clickable {
                        triggerHaptic(context)
                        showAddDialog = true
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("add_memory_btn"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = NeuraIcons.Plus,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ajouter",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Test Memory Card Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            NeuraElectricBlue.copy(alpha = 0.2f),
                            NeuraVibrantViolet.copy(alpha = 0.2f)
                        )
                    )
                )
                .border(1.dp, NeuraCardBorder, RoundedCornerShape(18.dp))
                .clickable {
                    triggerHaptic(context)
                    viewModel.setTab(NeuraTab.CHAT)
                    viewModel.sendMessage("Qu'est-ce que tu sais sur moi ?")
                }
                .padding(14.dp)
                .testTag("test_memory_banner")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(NeuraNeonCyan.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = NeuraIcons.Brain,
                            contentDescription = null,
                            tint = NeuraNeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Tester l'intelligence de mémoire",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Demande à NEURA : 'Qu'est-ce que tu sais sur moi ?'",
                            fontSize = 11.5.sp,
                            color = NeuraTextSecondary
                        )
                    }
                }
                Icon(
                    imageVector = NeuraIcons.ArrowForward,
                    contentDescription = null,
                    tint = NeuraNeonCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = cat == selectedCategory
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
                            selectedCategory = cat
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("mem_cat_$cat"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) Color.White else NeuraTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Memory Items List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredMemories, key = { it.id }) { mem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF161622))
                        .border(1.dp, NeuraCardBorder.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        .padding(14.dp)
                        .testTag("memory_item_${mem.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = mem.key,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        when (mem.category.lowercase()) {
                                            "profil" -> NeuraNeonCyan.copy(alpha = 0.2f)
                                            "préférences" -> NeuraVibrantViolet.copy(alpha = 0.2f)
                                            "objectifs" -> NeuraSuccess.copy(alpha = 0.2f)
                                            else -> Color(0xFF282838)
                                        },
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = mem.category,
                                    fontSize = 10.sp,
                                    color = when (mem.category.lowercase()) {
                                        "profil" -> NeuraNeonCyan
                                        "préférences" -> NeuraVibrantViolet
                                        "objectifs" -> NeuraSuccess
                                        else -> NeuraTextSecondary
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = mem.value,
                            fontSize = 13.sp,
                            color = NeuraTextSecondary,
                            lineHeight = 18.sp
                        )
                    }

                    IconButton(
                        onClick = {
                            triggerHaptic(context)
                            viewModel.deleteMemory(mem.id)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = NeuraIcons.Trash,
                            contentDescription = "Supprimer",
                            tint = NeuraError.copy(alpha = 0.6f),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Add Memory Dialog
    if (showAddDialog) {
        var newKey by remember { mutableStateOf("") }
        var newValue by remember { mutableStateOf("") }
        var newCategory by remember { mutableStateOf("Préférences") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF161622),
            title = {
                Text(
                    text = "Nouvelle information mémorisée",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newKey,
                        onValueChange = { newKey = it },
                        label = { Text("Titre / Sujet", color = NeuraTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeuraNeonCyan,
                            unfocusedBorderColor = NeuraCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newValue,
                        onValueChange = { newValue = it },
                        label = { Text("Information à retenir", color = NeuraTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeuraNeonCyan,
                            unfocusedBorderColor = NeuraCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newCategory,
                        onValueChange = { newCategory = it },
                        label = { Text("Catégorie (Profil, Objectifs, etc.)", color = NeuraTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeuraNeonCyan,
                            unfocusedBorderColor = NeuraCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                NeuraGradientButton(
                    text = "Enregistrer",
                    onClick = {
                        if (newKey.isNotBlank() && newValue.isNotBlank()) {
                            viewModel.addMemory(newKey, newValue, newCategory)
                            showAddDialog = false
                        }
                    },
                    testTag = "confirm_add_memory_btn"
                )
            },
            dismissButton = {
                Text(
                    text = "Annuler",
                    color = NeuraTextMuted,
                    modifier = Modifier
                        .clickable { showAddDialog = false }
                        .padding(12.dp)
                )
            }
        )
    }
}
