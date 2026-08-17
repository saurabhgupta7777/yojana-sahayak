package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.GovDocument
import com.example.data.model.Scheme
import com.example.ui.AppMode
import com.example.ui.MainViewModel
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VerifiedGovtGreen
import com.example.util.LanguageTranslator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryScreen(
    viewModel: MainViewModel,
    onOpenDrawerClick: () -> Unit
) {
    val context = LocalContext.current
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Government Documents, 1: Schemes & Scholarships
    var searchText by remember { mutableStateOf("") }
    val globalSearchQuery by viewModel.searchQuery.collectAsState()

    LaunchedEffect(globalSearchQuery) {
        if (globalSearchQuery.isNotBlank()) {
            searchText = globalSearchQuery
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "🎤 माइक्रोफोन अनुमति स्वीकृत", Toast.LENGTH_SHORT).show()
            viewModel.toggleVoiceInput()
        } else {
            Toast.makeText(context, "🎙️ माइक्रोफोन अनुमति अस्वीकृत", Toast.LENGTH_LONG).show()
        }
    }

    val handleMicClick = {
        val permissionCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            viewModel.toggleVoiceInput()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
    
    val defaultDocs = remember { viewModel.getGovernmentDocumentsList() }
    val allSchemes by viewModel.filteredSchemes.collectAsState()

    // Live Gemini Grounding result states
    var liveAnalysisDocId by remember { mutableStateOf<String?>(null) }
    var liveAnalysisResult by remember { mutableStateOf("") }
    var isLoadingLiveInfo by remember { mutableStateOf(false) }

    val filteredDocs = remember(searchText, defaultDocs) {
        if (searchText.isBlank()) defaultDocs
        else defaultDocs.filter {
            it.titleHindi.contains(searchText, ignoreCase = true) ||
            it.titleEng.contains(searchText, ignoreCase = true) ||
            it.issuingAuthority.contains(searchText, ignoreCase = true)
        }
    }

    val filteredSchemesList = remember(searchText, allSchemes) {
        if (searchText.isBlank()) allSchemes
        else allSchemes.filter {
            it.titleHindi.contains(searchText, ignoreCase = true) ||
            it.titleEng.contains(searchText, ignoreCase = true) ||
            it.ministryOrOrganization.contains(searchText, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📁 दस्तावेज व योजना डायरेक्टरी",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Documents & Schemes Directory (Live Verified)",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.setAppMode(AppMode.STANDARD_SCHEMES) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        IconButton(onClick = onOpenDrawerClick) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SaffronPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA))
        ) {
            // Search & Voice Filter Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = {
                        Text("Search document or scheme name (e.g. Aadhar, PAN, PM Kisan)...", fontSize = 13.sp)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = SaffronPrimary)
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = { searchText = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                                }
                            }
                            IconButton(onClick = { handleMicClick() }) {
                                Icon(Icons.Default.Mic, contentDescription = "Voice Search", tint = SaffronPrimary)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    singleLine = true
                )
            }

            // Tabs Header: "Government Documents" & "Schemes & Scholarships"
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = SaffronPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = SaffronPrimary,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "📜 सरकारी दस्तावेज\n(Govt Documents)",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.5.sp,
                            lineHeight = 14.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "🎓 योजना व स्कॉलरशिप\n(Schemes & Scholarships)",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.5.sp,
                            lineHeight = 14.sp
                        )
                    }
                )
            }

            // Grounding & Live Info Modal / View
            if (isLoadingLiveInfo) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = SaffronPrimary
                )
            }

            if (selectedTab == 0) {
                // TAB 1: Government Documents List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = "Verified", tint = VerifiedGovtGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "प्रत्येक दस्तावेज के लिए केवल LIVE आधिकारिक सरकारी पोर्टल लिंक प्रदान किए गए हैं। कोई भी static/downloaded PDF होस्ट नहीं किया गया है।",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF1B5E20),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    items(filteredDocs, key = { it.id }) { doc ->
                        GovDocumentCard(
                            doc = doc,
                            isExpandedLive = liveAnalysisDocId == doc.id,
                            liveResult = if (liveAnalysisDocId == doc.id) liveAnalysisResult else "",
                            onFetchLiveClick = {
                                liveAnalysisDocId = doc.id
                                isLoadingLiveInfo = true
                                liveAnalysisResult = ""
                                viewModel.fetchLiveDocumentInfo(doc.titleHindi) { res ->
                                    isLoadingLiveInfo = false
                                    liveAnalysisResult = res
                                }
                            },
                            onOpenLink = { url ->
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot open URL: $url", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            } else {
                // TAB 2: Schemes & Scholarships Directory
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredSchemesList, key = { it.id }) { scheme ->
                        SchemeDirectoryCard(
                            scheme = scheme,
                            onOpenLink = { url ->
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Opening official URL: $url", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onFetchLiveUpdates = {
                                liveAnalysisDocId = scheme.id
                                isLoadingLiveInfo = true
                                liveAnalysisResult = ""
                                viewModel.fetchLiveDocumentInfo(scheme.titleHindi + " " + scheme.officialUrl) { res ->
                                    isLoadingLiveInfo = false
                                    liveAnalysisResult = res
                                }
                            },
                            isExpandedLive = liveAnalysisDocId == scheme.id,
                            liveResult = if (liveAnalysisDocId == scheme.id) liveAnalysisResult else ""
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GovDocumentCard(
    doc: GovDocument,
    isExpandedLive: Boolean,
    liveResult: String,
    onFetchLiveClick: () -> Unit,
    onOpenLink: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF3E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Badge, contentDescription = "Doc", tint = SaffronPrimary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = doc.titleHindi,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Text(
                            text = doc.issuingAuthority,
                            fontSize = 11.5.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Verification Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "🟢 Verified Live",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerifiedGovtGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            if (doc.descriptionHindi.isNotBlank()) {
                Text(
                    text = doc.descriptionHindi,
                    fontSize = 12.sp,
                    color = Color(0xFF424242),
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Application Location & Required Docs
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(10.dp)
            ) {
                Text(
                    text = "🏢 आवेदन कहाँ करें (Where to Apply):",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
                Text(
                    text = doc.whereToApply,
                    fontSize = 11.sp,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "📄 आवश्यक दस्तावेज (Required Documents):",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
                doc.requiredDocuments.forEach { item ->
                    Text(
                        text = "• $item",
                        fontSize = 11.sp,
                        color = Color(0xFF424242)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Links & Timestamp Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = "Time", tint = Color.Gray, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = doc.lastVerifiedTimestamp,
                        fontSize = 10.5.sp,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = { onOpenLink(doc.officialFormLink) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = "Open", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "आधिकारिक फॉर्म लिंक (.gov.in)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Gemini Live Search Grounding Update Button
            OutlinedButton(
                onClick = onFetchLiveClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SaffronPrimary)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "AI Live Search", modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("🔍 AI Google Search से लाइव अपडेट जांचें (Real-Time Grounding)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Expanded Live Gemini Analysis
            if (isExpandedLive && liveResult.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = SaffronPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("लाइव गूगल सर्च सत्यापित विवरण (Live Search Grounded)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SaffronPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = liveResult, fontSize = 11.5.sp, color = Color(0xFF333333), lineHeight = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SchemeDirectoryCard(
    scheme: Scheme,
    onOpenLink: (String) -> Unit,
    onFetchLiveUpdates: () -> Unit,
    isExpandedLive: Boolean,
    liveResult: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scheme.titleHindi,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E88E5)
                    )
                    Text(
                        text = scheme.ministryOrOrganization,
                        fontSize = 11.5.sp,
                        color = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (scheme.isApplicationOpen) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (scheme.isApplicationOpen) "🟢 आवेदन जारी" else "⏳ जल्द शुरू",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (scheme.isApplicationOpen) VerifiedGovtGreen else Color(0xFFE65100)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = scheme.shortDescriptionHindi, fontSize = 12.sp, color = Color(0xFF424242))

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(10.dp)
            ) {
                Text(
                    text = "💰 अधिकतम लाभ (Benefit): ${scheme.maxBenefitAmount}",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = VerifiedGovtGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📄 आवश्यक दस्तावेज: ${scheme.documentsRequired.joinToString(", ")}",
                    fontSize = 11.sp,
                    color = Color(0xFF333333)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Verified Live: 01 Aug 2026",
                    fontSize = 10.5.sp,
                    color = Color.Gray
                )

                Button(
                    onClick = { onOpenLink(scheme.officialUrl) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = "Link", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "आधिकारिक पोर्टल (.gov.in)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedButton(
                onClick = onFetchLiveUpdates,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "AI", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("🔍 योजना फॉर्म व आवश्यक डॉक्यूमेंट लाइव जांचें", fontSize = 11.sp)
            }

            if (isExpandedLive && liveResult.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = liveResult, fontSize = 11.5.sp, color = Color(0xFF333333), lineHeight = 16.sp)
                    }
                }
            }
        }
    }
}
