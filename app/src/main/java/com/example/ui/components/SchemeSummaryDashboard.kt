package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CitizenCategory
import com.example.data.model.RecipientType
import com.example.data.model.Scheme
import com.example.data.model.SchemeSector
import com.example.data.model.VerificationType
import com.example.ui.theme.*

/**
 * Application Processing Stages
 */
enum class ApplicationStage(
    val stageNumber: Int,
    val titleHindi: String,
    val titleEng: String,
    val descriptionHindi: String,
    val badgeColor: Color,
    val icon: ImageVector
) {
    SUBMITTED(
        stageNumber = 1,
        titleHindi = "आवेदन प्रस्तुत",
        titleEng = "Application Submitted",
        descriptionHindi = "आपका आवेदन आधिकारिक पोर्टल पर सफलतापूर्वक दर्ज किया जा चुका है।",
        badgeColor = Color(0xFF0288D1),
        icon = Icons.Default.Send
    ),
    UNDER_REVIEW(
        stageNumber = 2,
        titleHindi = "सत्यापन प्रक्रियाधीन",
        titleEng = "Under Verification",
        descriptionHindi = "ग्राम विकास अधिकारी / ब्लॉक नोडल अधिकारी द्वारा दस्तावेजों की जांच जारी है।",
        badgeColor = Color(0xFFED6C02),
        icon = Icons.Default.HourglassTop
    ),
    SANCTIONED(
        stageNumber = 3,
        titleHindi = "स्वीकृति पत्र जारी",
        titleEng = "Sanctioned / Approved",
        descriptionHindi = "आपका आवेदन स्वीकृत हो चुका है तथा डीबीटी (DBT) भुगतान आदेश जारी किया जा चुका है।",
        badgeColor = Color(0xFF2E7D32),
        icon = Icons.Default.TaskAlt
    ),
    DISBURSED(
        stageNumber = 4,
        titleHindi = "डीबीटी जमा (राशि बैंक में)",
        titleEng = "DBT Disbursed",
        descriptionHindi = "योजना की वित्तीय राशि आपके आधार-लिंक्ड बैंक खाते (Aadhaar DBT) में सफलतापूर्वक जमा कर दी गई है।",
        badgeColor = Color(0xFF004D25),
        icon = Icons.Default.AccountBalance
    ),
    ACTION_REQUIRED(
        stageNumber = 0,
        titleHindi = "कार्रवाई आवश्यक (e-KYC)",
        titleEng = "Action Required",
        descriptionHindi = "दस्तावेज़ में त्रुटि अथवा आधार ई-केवाईसी (e-KYC) लंबित है। तुरंत अपडेट करें।",
        badgeColor = Color(0xFFD32F2F),
        icon = Icons.Default.Warning
    )
}

/**
 * Model representing a scheme being tracked by the citizen.
 */
data class TrackedSchemeItem(
    val id: String,
    val scheme: Scheme,
    val applicationRefNo: String,
    val submissionDate: String,
    val lastUpdatedDate: String,
    val currentStage: ApplicationStage,
    val eligibilityScore: Int, // 0 to 100
    val isEligible: Boolean,
    val missingRequirements: List<String> = emptyList(),
    val approvedBenefitAmount: String,
    val nextActionText: String? = null,
    val remarkHistory: List<String> = emptyList()
)

/**
 * Default mock list of tracked schemes for demonstration and testing.
 */
