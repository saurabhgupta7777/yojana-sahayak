package com.example.util

data class LanguageOption(
    val code: String,
    val displayName: String,
    val flagEmoji: String = "🇮🇳"
)

data class LanguageSelectionState(
    val selectedLanguage: LanguageOption = DEFAULT_LANGUAGE,
    val isDropdownExpanded: Boolean = false,
    val availableLanguages: List<LanguageOption> = SUPPORTED_LANGUAGES
) {
    val isHindi: Boolean get() = selectedLanguage.code == "hi"
    val isEnglish: Boolean get() = selectedLanguage.code == "en"

    companion object {
        val HINDI = LanguageOption("hi", "Hindi (हिंदी)")
        val ENGLISH = LanguageOption("en", "English")
        val BENGALI = LanguageOption("bn", "Bengali (বাংলা)")
        val TELUGU = LanguageOption("te", "Telugu (తెలుగు)")
        val MARATHI = LanguageOption("mr", "Marathi (मराठी)")
        val TAMIL = LanguageOption("ta", "Tamil (தமிழ்)")
        val GUJARATI = LanguageOption("gu", "Gujarati (ગુજરાતી)")

        val DEFAULT_LANGUAGE = HINDI

        val SUPPORTED_LANGUAGES = listOf(
            HINDI,
            ENGLISH,
            BENGALI,
            TELUGU,
            MARATHI,
            TAMIL,
            GUJARATI
        )

        fun fromString(langStr: String): LanguageOption {
            val lower = langStr.lowercase()
            return when {
                lower.contains("english") || lower == "en" -> ENGLISH
                lower.contains("bengali") || lower.contains("বাংলা") || lower == "bn" -> BENGALI
                lower.contains("telugu") || lower.contains("తెలుగు") || lower == "te" -> TELUGU
                lower.contains("marathi") || lower.contains("मराठी") || lower == "mr" -> MARATHI
                lower.contains("tamil") || lower.contains("தமிழ்") || lower == "ta" -> TAMIL
                lower.contains("gujarati") || lower.contains("ગુજરાતી") || lower == "gu" -> GUJARATI
                else -> HINDI
            }
        }
    }
}
