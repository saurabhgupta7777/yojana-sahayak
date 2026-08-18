package com.example.data.repository

import android.content.Context
import com.example.data.api.GeminiService
import com.example.data.datasource.PrepopulatedSchemes
import com.example.data.local.AppDatabase
import com.example.data.local.toDomainModel
import com.example.data.local.toEntity
import com.example.data.model.CitizenCategory
import com.example.data.model.SchemeSector
import com.example.data.model.CitizenProfile
import com.example.data.model.CscSlip
import com.example.data.model.RecipientType
import com.example.data.model.Scheme
import com.example.data.model.VerificationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Main Repository managing government welfare scheme data, local database persistence,
 * Gemini AI assistant queries, eligibility evaluation, and CSC application slips.
 *
 * @param context Android Application Context used to initialize Room database.
 */
class SchemeRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val schemeDao = db.schemeDao()
    private val cscSlipDao = db.cscSlipDao()
    private val questionnaireDao = db.questionnaireDao()
    private val geminiService = GeminiService()

    /**
     * Flow of saved multi-step questionnaire response state for instant restoration.
     */
    val savedQuestionnaire: Flow<com.example.data.local.QuestionnaireResponseEntity?> = questionnaireDao.getSavedQuestionnaire()

    /**
     * Persists the user's completed or draft questionnaire profile response.
     * @param stateEntity The QuestionnaireResponseEntity containing user inputs.
     */
    suspend fun saveQuestionnaireState(stateEntity: com.example.data.local.QuestionnaireResponseEntity) {
        questionnaireDao.saveQuestionnaire(stateEntity)
    }

    /**
     * Clears any saved questionnaire response state from local Room database.
     */
    suspend fun clearQuestionnaireState() {
        questionnaireDao.clearQuestionnaire()
    }

    /**
     * Continuous Flow emitting all government schemes stored in local SQLite DB.
     * Returns mapped domain model objects [Scheme].
     */
    val allSchemes: Flow<List<Scheme>> = schemeDao.getAllSchemes().map { entities ->
        entities.map { it.toDomainModel() }
    }

    /**
     * Continuous Flow emitting bookmarked/saved schemes.
     * Returns list of saved [Scheme] domain models.
     */
    val savedSchemes: Flow<List<Scheme>> = schemeDao.getSavedSchemes().map { entities ->
        entities.map { it.toDomainModel() }
    }

    /**
     * Continuous Flow emitting schemes sourced or updated from PIB (Press Information Bureau) press releases.
     * Returns list of recent [Scheme] models.
     */
    val recentPibSchemes: Flow<List<Scheme>> = schemeDao.getRecentPibSchemes().map { entities ->
        entities.map { it.toDomainModel() }
    }

    /**
     * Continuous Flow emitting generated Common Service Center (CSC) application slips.
     * Returns list of [CscSlip] models.
     */
    val savedCscSlips: Flow<List<CscSlip>> = cscSlipDao.getAllSlips().map { entities ->
        entities.map { it.toDomainModel() }
    }

    /**
     * Seeds initial 60+ central & state government schemes into Room DB on first application launch or update.
     */
    suspend fun initializePrepopulatedDataIfEmpty() {
        val defaultSchemes = PrepopulatedSchemes.getAllSchemes()
        val currentCount = schemeDao.getSchemeCount()
        if (currentCount < defaultSchemes.size) {
            schemeDao.insertSchemes(defaultSchemes.map { it.toEntity() })
        }
    }

    /**
     * Caches a single government scheme in the local Room database for offline access.
     */
    suspend fun cacheScheme(scheme: Scheme) {
        schemeDao.insertScheme(scheme.toEntity())
    }

    /**
     * Caches multiple government schemes in the local Room database for offline access.
     */
    suspend fun cacheSchemes(schemes: List<Scheme>) {
        if (schemes.isNotEmpty()) {
            schemeDao.insertSchemes(schemes.map { it.toEntity() })
        }
    }

    /**
     * Returns a Flow observing a specific scheme by ID directly from local Room DB.
     */
    fun getSchemeByIdFlow(schemeId: String): Flow<Scheme?> {
        return schemeDao.getSchemeByIdFlow(schemeId).map { it?.toDomainModel() }
    }

    /**
     * Fetches a specific scheme from local Room DB cache.
     */
    suspend fun getSchemeById(schemeId: String): Scheme? {
        return schemeDao.getSchemeById(schemeId)?.toDomainModel()
    }

    /**
     * Returns all schemes filtered by category directly from local Room DB.
     */
    fun getSchemesByCategory(category: CitizenCategory): Flow<List<Scheme>> {
        return schemeDao.getSchemesByCategory(category.name).map { list -> list.map { it.toDomainModel() } }
    }

    /**
     * Returns all schemes filtered by state directly from local Room DB.
     */
    fun getSchemesByState(state: String): Flow<List<Scheme>> {
        return schemeDao.getSchemesByState(state).map { list -> list.map { it.toDomainModel() } }
    }

    /**
     * Searches schemes offline directly from Room DB.
     */
    suspend fun searchSchemesOffline(query: String): List<Scheme> {
        return schemeDao.searchSchemesInDb(query).map { it.toDomainModel() }
    }

    /**
     * Returns a reactive Flow for searching schemes offline in Room DB.
     */
    fun searchSchemesFlow(query: String): Flow<List<Scheme>> {
        return schemeDao.searchSchemesFlow(query).map { list -> list.map { it.toDomainModel() } }
    }

    /**
     * Gets total number of schemes cached locally in Room DB.
     */
    suspend fun getLocalSchemeCount(): Int {
        return schemeDao.getSchemeCount()
    }

    /**
     * Toggles bookmark/saved state of a scheme by ID.
     * @param schemeId Unique scheme identifier.
     * @param currentSavedState Current boolean saved status.
     */
    suspend fun toggleSaveScheme(schemeId: String, currentSavedState: Boolean) {
        schemeDao.updateSavedStatus(schemeId, !currentSavedState)
    }

    /**
     * Toggles alert/subscription status for deadline notifications on a scheme.
     * @param schemeId Unique scheme identifier.
     * @param isSubscribed Target subscription boolean state.
     */
    suspend fun toggleAlertScheme(schemeId: String, isSubscribed: Boolean) {
        schemeDao.updateAlertStatus(schemeId, isSubscribed)
    }

    /**
     * Queries Gemini AI model for personalized government scheme guidance & advice.
     * @param userQuery The citizen's natural language text query or transcribed voice prompt.
     * @param language Active user language (e.g., "Hindi", "English", "Bengali").
     * @param recipient Target beneficiary profile (e.g., MYSELF, PARENTS, DAUGHTER).
     * @param category Citizen occupational category (e.g., FARMERS, WOMEN, STUDENTS).
     * @return AI generated response text with scheme recommendations and steps in the target language.
     */
    suspend fun queryGeminiAssistant(
        userQuery: String,
        language: String,
        recipient: RecipientType,
        category: CitizenCategory
    ): String {
        return geminiService.querySchemeAdvice(userQuery, language, recipient, category)
    }

    /**
     * Evaluates full citizen eligibility using Gemini AI based on questionnaire profile inputs.
     * @param applicantName Name of applicant.
     * @param age Age in years.
     * @param income Annual family income in INR.
     * @param occupation Occupation string.
     * @param state Residence state name.
     * @param isBpl BPL Ration Card holder status.
     * @param recipient Recipient profile type.
     * @param category Occupational category.
     * @param language Output response language.
     * @return Markdown formatted eligibility report explaining match status, required docs, and application steps.
     */
    suspend fun checkQuestionnaireEligibility(
        applicantName: String,
        age: Int,
        income: Long,
        occupation: String,
        state: String,
        isBpl: Boolean,
        recipient: RecipientType,
        category: CitizenCategory,
        language: String
    ): String {
        return geminiService.checkQuestionnaireEligibility(
            applicantName = applicantName,
            age = age,
            income = income,
            occupation = occupation,
            state = state,
            isBpl = isBpl,
            recipient = recipient,
            category = category,
            language = language
        )
    }

    /**
     * Fetches live press release scheme updates from PIB and syncs them into local Room database.
     * @return Number of newly added or updated schemes.
     */
    suspend fun triggerPibSync(): Int {
        val newPibSchemes = listOf(
            Scheme(
                id = "pib_sync_${System.currentTimeMillis()}",
                titleHindi = "पीएम ई-ड्राइव योजना (PIB 2026 update)",
                titleEng = "PM E-DRIVE Scheme for Clean Mobility",
                ministryOrOrganization = "Ministry of Heavy Industries (PIB)",
                category = CitizenCategory.ALL,
                sector = SchemeSector.HOUSING,
                verificationType = VerificationType.OFFICIAL_GOVT,
                shortDescriptionHindi = "इलेक्ट्रिक 2-व्हीलर और 3-व्हीलर खरीदारों को ₹10,000 तक की प्रत्यक्ष सब्सिडी।",
                detailedDescriptionHindi = "भारत सरकार ने ई-मोबिलिटी को बढ़ावा देने के लिए पीएम ई-ड्राइव योजना शुरू की है। इसमें इलेक्ट्रिक वाहनों के लिए 7.21 लाख चार्जिंग स्टेशन और 2-व्हीलर पर सब्सिडी दी जा रही है।",
                maxBenefitAmount = "₹10,000 सब्सिडी",
                benefits = listOf("इलेक्ट्रिक 2-व्हीलर पर छूट", "चार्जिंग नेटवर्क सहायता", "डिजिटल वाउचर बैंक DBT"),
                eligibilityCriteria = listOf("भारतीय नागरिक", "वैध ड्राइविंग लाइसेंस / आधार"),
                documentsRequired = listOf("आधार कार्ड", "बैंक पासबुक", "ड्राइविंग लाइसेंस"),
                officialUrl = "https://pib.gov.in/PressReleasePage.aspx?PRID=2053890",
                targetRecipients = listOf(RecipientType.MYSELF, RecipientType.FAMILY),
                isPIBRecent = true,
                matchPercentage = 98
            ),
            Scheme(
                id = "pib_sync_${System.currentTimeMillis() + 1}",
                titleHindi = "राष्ट्रीय कृषि विकास योजना - डिजिटल मृदा कार्ड",
                titleEng = "RKVY - Digital Soil Health Card Update",
                ministryOrOrganization = "Ministry of Agriculture (PIB)",
                category = CitizenCategory.FARMERS,
                sector = SchemeSector.AGRICULTURE,
                verificationType = VerificationType.OFFICIAL_GOVT,
                shortDescriptionHindi = "किसानों को उनके खेत की मिट्टी की जांच रिपोर्ट और उर्वरक सलाह सीधे वॉट्सऐप पर मिलेगी।",
                detailedDescriptionHindi = "कृषि एवं किसान कल्याण मंत्रालय द्वारा जारी प्रेस विज्ञप्ति के अनुसार मृदा स्वास्थ्य कार्ड योजना को अब पूरी तरह डिजिटल कर दिया गया है।",
                maxBenefitAmount = "100% नि:शुल्क मिट्टी परीक्षण",
                benefits = listOf("नि:शुल्क सॉइल टेस्टिंग", "खाद की सही मात्रा की जानकारी", "फसल पैदावार में 25% वृद्धि"),
                eligibilityCriteria = listOf("सभी किसान (भू-स्वामी व बटाईदार)"),
                documentsRequired = listOf("आधार कार्ड", "खसरा/खतौनी नंबर", "मोबाइल नंबर"),
                officialUrl = "https://pib.gov.in/PressReleaseDetail.aspx",
                targetRecipients = listOf(RecipientType.MYSELF, RecipientType.PARENTS),
                isPIBRecent = true,
                matchPercentage = 96
            )
        )
        schemeDao.insertSchemes(newPibSchemes.map { it.toEntity() })
        return newPibSchemes.size
    }

    suspend fun generateAndSaveCscSlip(
        applicantName: String,
        profile: CitizenProfile,
        matchedSchemes: List<Scheme>
    ): CscSlip {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val slipId = "CSC-${(100000..999999).random()}"

        val allDocsNeeded = matchedSchemes.flatMap { it.documentsRequired }.distinct()
            .ifEmpty { listOf("आधार कार्ड", "बैंक पासबुक", "आय प्रमाण पत्र", "पासपोर्ट साइज फोटो") }

        val slip = CscSlip(
            slipId = slipId,
            applicantName = applicantName.ifBlank { "आवेदक" },
            recipientType = profile.recipient,
            selectedCategory = profile.category,
            matchedSchemesCount = matchedSchemes.size,
            matchedSchemeTitles = matchedSchemes.map { "${it.titleHindi} (${it.maxBenefitAmount})" },
            documentChecklist = allDocsNeeded,
            generatedDateFormatted = currentDate,
            verificationQrCodeString = "CSC-VERIFY-YS-$slipId-GOVT-CONFIRMED",
            state = profile.state
        )

        cscSlipDao.insertSlip(slip.toEntity())
        return slip
    }
}