val DEFAULT_TRACKED_SCHEME_ITEMS = listOf(
    TrackedSchemeItem(
        id = "track_1",
        scheme = Scheme(
            id = "pm_kisan",
            titleHindi = "प्रधानमंत्री किसान सम्मान निधि (PM-KISAN)",
            titleEng = "PM Kisan Samman Nidhi",
            ministryOrOrganization = "कृषि एवं किसान कल्याण मंत्रालय",
            category = CitizenCategory.FARMERS,
            sector = SchemeSector.AGRICULTURE,
            verificationType = VerificationType.OFFICIAL_GOVT,
            shortDescriptionHindi = "किसान परिवारों को प्रति वर्ष ₹6,000 की वित्तीय सहायता।",
            detailedDescriptionHindi = "प्रति वर्ष ₹6,000 तीन किस्तों में सीधे किसान खाते में जमा की जाती है।",
            maxBenefitAmount = "₹6,000 / वर्ष",
            benefits = listOf("₹2,000 की 3 समान किस्तें", "डीबीटी बैंक हस्तांतरण"),
            eligibilityCriteria = listOf("कृषि योग्य भूमि पंजीकृत हो", "लघु व सीमांत किसान"),
            documentsRequired = listOf("आधार कार्ड", "भूमि खसरा", "बैंक खाता"),
            officialUrl = "https://pmkisan.gov.in",
            targetRecipients = listOf(RecipientType.MYSELF)
        ),
        applicationRefNo = "PMK-2026-889123",
        submissionDate = "15 Jan 2026",
        lastUpdatedDate = "10 Aug 2026",
        currentStage = ApplicationStage.DISBURSED,
        eligibilityScore = 98,
        isEligible = true,
        approvedBenefitAmount = "₹6,000 (17वीं किस्त जमा)",
        nextActionText = "अगली किस्त के लिए बैंक खाते की NPCI डीबीटी सीडिंग चेक करें।",
        remarkHistory = listOf(
            "10 Aug 2026: ₹2,000 किस्त खाते में डीबीटी द्वारा सफलतापूर्वक क्रेडिट की गई।",
            "02 Feb 2026: आधार e-KYC सफलतापूर्वक सत्यापित।"
        )
    ),
    TrackedSchemeItem(
        id = "track_2",
        scheme = Scheme(
            id = "pm_awas",
            titleHindi = "प्रधानमंत्री आवास योजना (ग्रामीण - PMAY-G)",
            titleEng = "PM Awas Yojana Gramin",
            ministryOrOrganization = "ग्रामीण विकास मंत्रालय",
            category = CitizenCategory.LOW_INCOME,
            sector = SchemeSector.HOUSING,
            verificationType = VerificationType.OFFICIAL_GOVT,
            shortDescriptionHindi = "कच्चे मकान वाले ग्रामीण परिवारों को पक्के घर हेतु ₹1.20 लाख की सहायता।",
            detailedDescriptionHindi = "बेघर एवं कच्चे घर वाले परिवारों को पक्का मकान बनाने हेतु आर्थिक सहायता दी जाती है।",
            maxBenefitAmount = "₹1,20,000",
            benefits = listOf("₹1,20,000 की सब्सिडी सहायता", "मनरेगा 90 दिन की मजदूरी", "मुफ्त शौचालय प्रोत्साहन"),
            eligibilityCriteria = listOf("कच्चा मकान या बेघर", "SECC 2011 सूची या आवास प्लस में नाम"),
            documentsRequired = listOf("आधार कार्ड", "बैंक पासबुक", "जमीन की फोटो/जियोटैग"),
            officialUrl = "https://pmayg.nic.in",
            targetRecipients = listOf(RecipientType.FAMILY)
        ),
        applicationRefNo = "PMAYG-UP-772109",
        submissionDate = "22 Feb 2026",
        lastUpdatedDate = "11 Aug 2026",
        currentStage = ApplicationStage.UNDER_REVIEW,
        eligibilityScore = 92,
        isEligible = true,
        approvedBenefitAmount = "₹1,20,000 (स्वीकृति प्रक्रियाधीन)",
        nextActionText = "ब्लॉक स्तर से जिओटैगिंग अधिकारी स्थल निरीक्षण हेतु आने वाले हैं।",
        remarkHistory = listOf(
            "11 Aug 2026: ग्राम पंचायत स्तर से पात्रता सूची में नाम अनुमोदित किया गया।",
            "22 Feb 2026: ऑनलाइन आवेदन पत्र दर्ज हुआ।"
        )
    ),
    TrackedSchemeItem(
        id = "track_3",
        scheme = Scheme(
            id = "ayushman_bharat",
            titleHindi = "आयुष्मान भारत - प्रधानमंत्री जन आरोग्य (AB-PMJAY)",
            titleEng = "Ayushman Bharat PMJAY",
            ministryOrOrganization = "स्वास्थ्य एवं परिवार कल्याण मंत्रालय",
            category = CitizenCategory.LOW_INCOME,
            sector = SchemeSector.HEALTH,
            verificationType = VerificationType.OFFICIAL_GOVT,
            shortDescriptionHindi = "प्रति परिवार प्रति वर्ष ₹5 लाख का मुफ्त कैशलेस स्वास्थ्य बीमा कवर।",
            detailedDescriptionHindi = "संबद्ध सरकारी व निजी अस्पतालों में मुफ़्त इलाज एवं भर्ती सुविधा।",
            maxBenefitAmount = "₹5,00,000 / वर्ष",
            benefits = listOf("₹5 लाख तक मुफ़्त इलाज", "कैशलेस भर्ती व दवाएं", "पूरी तरह मुफ़्त कार्ड"),
            eligibilityCriteria = listOf("BPL / SECC सूची अथवा राशन कार्ड धारक"),
            documentsRequired = listOf("आधार कार्ड", "राशन कार्ड"),
            officialUrl = "https://pmjay.gov.in",
            targetRecipients = listOf(RecipientType.FAMILY)
        ),
        applicationRefNo = "ABHA-9912048",
        submissionDate = "05 Mar 2026",
        lastUpdatedDate = "08 Aug 2026",
        currentStage = ApplicationStage.SANCTIONED,
        eligibilityScore = 100,
        isEligible = true,
        approvedBenefitAmount = "₹5,00,000 (आयुष्मान कार्ड एक्टिवेटेड)",
        nextActionText = "आयुष्मान डिजिटल स्वास्थ्य कार्ड डाउनलोड कर पास रखें।",
        remarkHistory = listOf(
            "08 Aug 2026: आयुष्मान कार्ड स्वीकृत एवं डाउनलोड हेतु उपलब्ध।"
        )
    ),
    TrackedSchemeItem(
        id = "track_4",
        scheme = Scheme(
            id = "pm_vishwakarma",
            titleHindi = "पीएम विश्वकर्मा योजना (टूलकिट ग्रांट)",
            titleEng = "PM Vishwakarma Scheme",
            ministryOrOrganization = "सूक्ष्म, लघु और मध्यम उद्यम मंत्रालय (MSME)",
            category = CitizenCategory.ARTISANS,
            sector = SchemeSector.EMPLOYMENT,
            verificationType = VerificationType.OFFICIAL_GOVT,
            shortDescriptionHindi = "पारंपरिक कारीगरों व शिल्पकारों को ₹15,000 टूलकिट वाउचर व कम ब्याज लोन।",
            detailedDescriptionHindi = "18 पारंपरिक व्यवसायों (बढ़ई, लोहार, दर्जी, राजमिस्त्री आदि) के लिए।",
            maxBenefitAmount = "₹15,000 टूलकिट + ₹3 लाख लोन",
            benefits = listOf("₹15,000 मुफ्त टूलकिट वाउचर", "5% ब्याज पर ₹3 लाख तक लोन", "कौशल प्रशिक्षण व दैनिक स्टाइपेंड"),
            eligibilityCriteria = listOf("पारंपरिक 18 व्यवसायों में से एक में कार्यरत", "आयु 18 वर्ष से अधिक"),
            documentsRequired = listOf("आधार कार्ड", "बैंक खाता", "व्यवसाय सत्यापन फोटो"),
            officialUrl = "https://pmvishwakarma.gov.in",
            targetRecipients = listOf(RecipientType.MYSELF)
        ),
        applicationRefNo = "PMV-2026-3391",
        submissionDate = "18 Apr 2026",
        lastUpdatedDate = "12 Aug 2026",
        currentStage = ApplicationStage.ACTION_REQUIRED,
        eligibilityScore = 85,
        isEligible = true,
        missingRequirements = listOf("बैंक खाता नाम सुधार (Aadhaar Match)", "CSC बायोमेट्रिक सत्यापन"),
        approvedBenefitAmount = "₹15,00,0 (लंबित)",
        nextActionText = "निकटतम CSC केंद्र पर जाकर बायोमेट्रिक ई-केवाईसी पूर्ण करें।",
        remarkHistory = listOf(
            "12 Aug 2026: बैंक खाते के नाम में अंतर होने के कारण सत्यापन रुका है। आधार अनुसार नाम अपडेट करें।"
        )
    )
)

