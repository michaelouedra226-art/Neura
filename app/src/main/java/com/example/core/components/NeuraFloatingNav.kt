package com.example.core.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.icons.NeuraIcons
import com.example.ui.theme.NeuraCard
import com.example.ui.theme.NeuraCardBorder
import com.example.ui.theme.NeuraElectricBlue
import com.example.ui.theme.NeuraNeonCyan
import com.example.ui.theme.NeuraTextMuted
import com.example.ui.theme.NeuraTextSecondary
import com.example.ui.theme.NeuraVibrantViolet

enum class NeuraTab(val title: String, val icon: ImageVector) {
    CHAT("Accueil", NeuraIcons.Chat),
    HISTORY("Historique", NeuraIcons.Sparkles),
    CREATIONS("Créations", NeuraIcons.Creations),
    MEMORY("Mémoire", NeuraIcons.Memory),
    SETTINGS("Paramètres", NeuraIcons.Settings)
}

@Composable
fun NeuraFloatingNav(
    selectedTab: NeuraTab,
    onTabSelected: (NeuraTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating pill container with subtle glow border
        Row(
            modifier = Modifier
                .background(
                    color = Color(0xF018181B),
                    shape = RoundedCornerShape(32.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            NeuraCardBorder.copy(alpha = 0.8f),
                            NeuraElectricBlue.copy(alpha = 0.4f),
                            NeuraVibrantViolet.copy(alpha = 0.4f),
                            NeuraCardBorder.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeuraTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1.0f,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "scale_${tab.name}"
                )
                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else NeuraTextMuted,
                    label = "color_${tab.name}"
                )

                Box(
                    modifier = Modifier
                        .scale(scale)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(tab) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("nav_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Icon with active background glow
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .then(
                                    if (isSelected) {
                                        Modifier.background(
                                            brush = Brush.linearGradient(
                                                listOf(
                                                    NeuraElectricBlue.copy(alpha = 0.35f),
                                                    NeuraVibrantViolet.copy(alpha = 0.35f)
                                                )
                                            ),
                                            shape = CircleShape
                                        )
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = iconColor,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = tab.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) NeuraNeonCyan else NeuraTextMuted
                        )
                    }
                }
            }
        }
    }
}
