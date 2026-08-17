package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppMode
import com.example.ui.MainViewModel
import com.example.ui.components.AppInfoImageSlider
import com.example.ui.components.CategoryFilterChips
import com.example.ui.components.SearchBarWithVoice
import com.example.ui.components.SectorFilterChips
import com.example.ui.components.SchemeCard
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SaffronPrimary
import com.example.util.LanguageTranslator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Primary Home Screen Composable displaying all government schemes, yojnas,
 * and scholarships with high performance and streamlined non-duplicated filters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val filteredSchemes by viewModel.filteredSchemes.collectAsState()
    val allSchemesList by viewModel.allSchemes.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedSector by viewModel.selectedSector.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val isListening by viewModel.isListening.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF6EF))
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.triggerPibSync()
                coroutineScope.launch {
                    delay(1000)
                    isRefreshing = false
                }
            },
            state = pullToRefreshState,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. App Info Carousel Banner
                item {
                    AppInfoImageSlider(
                        onNavigateMode = { viewModel.setAppMode(it) },
                        onVoiceActionClick = { viewModel.setAppMode(AppMode.VOICE_ASSISTANT) }
                    )
                }

                // 2. Search Bar with Voice Input 🎤 & Language Dropdown
                item {
                    SearchBarWithVoice(
                        searchQuery = searchQuery,
                        onQueryChange = { viewModel.setSearchQuery(it) },
                        selectedLanguage = selectedLanguage,
                        onLanguageSelected = { viewModel.setSelectedLanguage(it) },
                        isListening = isListening,
                        onMicClick = { viewModel.openVoiceInputDialog() },
                        onSearchSubmit = { }
                    )
                }

                // 3. Category Filter Chips (लाभार्थी श्रेणी)
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = LanguageTranslator.getLocalizedText("citizen_category", selectedLanguage),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3748)
                            )
                            if (searchQuery.isNotEmpty() || selectedCategory != com.example.data.model.CitizenCategory.ALL || selectedSector != com.example.data.model.SchemeSector.ALL) {
                                Text(
                                    text = LanguageTranslator.getLocalizedText("filter_reset", selectedLanguage),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronPrimary,
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.setSearchQuery("")
                                            viewModel.setCategory(com.example.data.model.CitizenCategory.ALL)
                                            viewModel.setSector(com.example.data.model.SchemeSector.ALL)
                                            viewModel.setRecipient(com.example.data.model.RecipientType.ALL)
                                        }
                                        .padding(4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        CategoryFilterChips(
                            selectedCategory = selectedCategory,
                            onCategorySelected = { viewModel.setCategory(it) },
                            selectedLanguage = selectedLanguage
                        )
                    }
                }

                // 4. Sector Filter Chips (योजना क्षेत्र / स्कॉलरशिप / कृषि / स्वास्थ्य आदि)
                item {
                    Column {
                        Text(
                            text = LanguageTranslator.getLocalizedText("scheme_sector", selectedLanguage),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D3748),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        SectorFilterChips(
                            selectedSector = selectedSector,
                            onSectorSelected = { viewModel.setSector(it) },
                            selectedLanguage = selectedLanguage
                        )
                    }
                }

                // 5. Section Header & Count Indicator
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${LanguageTranslator.getLocalizedText("all_schemes", selectedLanguage)} (${filteredSchemes.size})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldGreen
                        )

                        Text(
                            text = LanguageTranslator.getLocalizedText("central_state", selectedLanguage),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF718096)
                        )
                    }
                }

                // 6. Schemes List
                if (filteredSchemes.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🔍", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = LanguageTranslator.getLocalizedText("no_schemes", selectedLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF2D3748)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = LanguageTranslator.getLocalizedText("no_schemes_sub", selectedLanguage),
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                } else {
                    items(filteredSchemes, key = { it.id }) { scheme ->
                        SchemeCard(
                            scheme = scheme,
                            selectedLanguage = selectedLanguage,
                            onReadAloudClick = { viewModel.speakText(it) },
                            onGenerateCscSlipClick = { viewModel.generateCscSlipForScheme(it) },
                            onToggleSaveClick = { id, saved -> viewModel.toggleSaveScheme(id, saved) },
                            onToggleAlertClick = { id, subscribed -> viewModel.toggleAlertScheme(id, subscribed) },
                            onSchemeClick = { viewModel.showSchemeDetails(it) }
                        )
                    }
                }
            }
        }

        // Floating Action Button on Bottom-Right Side for "केवल बोलकर उपयोग करें"
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 20.dp)
        ) {
            Card(
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2A4A)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF4A02F)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .clickable { viewModel.setAppMode(AppMode.VOICE_ASSISTANT) }
                    .testTag("floating_voice_assistant_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF4A02F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "बोलकर उपयोग करें",
                            tint = Color(0xFF1B2A4A),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🎙️ ${LanguageTranslator.getLocalizedText("read_aloud", selectedLanguage)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
