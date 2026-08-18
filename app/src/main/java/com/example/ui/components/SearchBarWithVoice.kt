package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SaffronPrimary

@Composable
fun SearchBarWithVoice(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    isListening: Boolean,
    onMicClick: () -> Unit,
    onSearchSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var languageMenuExpanded by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val micPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micPulse"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF4A02F))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language selector dropdown button
                Box {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFFFF3E0))
                            .border(1.dp, SaffronPrimary, RoundedCornerShape(14.dp))
                            .clickable { languageMenuExpanded = true }
                            .padding(horizontal = 10.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = SaffronPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = com.example.util.LanguageSelectionState.fromString(selectedLanguage).displayName.split(" ").first(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = languageMenuExpanded,
                        onDismissRequest = { languageMenuExpanded = false }
                    ) {
                        com.example.util.LanguageSelectionState.SUPPORTED_LANGUAGES.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = option.flagEmoji, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = option.displayName,
                                            fontSize = 13.sp,
                                            fontWeight = if (option.displayName == selectedLanguage) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                onClick = {
                                    onLanguageSelected(option.displayName)
                                    languageMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Main Outlined Text Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_text_input"),
                    placeholder = {
                        Text(
                            text = "योजना या स्कॉलरशिप खोजें...",
                            fontSize = 12.5.sp,
                            color = Color(0xFF718096)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF1B2A4A)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFFAF6EF),
                        unfocusedContainerColor = Color(0xFFFAF6EF),
                        focusedBorderColor = Color(0xFF1B2A4A),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Microphone Button 🎤
                Box(
                    modifier = Modifier
                        .scale(if (isListening) micPulseScale else 1.0f)
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(if (isListening) Color(0xFFE53E3E) else Color(0xFF1B2A4A))
                        .clickable { onMicClick() }
                        .testTag("mic_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Input Mic",
                        tint = Color(0xFFF4A02F),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        if (isListening) {
            Text(
                text = "🎤 सुन रहे हैं... (Listening)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE53E3E),
                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
            )
        }
    }
}
