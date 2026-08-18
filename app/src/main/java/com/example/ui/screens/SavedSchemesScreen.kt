package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CitizenCategory
import com.example.data.model.Scheme
import com.example.ui.MainViewModel
import com.example.ui.components.SchemeCard
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyLight
import com.example.ui.theme.SaffronPrimary
import com.example.util.LanguageTranslator

@Composable
fun SavedSchemesScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val savedSchemes by viewModel.savedSchemes.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<CitizenCategory?>(null) }

    val filteredSavedSchemes = remember(savedSchemes, searchQuery, selectedCategoryFilter) {
        savedSchemes.filter { scheme ->
            val matchesQuery = searchQuery.isBlank() ||
                    scheme.titleHindi.contains(searchQuery, ignoreCase = true) ||
                    scheme.titleEng.contains(searchQuery, ignoreCase = true) ||
                    scheme.ministryOrOrganization.contains(searchQuery, ignoreCase = true) ||
                    scheme.shortDescriptionHindi.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategoryFilter == null || scheme.category == selectedCategoryFilter

            matchesQuery && matchesCategory
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = EmeraldGreen,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "🔖 ${LanguageTranslator.getLocalizedText("nav_saved", selectedLanguage)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${savedSchemes.size} ${LanguageTranslator.getLocalizedText("all_schemes", selectedLanguage)}",
                                fontSize = 11.5.sp,
                                color = Color(0xFFC8E6C9)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Box inside header
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(LanguageTranslator.getLocalizedText("search_schemes", selectedLanguage), fontSize = 13.sp, color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = EmeraldGreen) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("saved_schemes_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
            }
        }

        // Category Filter Chips
        if (savedSchemes.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null },
                        label = { Text("${LanguageTranslator.getLocalizedCategory(CitizenCategory.ALL, selectedLanguage)} (${savedSchemes.size})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                items(CitizenCategory.entries.filter { cat -> cat != CitizenCategory.ALL }) { cat ->
                    val count = savedSchemes.count { it.category == cat }
                    if (count > 0) {
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = {
                                selectedCategoryFilter = if (selectedCategoryFilter == cat) null else cat
                            },
                            label = { Text("${cat.icon} ${LanguageTranslator.getLocalizedCategory(cat, selectedLanguage)} ($count)", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Main List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (savedSchemes.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = SaffronPrimary.copy(alpha = 0.1f),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = null,
                                        tint = SaffronPrimary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "अभी कोई योजना सेव नहीं की गई है",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDeep
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "मुख्य पृष्ठ पर किसी भी योजना कार्ड पर 🔖 बुकमार्क बटन दबाएं। वह योजना यहाँ आपकी सुविधा के लिए सुरक्षित रहेगी।",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 8.dp),
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = onBackClick,
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("🏠 मुख्य योजनाएं देखें", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else if (filteredSavedSchemes.isEmpty()) {
                item {
                    Text(
                        text = "खोज के अनुसार कोई सेव की गई योजना नहीं मिली।",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(filteredSavedSchemes, key = { it.id }) { scheme ->
                    SchemeCard(
                        scheme = scheme,
                        selectedLanguage = selectedLanguage,
                        onReadAloudClick = { viewModel.speakText(it) },
                        onGenerateCscSlipClick = { viewModel.showSchemeDetails(it) },
                        onToggleSaveClick = { id, saved -> viewModel.toggleSaveScheme(id, saved) },
                        onToggleAlertClick = { id, sub -> viewModel.toggleAlertScheme(id, sub) },
                        onSchemeClick = { viewModel.showSchemeDetails(it) }
                    )
                }
            }
        }
    }
}
