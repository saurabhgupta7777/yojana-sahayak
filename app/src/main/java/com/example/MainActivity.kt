package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppMode
import com.example.ui.MainViewModel
import com.example.ui.screens.CscSlipScreen
import com.example.ui.screens.EligibilityCalculatorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.QuestionnaireScreen
import com.example.ui.screens.SavedSchemesScreen
import com.example.ui.screens.WhatsAppChatScreen
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.YojanaSahayakTheme

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.IconButton
import androidx.compose.material3.Divider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.filled.Mic
import com.example.ui.screens.DirectoryScreen
import com.example.ui.screens.VoiceAssistantScreen
import com.example.ui.screens.DocValidityScannerScreen
import com.example.util.LanguageTranslator
import com.example.util.ProvideAppLanguage
import com.example.util.LocalAppLanguage
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Schedule daily recurring WorkManager task for government scheme data sync
        try {
            com.example.data.sync.SchemeSyncWorker.scheduleDailySync(this)
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error scheduling WorkManager sync: ${e.message}", e)
        }

        setContent {
            ProvideAppLanguage(viewModel = viewModel) {
                val selectedLanguage = LocalAppLanguage.current
                androidx.compose.runtime.LaunchedEffect(selectedLanguage) {
                    val option = com.example.util.LanguageSelectionState.fromString(selectedLanguage)
                    val locale = java.util.Locale(option.code)
                    java.util.Locale.setDefault(locale)
                    val config = resources.configuration
                    config.setLocale(locale)
                    @Suppress("DEPRECATION")
                    resources.updateConfiguration(config, resources.displayMetrics)
                }

            YojanaSahayakTheme {
                val currentMode by viewModel.currentMode.collectAsState()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var showPrivacyPolicyDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                val selectedSchemeForDetails by viewModel.selectedSchemeForDetails.collectAsState()
                val showVoiceInputDialog by viewModel.showVoiceInputDialog.collectAsState()
                val context = androidx.compose.ui.platform.LocalContext.current
                var backPressedTime by androidx.compose.runtime.remember { androidx.compose.runtime.mutableLongStateOf(0L) }

                androidx.activity.compose.BackHandler(enabled = true) {
                    when {
                        drawerState.isOpen -> {
                            scope.launch { drawerState.close() }
                        }
                        showPrivacyPolicyDialog -> {
                            showPrivacyPolicyDialog = false
                        }
                        selectedSchemeForDetails != null -> {
                            viewModel.dismissSchemeDetails()
                        }
                        showVoiceInputDialog -> {
                            viewModel.dismissVoiceInputDialog()
                        }
                        currentMode != AppMode.STANDARD_SCHEMES -> {
                            viewModel.setAppMode(AppMode.STANDARD_SCHEMES)
                        }
                        else -> {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - backPressedTime < 2000) {
                                (context as? android.app.Activity)?.finish()
                            } else {
                                backPressedTime = currentTime
                                android.widget.Toast.makeText(
                                    context,
                                    "ऐप से बाहर निकलने के लिए फिर से बैक दबाएं (Press back again to exit)",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                if (showPrivacyPolicyDialog) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { showPrivacyPolicyDialog = false },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = "Privacy Policy", tint = EmeraldGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("🔒 गोपनीयता नीति (Privacy Policy)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("🇮🇳 **योजना सहायक डेटा सुरक्षा एवं निजता प्रतिबद्धता**", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("• **न्यूनतम डेटा संग्रह**: हम केवल ऐप को सुचारू रूप से चलाने के लिए आवश्यक डेटा का उपयोग करते हैं।", fontSize = 12.5.sp)
                                Text("• **कोई सर्वर स्टोरेज नहीं**: आपकी कोई भी संवेदनशील जानकारी (जैसे नाम, आय विवरण, आधार/पैन नंबर) किसी बाहरी सर्वर पर संग्रहीत नहीं की जाती।", fontSize = 12.5.sp)
                                Text("• **अस्थायी फ़ोटो प्रसंस्करण**: दस्तावेज़ वैधता जांच के लिए अपलोड की गई फ़ोटो केवल AI विश्लेषण के समय उपयोग की जाती हैं और तुरंत हटा दी जाती हैं।", fontSize = 12.5.sp)
                                Text("• **सुरक्षित स्थानीय संग्रहण**: सेव की गई योजनाएं और CSC पर्चियां आपके डिवाइस में ही local Room Database द्वारा सुरक्षित रहती हैं।", fontSize = 12.5.sp)
                            }
                        },
                        confirmButton = {
                            androidx.compose.material3.TextButton(onClick = { showPrivacyPolicyDialog = false }) {
                                Text("ठीक है (OK)", fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            }
                        }
                    )
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerContainerColor = Color.White,
                            modifier = Modifier.width(300.dp)
                        ) {
                            // Sidebar Header
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(EmeraldGreen)
                                    .padding(20.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🇮🇳", fontSize = 28.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "योजना व दस्तावेज सहायक",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Official Govt Schemes & Doc Directory",
                                        fontSize = 11.sp,
                                        color = Color(0xFFC8E6C9)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Navigation Items
                            val drawerColors = NavigationDrawerItemDefaults.colors(
                                unselectedTextColor = Color.Black,
                                selectedTextColor = Color.Black
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Mic, contentDescription = "Voice Assistant", tint = Color(0xFF3182CE)) },
                                label = { Text(LanguageTranslator.getLocalizedText("drawer_voice", selectedLanguage), fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color.Black) },
                                selected = currentMode == AppMode.VOICE_ASSISTANT,
                                colors = drawerColors,
                                onClick = {
                                    viewModel.setAppMode(AppMode.VOICE_ASSISTANT)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = EmeraldGreen) },
                                label = { Text(LanguageTranslator.getLocalizedText("drawer_home", selectedLanguage), fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color.Black) },
                                selected = currentMode == AppMode.STANDARD_SCHEMES,
                                colors = drawerColors,
                                onClick = {
                                    viewModel.setAppMode(AppMode.STANDARD_SCHEMES)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Folder, contentDescription = "Directory", tint = SaffronPrimary) },
                                label = { Text(LanguageTranslator.getLocalizedText("drawer_directory", selectedLanguage), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black) },
                                selected = currentMode == AppMode.DOCUMENTS_DIRECTORY,
                                colors = drawerColors,
                                onClick = {
                                    viewModel.setAppMode(AppMode.DOCUMENTS_DIRECTORY)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.CameraAlt, contentDescription = "Scanner", tint = SaffronPrimary) },
                                label = { Text(LanguageTranslator.getLocalizedText("drawer_scanner", selectedLanguage), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black) },
                                selected = currentMode == AppMode.DOC_VALIDITY_SCANNER,
                                colors = drawerColors,
                                onClick = {
                                    viewModel.setAppMode(AppMode.DOC_VALIDITY_SCANNER)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculator", tint = EmeraldGreen) },
                                label = { Text(LanguageTranslator.getLocalizedText("drawer_calculator", selectedLanguage), fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Color.Black) },
                                selected = currentMode == AppMode.ELIGIBILITY_CALCULATOR || currentMode == AppMode.QUESTIONNAIRE,
                                colors = drawerColors,
                                onClick = {
                                    viewModel.setAppMode(AppMode.ELIGIBILITY_CALCULATOR)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = WhatsAppGreen) },
                                label = { Text(LanguageTranslator.getLocalizedText("drawer_whatsapp", selectedLanguage), fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Color.Black) },
                                selected = currentMode == AppMode.WHATSAPP_AI,
                                colors = drawerColors,
                                onClick = {
                                    viewModel.setAppMode(AppMode.WHATSAPP_AI)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Bookmark, contentDescription = "Saved", tint = EmeraldGreen) },
                                label = { Text(LanguageTranslator.getLocalizedText("drawer_saved", selectedLanguage), fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color.Black) },
                                selected = currentMode == AppMode.SAVED_ITEMS || currentMode == AppMode.CSC_SLIPS,
                                colors = drawerColors,
                                onClick = {
                                    viewModel.setAppMode(AppMode.SAVED_ITEMS)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.Place, contentDescription = "Office Locator", tint = EmeraldGreen) },
                                label = { Text(LanguageTranslator.getLocalizedText("drawer_locator", selectedLanguage), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black) },
                                selected = currentMode == AppMode.OFFICE_LOCATOR,
                                colors = drawerColors,
                                onClick = {
                                    viewModel.setAppMode(AppMode.OFFICE_LOCATOR)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )

                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.VerifiedUser, contentDescription = "Privacy Policy", tint = EmeraldGreen) },
                                label = { Text(LanguageTranslator.getLocalizedText("drawer_privacy", selectedLanguage), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black) },
                                selected = false,
                                colors = drawerColors,
                                onClick = {
                                    showPrivacyPolicyDialog = true
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            if (selectedSchemeForDetails == null && currentMode != AppMode.WHATSAPP_AI && currentMode != AppMode.CSC_SLIPS && currentMode != AppMode.QUESTIONNAIRE && currentMode != AppMode.DOCUMENTS_DIRECTORY && currentMode != AppMode.DOC_VALIDITY_SCANNER && currentMode != AppMode.SCHEME_COLLECTOR_DASHBOARD && currentMode != AppMode.OFFICE_LOCATOR && currentMode != AppMode.VOICE_ASSISTANT) {
                                TopHeaderBar(
                                    currentMode = currentMode,
                                    selectedLanguage = selectedLanguage,
                                    onModeToggle = { mode -> viewModel.setAppMode(mode) },
                                    onOpenDrawer = { scope.launch { drawerState.open() } }
                                )
                            }
                        },
                        bottomBar = {
                            if (selectedSchemeForDetails == null && currentMode != AppMode.DOC_VALIDITY_SCANNER && currentMode != AppMode.SCHEME_COLLECTOR_DASHBOARD && currentMode != AppMode.OFFICE_LOCATOR && currentMode != AppMode.VOICE_ASSISTANT) {
                                BottomNavBar(
                                    currentMode = currentMode,
                                    selectedLanguage = selectedLanguage,
                                    onModeSelected = { mode -> viewModel.setAppMode(mode) }
                                )
                            }
                        },
                        contentWindowInsets = WindowInsets.safeDrawing
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = selectedSchemeForDetails,
                                transitionSpec = {
                                    if (targetState != null) {
                                        // Forward Navigation (Scheme List -> Detailed Scheme View)
                                        (slideInHorizontally(
                                            initialOffsetX = { fullWidth -> fullWidth },
                                            animationSpec = spring(
                                                dampingRatio = 0.85f,
                                                stiffness = Spring.StiffnessMediumLow
                                            )
                                        ) + fadeIn(animationSpec = tween(280))).togetherWith(
                                            slideOutHorizontally(
                                                targetOffsetX = { fullWidth -> -fullWidth / 4 },
                                                animationSpec = tween(280)
                                            ) + fadeOut(animationSpec = tween(180))
                                        )
                                    } else {
                                        // Backward Navigation (Detailed Scheme View -> Scheme List)
                                        (slideInHorizontally(
                                            initialOffsetX = { fullWidth -> -fullWidth / 4 },
                                            animationSpec = spring(
                                                dampingRatio = 0.85f,
                                                stiffness = Spring.StiffnessMediumLow
                                            )
                                        ) + fadeIn(animationSpec = tween(280))).togetherWith(
                                            slideOutHorizontally(
                                                targetOffsetX = { fullWidth -> fullWidth },
                                                animationSpec = spring(
                                                    dampingRatio = 0.85f,
                                                    stiffness = Spring.StiffnessMediumLow
                                                )
                                            ) + fadeOut(animationSpec = tween(180))
                                        )
                                    }
                                },
                                label = "scheme_list_detail_navigation_transition"
                            ) { scheme ->
                                if (scheme != null) {
                                    com.example.ui.screens.SchemeDetailScreen(
                                        scheme = scheme,
                                        selectedLanguage = selectedLanguage,
                                        onBackClick = { viewModel.dismissSchemeDetails() },
                                        onReadAloudClick = { viewModel.speakText(it) },
                                        onGenerateCscSlipClick = {
                                            viewModel.generateCscSlipForScheme(it)
                                            viewModel.dismissSchemeDetails()
                                        },
                                        onToggleSaveClick = { id, saved -> viewModel.toggleSaveScheme(id, saved) }
                                    )
                                } else {
                                    when (currentMode) {
                                        AppMode.STANDARD_SCHEMES -> HomeScreen(viewModel = viewModel)
                                        AppMode.DOCUMENTS_DIRECTORY -> DirectoryScreen(
                                            viewModel = viewModel,
                                            onOpenDrawerClick = { scope.launch { drawerState.open() } }
                                        )
                                        AppMode.DOC_VALIDITY_SCANNER -> DocValidityScannerScreen(
                                            viewModel = viewModel,
                                            onOpenDrawerClick = { scope.launch { drawerState.open() } }
                                        )
                                        AppMode.WHATSAPP_AI -> WhatsAppChatScreen(
                                            viewModel = viewModel,
                                            onBackClick = { viewModel.setAppMode(AppMode.STANDARD_SCHEMES) }
                                        )
                                        AppMode.CSC_SLIPS, AppMode.SAVED_ITEMS -> SavedSchemesScreen(
                                            viewModel = viewModel,
                                            onBackClick = { viewModel.setAppMode(AppMode.STANDARD_SCHEMES) }
                                        )
                                        AppMode.ELIGIBILITY_CALCULATOR -> EligibilityCalculatorScreen(
                                            viewModel = viewModel,
                                            onBackClick = { viewModel.setAppMode(AppMode.STANDARD_SCHEMES) }
                                        )
                                        AppMode.QUESTIONNAIRE -> QuestionnaireScreen(
                                            viewModel = viewModel,
                                            onBackClick = { viewModel.setAppMode(AppMode.STANDARD_SCHEMES) }
                                        )
                                        AppMode.SCHEME_COLLECTOR_DASHBOARD -> com.example.ui.screens.SchemeCollectorDashboardScreen(
                                            viewModel = viewModel,
                                            onBackClick = { viewModel.setAppMode(AppMode.STANDARD_SCHEMES) }
                                        )
                                        AppMode.OFFICE_LOCATOR -> com.example.ui.screens.OfficeLocatorScreen(
                                            viewModel = viewModel,
                                            onBackClick = { viewModel.setAppMode(AppMode.STANDARD_SCHEMES) }
                                        )
                                        AppMode.VOICE_ASSISTANT -> VoiceAssistantScreen(
                                            viewModel = viewModel,
                                            onBack = { viewModel.setAppMode(AppMode.STANDARD_SCHEMES) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Render Voice Input Dialog if active
                    val isListeningState by viewModel.isListening.collectAsState()
                    if (showVoiceInputDialog) {
                        com.example.ui.components.VoiceInputDialog(
                            selectedLanguage = selectedLanguage,
                            isListening = isListeningState,
                            onDismissRequest = { viewModel.dismissVoiceInputDialog() },
                            onSubmitQuery = { query -> viewModel.submitVoiceQuery(query) },
                            onRetryVoice = { viewModel.toggleVoiceInput() }
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
fun TopHeaderBar(
    currentMode: AppMode,
    selectedLanguage: String = LocalAppLanguage.current,
    onModeToggle: (AppMode) -> Unit,
    onOpenDrawer: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(EmeraldGreen)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hamburger Menu & App Identity Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onOpenDrawer) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu Drawer", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "🇮🇳", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = LanguageTranslator.getLocalizedText("app_name", selectedLanguage),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Sarkari & CSR Assistant",
                        fontSize = 10.5.sp,
                        color = Color(0xFFC8E6C9)
                    )
                }
            }

            // Yojana Sahayak AI Mode Switch Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(WhatsAppGreen)
                    .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                    .clickable {
                        val newMode = if (currentMode == AppMode.WHATSAPP_AI) AppMode.STANDARD_SCHEMES else AppMode.WHATSAPP_AI
                        onModeToggle(newMode)
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("whatsapp_mode_header_toggle")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Yojana Sahayak AI",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "💬 ${LanguageTranslator.getLocalizedText("nav_ai_assistant", selectedLanguage)}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(
    currentMode: AppMode,
    selectedLanguage: String = LocalAppLanguage.current,
    onModeSelected: (AppMode) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentMode == AppMode.STANDARD_SCHEMES,
            onClick = { onModeSelected(AppMode.STANDARD_SCHEMES) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text(LanguageTranslator.getLocalizedText("nav_schemes", selectedLanguage), fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldGreen,
                selectedTextColor = EmeraldGreen,
                indicatorColor = Color(0xFFC8E6C9)
            ),
            modifier = Modifier.testTag("nav_home")
        )

        NavigationBarItem(
            selected = currentMode == AppMode.DOCUMENTS_DIRECTORY,
            onClick = { onModeSelected(AppMode.DOCUMENTS_DIRECTORY) },
            icon = { Icon(Icons.Default.Folder, contentDescription = "Documents") },
            label = { Text(LanguageTranslator.getLocalizedText("nav_directory", selectedLanguage), fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SaffronPrimary,
                selectedTextColor = SaffronPrimary,
                indicatorColor = Color(0xFFFFE0B2)
            ),
            modifier = Modifier.testTag("nav_directory")
        )

        NavigationBarItem(
            selected = currentMode == AppMode.WHATSAPP_AI,
            onClick = { onModeSelected(AppMode.WHATSAPP_AI) },
            icon = { Icon(Icons.Default.Chat, contentDescription = "Yojana Sahayak AI Bot") },
            label = { Text(LanguageTranslator.getLocalizedText("nav_ai_assistant", selectedLanguage), fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = WhatsAppGreen,
                selectedTextColor = WhatsAppGreen,
                indicatorColor = Color(0xFFE8F5E9)
            ),
            modifier = Modifier.testTag("nav_whatsapp")
        )

        NavigationBarItem(
            selected = currentMode == AppMode.ELIGIBILITY_CALCULATOR || currentMode == AppMode.QUESTIONNAIRE,
            onClick = { onModeSelected(AppMode.ELIGIBILITY_CALCULATOR) },
            icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculator") },
            label = { Text(LanguageTranslator.getLocalizedText("nav_eligibility", selectedLanguage), fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SaffronPrimary,
                selectedTextColor = SaffronPrimary,
                indicatorColor = Color(0xFFFFE0B2)
            ),
            modifier = Modifier.testTag("nav_calculator")
        )

        NavigationBarItem(
            selected = currentMode == AppMode.SAVED_ITEMS || currentMode == AppMode.CSC_SLIPS,
            onClick = { onModeSelected(AppMode.SAVED_ITEMS) },
            icon = { Icon(Icons.Default.Bookmark, contentDescription = "Saved Schemes") },
            label = { Text(LanguageTranslator.getLocalizedText("nav_saved", selectedLanguage), fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldGreen,
                selectedTextColor = EmeraldGreen,
                indicatorColor = Color(0xFFC8E6C9)
            ),
            modifier = Modifier.testTag("nav_saved")
        )
    }
}
