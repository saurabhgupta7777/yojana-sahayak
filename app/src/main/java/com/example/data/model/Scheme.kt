package com.example.data.model

enum class VerificationType {
    OFFICIAL_GOVT, // 🟢 Official Govt (.gov.in, nic.in, myscheme.gov.in)
    VERIFIED_CSR,  // 🔵 Verified CSR (Tata Capital, HDFC Parivartan, Reliance, Infosys)
    UNVERIFIED     // ⚠️ Non-official / suspect
}

enum class SchemeTypeFilter(val displayNameHindi: String, val displayNameEng: String, val icon: String) {
    ALL("सभी (All)", "All Schemes", "🏛️"),
    GOVT("🟢 सरकारी (Govt)", "Govt Schemes", "🏛️"),
    CSR("🔵 CSR / कॉर्पोरेट (CSR)", "CSR Schemes", "🤝")
}

enum class SchemeSector(val displayNameHindi: String, val displayNameEng: String, val icon: String) {
    ALL("सभी क्षेत्र", "All Sectors", "🌐"),
    AGRICULTURE("कृषि (Agriculture)", "Agriculture", "🌾"),
    HEALTH("स्वास्थ्य (Health)", "Health", "🏥"),
    EDUCATION("शिक्षा (Education)", "Education", "🎓"),
    HOUSING("आवास व बिजली (Housing)", "Housing", "🏠"),
    EMPLOYMENT("रोज़गार व लोन (Employment)", "Employment", "💼"),
    PENSION("पेंशन व कल्याण (Pension)", "Pension & Welfare", "👴"),
    WOMEN_CHILD("महिला व बाल (Women)", "Women & Child", "👩")
}

enum class CitizenCategory(val displayNameHindi: String, val displayNameEng: String, val icon: String) {
    ALL("सभी योजनाएं", "All Schemes", "🏛️"),
    FARMERS("किसान", "Farmers", "🌾"),
    WOMEN("महिलाएं", "Women & Girls", "👩"),
    STUDENTS("छात्र व छात्रवृत्ति", "Students", "🎓"),
    SENIOR_CITIZENS("वरिष्ठ नागरिक", "Senior Citizens", "👵"),
    LOW_INCOME("कम आय / BPL", "Low Income", "🏠"),
    ARTISANS("कारीगर व विक्रेता", "Artisans & Vendors", "🛠️"),
    DIFFERENTLY_ABLED("दिव्यांगजन", "Differently Abled", "♿")
}

enum class RecipientType(val displayNameHindi: String, val displayNameEng: String, val icon: String) {
    ALL("सभी (All)", "All", "🌐"),
    MYSELF("खुद के लिए", "Myself", "🙋"),
    CHILD("बच्चे/छात्र", "Child / Student", "👦"),
    PARENTS("माता-पिता/बुजुर्ग", "Parents / Elderly", "👵"),
    FAMILY("पूरे परिवार", "Whole Family", "👪")
}

data class Scheme(
    val id: String,
    val titleHindi: String,
    val titleEng: String,
    val ministryOrOrganization: String,
    val category: CitizenCategory,
    val sector: SchemeSector = SchemeSector.ALL,
    val verificationType: VerificationType,
    val shortDescriptionHindi: String,
    val detailedDescriptionHindi: String,
    val maxBenefitAmount: String,
    val benefits: List<String>,
    val eligibilityCriteria: List<String>,
    val documentsRequired: List<String>,
    val officialUrl: String,
    val targetRecipients: List<RecipientType>,
    val state: String = "All India",
    val isPIBRecent: Boolean = false,
    val matchPercentage: Int = 95,
    val isSaved: Boolean = false,
    val applicationWindow: String = "आवेदन हमेशा जारी (Open All Year)",
    val howToApplySteps: List<String> = emptyList(),
    val targetQualification: String = "सभी के लिए (All Qualifications)",
    val isApplicationOpen: Boolean = true,
    val isAlertSubscribed: Boolean = false,
    val lastVerifiedDate: String = "03 Aug 2026"
)