/**
 * Filter modes for the Dashboard tabs
 */
private enum class DashboardTab(val titleHindi: String, val icon: ImageVector) {
    OVERVIEW("समग्र सारांश", Icons.Default.Dashboard),
    ELIGIBILITY("पात्रता स्थिति", Icons.Default.Verified),
    PROGRESS("आवेदन प्रगति", Icons.Default.Timeline)
}

/**
 * Reusable Jetpack Compose Summary Dashboard Component showing:
 * 1. User's Eligibility Status & Match scores across tracked schemes.
 * 2. Real-time Application Progress Tracking with stage steppers and next actions.
 *
 * @param trackedSchemes List of schemes being tracked by the citizen. Defaults to [DEFAULT_TRACKED_SCHEME_ITEMS].
 * @param onSchemeClick Callback when a scheme card is clicked.
 * @param onApplyClick Callback when "Apply / Portal" is clicked.
 * @param onGenerateCscSlip Callback to create CSC slip for offline help.
 * @param modifier Modifier.
 */
@Composable
fun SchemeSummaryDashboard(
    trackedSchemes: List<TrackedSchemeItem> = DEFAULT_TRACKED_SCHEME_ITEMS,
    onSchemeClick: (Scheme) -> Unit = {},
    onApplyClick: (Scheme) -> Unit = {},
    onGenerateCscSlip: (Scheme) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableStateOf(DashboardTab.OVERVIEW) }
    var expandedSchemeId by remember { mutableStateOf<String?>(null) }

    // Aggregate KPI Statistics
    val totalTracked = trackedSchemes.size
    val eligibleCount = trackedSchemes.count { it.isEligible && it.eligibilityScore >= 80 }
    val actionRequiredCount = trackedSchemes.count { it.currentStage == ApplicationStage.ACTION_REQUIRED }
    val approvedCount = trackedSchemes.count { it.currentStage == ApplicationStage.SANCTIONED || it.currentStage == ApplicationStage.DISBURSED }
    val inProgressCount = trackedSchemes.count { it.currentStage == ApplicationStage.SUBMITTED || it.currentStage == ApplicationStage.UNDER_REVIEW }

    val averageEligibilityScore = if (totalTracked > 0) {
        trackedSchemes.map { it.eligibilityScore }.average().toInt()
    } else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("scheme_summary_dashboard"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Title & User Summary Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NavyDeep),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SaffronPrimary,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "योजना ट्रैक व पात्रता डैशबोर्ड",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "आपकी सभी योजनाओं की स्थिति व प्रगति",
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )
                        }
                    }

                    Surface(
                        color = EmeraldGreen,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "$averageEligibilityScore% औसत पात्र",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                // KPI Metrics Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    KpiStatBadge(
                        count = totalTracked.toString(),
                        label = "कुल ट्रैक्ड",
                        icon = Icons.Default.Bookmark,
                        accentColor = Color(0xFF64B5F6)
                    )
                    KpiStatBadge(
                        count = approvedCount.toString(),
                        label = "स्वीकृत / जमा",
                        icon = Icons.Default.CheckCircle,
                        accentColor = Color(0xFF81C784)
                    )
                    KpiStatBadge(
                        count = inProgressCount.toString(),
                        label = "प्रक्रियाधीन",
                        icon = Icons.Default.HourglassTop,
                        accentColor = Color(0xFFFFB74D)
                    )
                    KpiStatBadge(
                        count = actionRequiredCount.toString(),
                        label = "कार्रवाई जरूरी",
                        icon = Icons.Default.Error,
                        accentColor = Color(0xFFE57373)
                    )
                }
            }
        }

        // Segmented Control Tab Bar
        Surface(
            color = WarmSurface,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DashboardTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    val tabBg = if (isSelected) SaffronPrimary else Color.Transparent
                    val tabTextColor = if (isSelected) Color.White else NavyDeep

                    Surface(
                        onClick = { selectedTab = tab },
                        color = tabBg,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_${tab.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = tabTextColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.titleHindi,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = tabTextColor
                            )
                        }
                    }
                }
            }
        }

        // Active Tab View Rendering
        when (selectedTab) {
            DashboardTab.OVERVIEW -> {
                OverviewTabContent(
                    trackedSchemes = trackedSchemes,
                    onSchemeClick = onSchemeClick,
                    onApplyClick = onApplyClick,
                    onGenerateCscSlip = onGenerateCscSlip
                )
            }
            DashboardTab.ELIGIBILITY -> {
                EligibilityTabContent(
                    trackedSchemes = trackedSchemes,
                    onSchemeClick = onSchemeClick,
                    onApplyClick = onApplyClick
                )
            }
            DashboardTab.PROGRESS -> {
                ApplicationProgressTabContent(
                    trackedSchemes = trackedSchemes,
                    expandedSchemeId = expandedSchemeId,
                    onToggleExpand = { id ->
                        expandedSchemeId = if (expandedSchemeId == id) null else id
                    },
                    onSchemeClick = onSchemeClick,
                    onApplyClick = onApplyClick,
                    onGenerateCscSlip = onGenerateCscSlip,
                    onCopyRefNo = { refNo ->
                        clipboardManager.setText(AnnotatedString(refNo))
                        Toast.makeText(context, "आवेदन संख्या कॉपी की गई: $refNo", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 1: OVERVIEW TAB CONTENT
// -----------------------------------------------------------------------------

@Composable
private fun OverviewTabContent(
    trackedSchemes: List<TrackedSchemeItem>,
    onSchemeClick: (Scheme) -> Unit,
    onApplyClick: (Scheme) -> Unit,
    onGenerateCscSlip: (Scheme) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // High-Priority Action Alert Card if any action is needed
        val pendingActionItem = trackedSchemes.firstOrNull { it.currentStage == ApplicationStage.ACTION_REQUIRED }
        if (pendingActionItem != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, UnverifiedAlertRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReportProblem,
                            contentDescription = null,
                            tint = UnverifiedAlertRed,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "⚠️ ध्यान दें: कार्रवाई आवश्यक (Action Required)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnverifiedAlertRed
                        )
                    }

                    Text(
                        text = "आपकी योजना '${pendingActionItem.scheme.titleHindi}' में अपडेट आवश्यक है:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = NavyDeep
                    )

                    pendingActionItem.nextActionText?.let { action ->
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, UnverifiedAlertRed.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "👉 $action",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = UnverifiedAlertRed,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onApplyClick(pendingActionItem.scheme) },
                            colors = ButtonDefaults.buttonColors(containerColor = UnverifiedAlertRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("तुरंत पोर्टल खोलें", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Quick Highlights Section Title
        Text(
            text = "📋 ट्रैक्ड योजनाओं की संक्षिप्त स्थिति",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = NavyDeep
        )

        trackedSchemes.forEach { item ->
            TrackedOverviewSummaryCard(
                item = item,
                onSchemeClick = { onSchemeClick(item.scheme) },
                onApplyClick = { onApplyClick(item.scheme) }
            )
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 2: ELIGIBILITY STATUS TAB CONTENT
// -----------------------------------------------------------------------------

@Composable
private fun EligibilityTabContent(
    trackedSchemes: List<TrackedSchemeItem>,
    onSchemeClick: (Scheme) -> Unit,
    onApplyClick: (Scheme) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Info Banner
        Surface(
            color = EmeraldContainer.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FactCheck,
                    contentDescription = null,
                    tint = EmeraldGreen,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "आपकी प्रोफ़ाइल (आयु, आय, व्यवसाय, निवास) के आधार पर एआई स्वचालित रूप से पात्रता मैच स्कोर की गणना करता है।",
                    fontSize = 12.sp,
                    color = EmeraldGreen,
                    lineHeight = 18.sp
                )
            }
        }

        trackedSchemes.forEach { item ->
            EligibilityDetailCard(
                item = item,
                onSchemeClick = { onSchemeClick(item.scheme) },
                onApplyClick = { onApplyClick(item.scheme) }
            )
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 3: APPLICATION PROGRESS TAB CONTENT
// -----------------------------------------------------------------------------

@Composable
private fun ApplicationProgressTabContent(
    trackedSchemes: List<TrackedSchemeItem>,
    expandedSchemeId: String?,
    onToggleExpand: (String) -> Unit,
    onSchemeClick: (Scheme) -> Unit,
    onApplyClick: (Scheme) -> Unit,
    onGenerateCscSlip: (Scheme) -> Unit,
    onCopyRefNo: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        trackedSchemes.forEach { item ->
            val isExpanded = expandedSchemeId == item.id

            ApplicationProgressCard(
                item = item,
                isExpanded = isExpanded,
                onToggleExpand = { onToggleExpand(item.id) },
                onSchemeClick = { onSchemeClick(item.scheme) },
                onApplyClick = { onApplyClick(item.scheme) },
                onGenerateCscSlip = { onGenerateCscSlip(item.scheme) },
                onCopyRefNo = { onCopyRefNo(item.applicationRefNo) }
            )
        }
    }
}

// -----------------------------------------------------------------------------
// COMPONENT CARDS & ITEM VIEWS
// -----------------------------------------------------------------------------

@Composable
private fun KpiStatBadge(
    count: String,
    label: String,
    icon: ImageVector,
    accentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = accentColor.copy(alpha = 0.2f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Text(
            text = count,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TrackedOverviewSummaryCard(
    item: TrackedSchemeItem,
    onSchemeClick: () -> Unit,
    onApplyClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSchemeClick() }
            .testTag("overview_card_${item.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = item.currentStage.badgeColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = item.currentStage.icon,
                            contentDescription = null,
                            tint = item.currentStage.badgeColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = item.currentStage.titleHindi,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = item.currentStage.badgeColor
                        )
                    }
                }

                Text(
                    text = "पात्रता: ${item.eligibilityScore}% मैच",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.eligibilityScore >= 90) VerifiedGovtGreen else SaffronPrimary
                )
            }

            Text(
                text = item.scheme.titleHindi,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDeep,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "आवेदन संख्या:",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = item.applicationRefNo,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NavyDeep
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "अनुमोदित लाभ:",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = item.approvedBenefitAmount,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun EligibilityDetailCard(
    item: TrackedSchemeItem,
    onSchemeClick: () -> Unit,
    onApplyClick: () -> Unit
) {
    val scoreColor = when {
        item.eligibilityScore >= 90 -> VerifiedGovtGreen
        item.eligibilityScore >= 70 -> SaffronPrimary
        else -> UnverifiedAlertRed
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("eligibility_card_${item.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.scheme.category.displayNameHindi,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Text(
                        text = item.scheme.titleHindi,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep
                    )
                }

                Surface(
                    color = scoreColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${item.eligibilityScore}% मैच",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Match Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("पात्रता प्रतिशत (Match Score)", fontSize = 11.sp, color = Color.Gray)
                    Text(if (item.isEligible) "पात्र (Eligible)" else "अपात्र", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = scoreColor)
                }
                LinearProgressIndicator(
                    progress = { item.eligibilityScore / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = scoreColor,
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )
            }

            // Eligibility Criteria list
            Text(
                text = "✓ जांची गई पात्रता शर्तें:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDeep
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                item.scheme.eligibilityCriteria.take(3).forEach { criterion ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = VerifiedGovtGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = criterion,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // Missing requirements if any
            if (item.missingRequirements.isNotEmpty()) {
                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SaffronContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "⚠️ पूर्ति हेतु आवश्यक कदम:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                        item.missingRequirements.forEach { req ->
                            Text(
                                text = "• $req",
                                fontSize = 12.sp,
                                color = NavyDeep
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onSchemeClick,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("विवरण देखें", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onApplyClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("पोर्टल पर आवेदन करें", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ApplicationProgressCard(
    item: TrackedSchemeItem,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSchemeClick: () -> Unit,
    onApplyClick: () -> Unit,
    onGenerateCscSlip: () -> Unit,
    onCopyRefNo: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("progress_card_${item.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title Header & Ref Number Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.scheme.titleHindi,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep
                    )
                    Text(
                        text = item.scheme.ministryOrOrganization,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Surface(
                    color = item.currentStage.badgeColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = item.currentStage.titleHindi,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = item.currentStage.badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Application Ref ID & Copy Button
            Surface(
                color = SurfaceVariantWarm,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = NavyDeep,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "आवेदन संख्या: ${item.applicationRefNo}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDeep
                        )
                    }

                    IconButton(
                        onClick = onCopyRefNo,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Ref No",
                            tint = SaffronPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 4-Stage Stepper Progress Tracker
            ApplicationStageStepper(currentStage = item.currentStage)

            // Current Stage Description
            Text(
                text = "📌 वर्तमान स्थिति: ${item.currentStage.descriptionHindi}",
                fontSize = 12.sp,
                color = Color.DarkGray,
                lineHeight = 18.sp
            )

            // Expandable History / Remarks Toggle Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "स्थिति इतिहास छिपाएं ▲" else "सत्यापन इतिहास व टिप्पणी देखें ▼",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SaffronPrimary
                )
                Text(
                    text = "अंतिम अपडेट: ${item.lastUpdatedDate}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // Expandable Remarks
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📝 नोडल अधिकारी टिप्पणी (Remark History):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep
                    )
                    item.remarkHistory.forEach { remark ->
                        Text(
                            text = "• $remark",
                            fontSize = 12.sp,
                            color = Color(0xFF334155),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onGenerateCscSlip,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CSC पर्ची", fontSize = 12.sp)
                }

                Button(
                    onClick = onApplyClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("पोर्टल देखें", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

/**
 * Visual Stepper for the 4 key stages of a government scheme application
 */
@Composable
private fun ApplicationStageStepper(currentStage: ApplicationStage) {
    val activeStageNum = currentStage.stageNumber

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val stages = listOf(
            ApplicationStage.SUBMITTED,
            ApplicationStage.UNDER_REVIEW,
            ApplicationStage.SANCTIONED,
            ApplicationStage.DISBURSED
        )

        stages.forEachIndexed { index, stage ->
            val isCompleted = activeStageNum >= stage.stageNumber
            val isCurrent = activeStageNum == stage.stageNumber
            val isError = currentStage == ApplicationStage.ACTION_REQUIRED && index == 1

            val circleBg = when {
                isError -> UnverifiedAlertRed
                isCompleted -> EmeraldGreen
                else -> Color.LightGray.copy(alpha = 0.5f)
            }

            val circleIcon = when {
                isError -> Icons.Default.Warning
                isCompleted -> Icons.Default.Check
                else -> stage.icon
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = circleBg,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = circleIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stage.titleHindi,
                    fontSize = 9.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) NavyDeep else Color.Gray,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 11.sp
                )
            }

            // Connecting line between stepper nodes
            if (index < stages.size - 1) {
                val lineBg = if (activeStageNum > stage.stageNumber) EmeraldGreen else Color.LightGray.copy(alpha = 0.4f)
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .height(3.dp)
                        .background(lineBg, RoundedCornerShape(2.dp))
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SchemeSummaryDashboardPreview() {
    SchemeSummaryDashboard(
        trackedSchemes = DEFAULT_TRACKED_SCHEME_ITEMS
    )
}
