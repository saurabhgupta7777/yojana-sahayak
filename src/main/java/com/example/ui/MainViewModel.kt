package com.example.ui

import android.app.Application
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CitizenCategory
import com.example.data.model.SchemeSector
import com.example.data.model.SchemeTypeFilter
import com.example.data.model.VerificationType
import com.example.data.model.CitizenProfile
import com.example.data.model.CscSlip
import com.example.data.model.RecipientType
import com.example.data.model.Scheme
import com.example.data.model.ChatMessage
import com.example.data.repository.SchemeRepository
import com.example.data.repository.GovDocumentRepository
import com.example.data.model.GovDocument
import com.example.data.util.TtsManager
import com.example.util.VoiceRecognizer
import com.example.util.LanguageOption
import com.example.util.LanguageSelectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppMode {
    STANDARD_SCHEMES,
    DOCUMENTS_DIRECTORY,
    DOC_VALIDITY_SCANNER,
    WHATSAPP_AI,
    CSC_SLIPS,
    ELIGIBILITY_CALCULATOR,
    SAVED_ITEMS,
    QUESTIONNAIRE,
    SCHEME_COLLECTOR_DASHBOARD,
    OFFICE_LOCATOR,
    VOICE_ASSISTANT
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SchemeRepository(application)
    private val govDocumentRepository = GovDocumentRepository()
    val syncManager = com.example.data.sync.SchemeSyncManager(application)
    val ttsManager = TtsManager(application)
    private var voiceRecognizer: VoiceRecognizer? = null

    // UI Navigation State
    private val _currentMode = MutableStateFlow(AppMode.STANDARD_SCHEMES)
    val currentMode: StateFlow<AppMode> = _currentMode.asStateFlow()

    // Filters
    private val _selectedRecipient = MutableStateFlow(RecipientType.ALL)
    val selectedRecipient: StateFlow<RecipientType> = _selectedRecipient.asStateFlow()

    private val _selectedCategory = MutableStateFlow(CitizenCategory.ALL)
    val selectedCategory: StateFlow<CitizenCategory> = _selectedCategory.asStateFlow()

    private val _selectedSector = MutableStateFlow(SchemeSector.ALL)
    val selectedSector: StateFlow<SchemeSector> = _selectedSector.asStateFlow()

    private val _selectedSchemeType = MutableStateFlow(SchemeTypeFilter.ALL)
    val selectedSchemeType: StateFlow<SchemeTypeFilter> = _selectedSchemeType.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("Hindi (हिंदी)")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _selectedSchemeForDetails = MutableStateFlow<Scheme?>(null)
    val selectedSchemeForDetails: StateFlow<Scheme?> = _selectedSchemeForDetails.asStateFlow()

    private val _languageSelectionState = MutableStateFlow(LanguageSelectionState())
    val languageSelectionState: StateFlow<LanguageSelectionState> = _languageSelectionState.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _showVoiceInputDialog = MutableStateFlow(false)
    val showVoiceInputDialog: StateFlow<Boolean> = _showVoiceInputDialog.asStateFlow()

    // Dedicated Voice-Only Assistant Mode State ("केवल बोलकर उपयोग करें")
    private val _voiceAssistantTranscript = MutableStateFlow("")
    val voiceAssistantTranscript: StateFlow<String> = _voiceAssistantTranscript.asStateFlow()

    private val _voiceAssistantResponse = MutableStateFlow("नमस्ते! मैं आपका योजना सहायक हूँ। अपनी समस्या या किसी योजना के बारे में खुलकर बोलें, मैं आपको बोलकर समझाऊंगा।")
    val voiceAssistantResponse: StateFlow<String> = _voiceAssistantResponse.asStateFlow()

    private val _isVoiceAssistantThinking = MutableStateFlow(false)
    val isVoiceAssistantThinking: StateFlow<Boolean> = _isVoiceAssistantThinking.asStateFlow()

    // User Profile for Calculator & Slip
    private val _citizenProfile = MutableStateFlow(CitizenProfile())
    val citizenProfile: StateFlow<CitizenProfile> = _citizenProfile.asStateFlow()

    // Active CSC Slip viewed
    private val _activeSlip = MutableStateFlow<CscSlip?>(null)
    val activeSlip: StateFlow<CscSlip?> = _activeSlip.asStateFlow()

    // Sync notification message
    private val _pibSyncStatus = MutableStateFlow<String?>(null)
    val pibSyncStatus: StateFlow<String?> = _pibSyncStatus.asStateFlow()

    // WhatsApp Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Step-by-Step Questionnaire State
    private val _questionnaireStep = MutableStateFlow(0)
    val questionnaireStep: StateFlow<Int> = _questionnaireStep.asStateFlow()

    private val _questionnaireName = MutableStateFlow("")
    val questionnaireName: StateFlow<String> = _questionnaireName.asStateFlow()

    private val _questionnaireAge = MutableStateFlow(35)
    val questionnaireAge: StateFlow<Int> = _questionnaireAge.asStateFlow()

    private val _questionnaireIncome = MutableStateFlow(120000L)
    val questionnaireIncome: StateFlow<Long> = _questionnaireIncome.asStateFlow()

    private val _questionnaireOccupation = MutableStateFlow("")
    val questionnaireOccupation: StateFlow<String> = _questionnaireOccupation.asStateFlow()

    private val _questionnaireState = MutableStateFlow("All India")
    val questionnaireState: StateFlow<String> = _questionnaireState.asStateFlow()

    private val _questionnaireIsBpl = MutableStateFlow(false)
    val questionnaireIsBpl: StateFlow<Boolean> = _questionnaireIsBpl.asStateFlow()

    private val _isCheckingQuestionnaireEligibility = MutableStateFlow(false)
    val isCheckingQuestionnaireEligibility: StateFlow<Boolean> = _isCheckingQuestionnaireEligibility.asStateFlow()

    private val _questionnaireResult = MutableStateFlow<String?>(null)
    val questionnaireResult: StateFlow<String?> = _questionnaireResult.asStateFlow()

    // DB Flows
    val allSchemes: StateFlow<List<Scheme>> = repository.allSchemes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedSchemes: StateFlow<List<Scheme>> = repository.savedSchemes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedCscSlips: StateFlow<List<CscSlip>> = repository.savedCscSlips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentPibSchemes: StateFlow<List<Scheme>> = repository.recentPibSchemes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter parameters data holder
    private data class FilterParams(
        val recipient: RecipientType,
        val category: CitizenCategory,
        val sector: SchemeSector,
        val schemeType: SchemeTypeFilter,
        val query: String
    )

    private val filterState = combine(
        selectedRecipient,
        selectedCategory,
        selectedSector,
        selectedSchemeType,
        searchQuery
    ) { recipient, category, sector, schemeType, query ->
        FilterParams(recipient, category, sector, schemeType, query)
    }

    // Filtered Schemes combining Search, Category, Sector, SchemeType, and Recipient
    val filteredSchemes: StateFlow<List<Scheme>> = combine(allSchemes, filterState) { schemes, filters ->
        val (recipient, category, sector, schemeType, query) = filters
        schemes.filter { scheme ->
            val matchesRecipient = recipient == RecipientType.ALL || scheme.targetRecipients.isEmpty() || scheme.targetRecipients.contains(recipient)
            val matchesCategory = category == CitizenCategory.ALL || scheme.category == category
            val matchesSector = sector == SchemeSector.ALL || scheme.sector == sector
            val matchesSchemeType = when (schemeType) {
                SchemeTypeFilter.ALL -> true
                SchemeTypeFilter.GOVT -> scheme.verificationType == VerificationType.OFFICIAL_GOVT
                SchemeTypeFilter.CSR -> scheme.verificationType == VerificationType.VERIFIED_CSR
            }
            val matchesQuery = query.isBlank() ||
                    scheme.titleHindi.contains(query, ignoreCase = true) ||
                    scheme.titleEng.contains(query, ignoreCase = true) ||
                    scheme.shortDescriptionHindi.contains(query, ignoreCase = true) ||
                    scheme.detailedDescriptionHindi.contains(query, ignoreCase = true) ||
                    scheme.ministryOrOrganization.contains(query, ignoreCase = true) ||
                    scheme.category.displayNameHindi.contains(query, ignoreCase = true) ||
                    scheme.category.displayNameEng.contains(query, ignoreCase = true) ||
                    scheme.sector.displayNameHindi.contains(query, ignoreCase = true) ||
                    scheme.sector.displayNameEng.contains(query, ignoreCase = true) ||
                    scheme.state.contains(query, ignoreCase = true) ||
                    scheme.maxBenefitAmount.contains(query, ignoreCase = true)

            matchesRecipient && matchesCategory && matchesSector && matchesSchemeType && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializePrepopulatedDataIfEmpty()
            initInitialChatWelcome()

            // Restore saved questionnaire state from Room
            repository.savedQuestionnaire.firstOrNull()?.let { saved ->
                _questionnaireStep.value = saved.currentStep
                _questionnaireName.value = saved.applicantName
                _questionnaireAge.value = saved.age
                _questionnaireIncome.value = saved.income
                _questionnaireOccupation.value = saved.occupation
                _questionnaireState.value = saved.state
                _questionnaireIsBpl.value = saved.isBpl
                _questionnaireResult.value = saved.lastAssessmentResult
                try {
                    _selectedRecipient.value = RecipientType.valueOf(saved.selectedRecipientName)
                } catch (_: Exception) {}
                try {
                    _selectedCategory.value = CitizenCategory.valueOf(saved.selectedCategoryName)
                } catch (_: Exception) {}
            }
        }

        voiceRecognizer = VoiceRecognizer(
            context = application,
            onResult = { text ->
                _isListening.value = false
                submitVoiceQuery(text)
            },
            onError = { _ ->
                _isListening.value = false
            }
        )
    }

    fun getSelectedLanguageCode(): String {
        val lang = _selectedLanguage.value
        return when {
            lang.contains("Hindi") -> "hi-IN"
            lang.contains("Bengali") -> "bn-IN"
            lang.contains("Telugu") -> "te-IN"
            lang.contains("Marathi") -> "mr-IN"
            lang.contains("Tamil") -> "ta-IN"
            lang.contains("Gujarati") -> "gu-IN"
            lang.contains("Kannada") -> "kn-IN"
            lang.contains("Malayalam") -> "ml-IN"
            lang.contains("Punjabi") -> "pa-IN"
            lang.contains("Odia") -> "or-IN"
            lang.contains("Urdu") -> "ur-IN"
            lang.contains("English") -> "en-IN"
            else -> "hi-IN"
        }
    }

    fun getSpeechRecognitionIntent(prompt: String = "योजना या दस्तावेज का नाम बोलें..."): Intent {
        val langCode = getSelectedLanguageCode()
        return voiceRecognizer?.createSpeechIntent(langCode, prompt)
            ?: Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, langCode)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langCode)
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, langCode)
                putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }
    }

    private fun initInitialChatWelcome() {
        val welcomeMsg = ChatMessage(
            id = "welcome_1",
            senderName = "Yojana Sahayak Bot",
            messageText = "Namaste Ji! 🙏 I am your Yojana Sahayak WhatsApp AI Assistant (+91 98765 43210). Ask me anything about PM Kisan, Scholarships, Pension, Women Welfare or Senior Citizen schemes in your native language!",
            isUser = false,
            timestampFormatted = "10:30 AM",
            quickReplies = listOf("🌾 PM Kisan Status?", "🎓 Girl Child Scholarship", "👵 Senior Citizen 70+ Healthcare", "🖨️ Generate CSC Slip")
        )
        _chatMessages.value = listOf(welcomeMsg)
    }

    fun setAppMode(mode: AppMode) {
        _currentMode.value = mode
    }

    fun setRecipient(recipient: RecipientType) {
        _selectedRecipient.value = recipient
        _citizenProfile.value = _citizenProfile.value.copy(recipient = recipient)
    }

    fun setCategory(category: CitizenCategory) {
        _selectedCategory.value = category
        _citizenProfile.value = _citizenProfile.value.copy(category = category)
    }

    fun setSector(sector: SchemeSector) {
        _selectedSector.value = sector
    }

    fun setSchemeType(type: SchemeTypeFilter) {
        _selectedSchemeType.value = type
    }

    fun startVoiceAssistantQuery(spokenText: String) {
        processVoiceAssistantQuery(spokenText)
    }

    fun processVoiceAssistantQuery(userSpokenText: String) {
        val cleanText = userSpokenText.trim()
        if (cleanText.isBlank()) return
        _voiceAssistantTranscript.value = cleanText
        _isVoiceAssistantThinking.value = true

        viewModelScope.launch {
            val response = repository.queryGeminiAssistant(
                userQuery = cleanText,
                language = _selectedLanguage.value,
                recipient = _selectedRecipient.value,
                category = _selectedCategory.value
            )
            _voiceAssistantResponse.value = response
            _isVoiceAssistantThinking.value = false
            ttsManager.speakAloud(response)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedLanguage(lang: String) {
        _selectedLanguage.value = lang
        val option = LanguageSelectionState.fromString(lang)
        _languageSelectionState.value = _languageSelectionState.value.copy(
            selectedLanguage = option,
            isDropdownExpanded = false
        )
        ttsManager.updateLanguage(lang)
    }

    fun setLanguage(lang: String) {
        setSelectedLanguage(lang)
    }

    fun setLanguage(option: LanguageOption) {
        setSelectedLanguage(option.displayName)
    }

    fun toggleLanguageDropdown(expanded: Boolean) {
        _languageSelectionState.value = _languageSelectionState.value.copy(isDropdownExpanded = expanded)
    }

    fun showSchemeDetails(scheme: Scheme) {
        _selectedSchemeForDetails.value = scheme
    }

    fun dismissSchemeDetails() {
        _selectedSchemeForDetails.value = null
    }

    fun openVoiceInputDialog() {
        _showVoiceInputDialog.value = true
    }

    fun dismissVoiceInputDialog() {
        _showVoiceInputDialog.value = false
        _isListening.value = false
    }

    fun submitVoiceQuery(text: String) {
        val cleanText = text.trim()
        if (cleanText.isBlank()) return

        _searchQuery.value = cleanText
        _isListening.value = false
        _showVoiceInputDialog.value = false

        when (_currentMode.value) {
            AppMode.VOICE_ASSISTANT -> {
                _voiceAssistantTranscript.value = cleanText
                processVoiceAssistantQuery(cleanText)
            }
            AppMode.WHATSAPP_AI -> {
                sendWhatsAppMessage(cleanText)
            }
            else -> {
                ttsManager.speakAloud("🎤 $cleanText खोजा जा रहा है")
            }
        }
    }

    fun toggleVoiceInput() {
        if (_isListening.value) {
            voiceRecognizer?.stopListening()
            _isListening.value = false
        } else {
            _isListening.value = true
            val langCode = when {
                _selectedLanguage.value.contains("Hindi") -> "hi-IN"
                _selectedLanguage.value.contains("Bengali") -> "bn-IN"
                _selectedLanguage.value.contains("Telugu") -> "te-IN"
                _selectedLanguage.value.contains("Marathi") -> "mr-IN"
                _selectedLanguage.value.contains("Tamil") -> "ta-IN"
                _selectedLanguage.value.contains("Gujarati") -> "gu-IN"
                else -> "hi-IN"
            }
            if (voiceRecognizer?.isAvailable() == true) {
                voiceRecognizer?.startListening(langCode)
            } else {
                _showVoiceInputDialog.value = true
            }
        }
    }

    fun speakText(text: String) {
        ttsManager.speakAloud(text)
    }

    fun toggleSaveScheme(schemeId: String, currentSaved: Boolean) {
        viewModelScope.launch {
            repository.toggleSaveScheme(schemeId, currentSaved)
        }
    }

    fun cacheScheme(scheme: Scheme) {
        viewModelScope.launch {
            repository.cacheScheme(scheme)
        }
    }

    fun cacheSchemes(schemes: List<Scheme>) {
        viewModelScope.launch {
            repository.cacheSchemes(schemes)
        }
    }

    fun toggleAlertScheme(schemeId: String, isSubscribed: Boolean) {
        viewModelScope.launch {
            repository.toggleAlertScheme(schemeId, isSubscribed)
        }
    }

    fun triggerPibSync() {
        viewModelScope.launch {
            _pibSyncStatus.value = "Syncing live PIB press releases..."
            val count = repository.triggerPibSync()
            _pibSyncStatus.value = "✅ PIB Sync Complete! Discovered $count new official schemes."
        }
    }

    fun updateCitizenProfile(profile: CitizenProfile) {
        _citizenProfile.value = profile
    }

    fun generateCscSlipForScheme(scheme: Scheme, applicantName: String = "") {
        viewModelScope.launch {
            val slip = repository.generateAndSaveCscSlip(
                applicantName = applicantName.ifBlank { "नागरिक आवेदक" },
                profile = _citizenProfile.value,
                matchedSchemes = listOf(scheme)
            )
            _activeSlip.value = slip
            _currentMode.value = AppMode.CSC_SLIPS
        }
    }

    fun generateCscSlipForMultiple(schemes: List<Scheme>, applicantName: String = "") {
        viewModelScope.launch {
            val slip = repository.generateAndSaveCscSlip(
                applicantName = applicantName.ifBlank { "नागरिक आवेदक" },
                profile = _citizenProfile.value,
                matchedSchemes = schemes
            )
            _activeSlip.value = slip
            _currentMode.value = AppMode.CSC_SLIPS
        }
    }

    fun viewCscSlip(slip: CscSlip) {
        _activeSlip.value = slip
        _currentMode.value = AppMode.CSC_SLIPS
    }

    fun sendWhatsAppMessage(userText: String) {
        if (userText.isBlank()) return

        val userMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderName = "You",
            messageText = userText,
            isUser = true,
            timestampFormatted = "Just now"
        )

        _chatMessages.value = _chatMessages.value + userMsg
        _isAiThinking.value = true

        viewModelScope.launch {
            val aiResponse = repository.queryGeminiAssistant(
                userQuery = userText,
                language = _selectedLanguage.value,
                recipient = _selectedRecipient.value,
                category = _selectedCategory.value
            )

            val matchedScheme = allSchemes.value.firstOrNull {
                userText.contains("kisan", ignoreCase = true) && it.id == "pm_kisan" ||
                userText.contains("scholarship", ignoreCase = true) && it.category == CitizenCategory.STUDENTS ||
                userText.contains("senior", ignoreCase = true) && it.id == "ayushman_vaya_vandana" ||
                userText.contains("women", ignoreCase = true) && it.id == "ladli_behna"
            }

            val botMsg = ChatMessage(
                id = "bot_${System.currentTimeMillis()}",
                senderName = "Yojana Sahayak Bot",
                messageText = aiResponse,
                isUser = false,
                timestampFormatted = "Just now",
                matchedScheme = matchedScheme,
                quickReplies = listOf("🖨️ CSC पर्ची बनाएं", "🔊 बोलकर सुनें", "अन्य योजनाएं देखें")
            )

            _isAiThinking.value = false
            _chatMessages.value = _chatMessages.value + botMsg
        }
    }

    // Questionnaire methods & Room Persistence
    private fun persistQuestionnaireToRoom() {
        viewModelScope.launch {
            val entity = com.example.data.local.QuestionnaireResponseEntity(
                id = 1,
                currentStep = _questionnaireStep.value,
                applicantName = _questionnaireName.value,
                age = _questionnaireAge.value,
                income = _questionnaireIncome.value,
                occupation = _questionnaireOccupation.value,
                state = _questionnaireState.value,
                isBpl = _questionnaireIsBpl.value,
                selectedRecipientName = _selectedRecipient.value.name,
                selectedCategoryName = _selectedCategory.value.name,
                lastAssessmentResult = _questionnaireResult.value
            )
            repository.saveQuestionnaireState(entity)
        }
    }

    fun setQuestionnaireStep(step: Int) {
        _questionnaireStep.value = step.coerceIn(0, 4)
        persistQuestionnaireToRoom()
    }

    fun updateQuestionnaireName(name: String) {
        _questionnaireName.value = name
        persistQuestionnaireToRoom()
    }

    fun updateQuestionnaireAge(age: Int) {
        _questionnaireAge.value = age
        _citizenProfile.value = _citizenProfile.value.copy(age = age)
        persistQuestionnaireToRoom()
    }

    fun updateQuestionnaireIncome(income: Long) {
        _questionnaireIncome.value = income
        _citizenProfile.value = _citizenProfile.value.copy(annualIncomeRupees = income)
        persistQuestionnaireToRoom()
    }

    fun updateQuestionnaireOccupation(occupation: String) {
        _questionnaireOccupation.value = occupation
        persistQuestionnaireToRoom()
    }

    fun updateQuestionnaireState(state: String) {
        _questionnaireState.value = state
        _citizenProfile.value = _citizenProfile.value.copy(state = state)
        persistQuestionnaireToRoom()
    }

    fun updateQuestionnaireIsBpl(isBpl: Boolean) {
        _questionnaireIsBpl.value = isBpl
        _citizenProfile.value = _citizenProfile.value.copy(isBplCardHolder = isBpl)
        persistQuestionnaireToRoom()
    }

    fun submitQuestionnaireForGeminiCheck() {
        _isCheckingQuestionnaireEligibility.value = true
        _questionnaireStep.value = 4 // Results step
        persistQuestionnaireToRoom()

        viewModelScope.launch {
            val result = repository.checkQuestionnaireEligibility(
                applicantName = _questionnaireName.value,
                age = _questionnaireAge.value,
                income = _questionnaireIncome.value,
                occupation = _questionnaireOccupation.value,
                state = _questionnaireState.value,
                isBpl = _questionnaireIsBpl.value,
                recipient = _selectedRecipient.value,
                category = _selectedCategory.value,
                language = _selectedLanguage.value
            )
            _questionnaireResult.value = result
            _isCheckingQuestionnaireEligibility.value = false
            persistQuestionnaireToRoom()
        }
    }

    fun resetQuestionnaire() {
        _questionnaireStep.value = 0
        _questionnaireName.value = ""
        _questionnaireAge.value = 35
        _questionnaireIncome.value = 120000L
        _questionnaireOccupation.value = ""
        _questionnaireState.value = "All India"
        _questionnaireIsBpl.value = false
        _questionnaireResult.value = null
        _isCheckingQuestionnaireEligibility.value = false
        viewModelScope.launch {
            repository.clearQuestionnaireState()
        }
    }

    fun getGovernmentDocumentsList(): List<GovDocument> {
        return govDocumentRepository.getDefaultGovernmentDocuments()
    }

    fun fetchLiveDocumentInfo(documentName: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val result = govDocumentRepository.fetchLiveDocumentDetails(documentName)
            onResult(result)
        }
    }

    fun analyzeDocumentValidity(
        docName: String,
        issueDateInput: String,
        imageBase64: String?,
        selectedState: String = "Uttar Pradesh",
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = govDocumentRepository.analyzeDocumentValidity(docName, issueDateInput, imageBase64, selectedState)
            onResult(result)
        }
    }

    fun clearAllSessionData() {
        resetQuestionnaire()
        _chatMessages.value = emptyList()
        _voiceAssistantTranscript.value = ""
        _voiceAssistantResponse.value = "सत्र रीसेट कर दिया गया है। (Session cleared successfully)"
        _searchQuery.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
        voiceRecognizer?.destroy()
    }
}
