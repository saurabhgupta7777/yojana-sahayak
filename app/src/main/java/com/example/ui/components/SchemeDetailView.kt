package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
 * Default mock data object for demonstration & standalone usage when no scheme is passed explicitly.
 */
val MOCK_GOVT_SCHEME_DETAIL = Scheme(
    id = "pm_kisan_yojana_mock",
    titleHindi = "प्रधानमंत्री किसान सम्मान निधि (PM-KISAN)",
    titleEng = "PM Kisan Samman Nidhi Yojana",
    ministryOrOrganization = "कृषि एवं किसान कल्याण मंत्रालय, भारत सरकार",
    category = CitizenCategory.FARMERS,
    sector = SchemeSector.AGRICULTURE,
    verificationType = VerificationType.OFFICIAL_GOVT,
    shortDescriptionHindi = "सभी छोटे व सीमांत किसान परिवारों को प्रति वर्ष ₹6,000 की सीधी वित्तीय सहायता।",
    detailedDescriptionHindi = "प्रधानमंत्री किसान सम्मान निधि योजना (PM-KISAN) भारत सरकार की एक प्रमुख केंद्रीय क्षेत्र की योजना है। इसके तहत पात्र किसान परिवारों को प्रति वर्ष ₹6,000 की राशि ₹2,000 की तीन समान किस्तों में सीधे उनके बैंक खातों में डीबीटी (DBT) के माध्यम से हस्तांतरित की जाती है।",
    maxBenefitAmount = "₹6,000 / वर्ष (3 किस्तों में)",
    benefits = listOf(
        "प्रति वर्ष ₹6,000 की नकद वित्तीय सहायता direct bank transfer (DBT) के ज़रिए।",
        "3 समान किस्तों (₹2,000 प्रत्येक) में सीधा भुगतान।",
        "कृषि आदानों (खाद, बीज, उपकरण) की खरीद में प्रत्यक्ष मदद।",
        "डिजिटल पीएम-किसान पोर्टल व मोबाइल ऐप से ई-केवाईसी (e-KYC) तथा स्थिति की त्वरित जांच।"
    ),
    eligibilityCriteria = listOf(
        "आवेदक भारतीय नागरिक होना चाहिए तथा किसान परिवार से संबंधित हो।",
        "आवेदक के नाम पर कृषि योग्य भूमि पंजीकृत होनी चाहिए।",
        "लघु एवं सीमांत किसान (Small & Marginal Farmers) परिवार पात्र हैं।",
        "संस्थागत भू-धारक (Institutional Land Holders) एवं आयकरदाता परिवार अपात्र हैं।"
    ),
    documentsRequired = listOf(
        "आधार कार्ड (Aadhaar Card - अनिवार्य)",
        "भूमि स्वामित्व दस्तावेज / खसरा-खतौनी फर्द",
        "बैंक खाता विवरण (आधार से लिंक एवं DBT सक्षम)",
        "सक्रिय मोबाइल नंबर"
    ),
    officialUrl = "https://pmkisan.gov.in",
    targetRecipients = listOf(RecipientType.MYSELF, RecipientType.FAMILY),
    state = "All India (सभी राज्य व केंद्रशासित प्रदेश)",
    isPIBRecent = true,
    matchPercentage = 98,
    isSaved = false,
    applicationWindow = "आवेदन हमेशा जारी (Open All Year)",
    howToApplySteps = listOf(
        "आधिकारिक पोर्टल pmkisan.gov.in पर जाएं या 'New Farmer Registration' पर क्लिक करें।",
        "अपना आधार नंबर और राज्य दर्ज कर विवरण सत्यापित करें।",
        "आवेदन पत्र में अपनी व्यक्तिगत व भूमि खसरा विवरण भरें।",
        "दस्तावेज़ अपलोड करें तथा फॉर्म सबमिट कर पावती संख्या (Registration No.) सुरक्षित रखें।"
    ),
    targetQualification = "सभी भूमिधारक किसान परिवार",
    isApplicationOpen = true,
    lastVerifiedDate = "12 Aug 2026"
)

/**
 * Reusable Jetpack Compose Component to display detailed government scheme information
 * including benefits, eligibility criteria, required documents, how to apply steps,
 * and direct application links.
 *
 * @param scheme Scheme data object (defaults to [MOCK_GOVT_SCHEME_DETAIL]).
 * @param selectedLanguage Current UI display language preference.
 * @param onToggleSaveClick Callback when bookmark button is clicked.
 * @param onReadAloudClick Callback when voice read-aloud button is clicked.
 * @param onGenerateCscSlipClick Callback when CSC Application Slip button is clicked.
 * @param modifier Composable Modifier.
 */
@Composable
fun SchemeDetailView(
    scheme: Scheme = MOCK_GOVT_SCHEME_DETAIL,
    selectedLanguage: String = "Hindi (हिंदी)",
    onToggleSaveClick: (String, Boolean) -> Unit = { _, _ -> },
    onReadAloudClick: (String) -> Unit = {},
    onGenerateCscSlipClick: ((Scheme) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSavedState by remember(scheme.id) { mutableStateOf(scheme.isSaved) }

    val displayTitle = if (selectedLanguage.startsWith("Eng") && scheme.titleEng.isNotBlank()) scheme.titleEng else scheme.titleHindi
    val displayDesc = scheme.detailedDescriptionHindi.ifBlank { scheme.shortDescriptionHindi }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("scheme_detail_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Header Card with Verification Badge & Save/Share Actions
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = WarmSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Verification Badge & Category Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (badgeText, badgeBg, badgeTextColor) = when (scheme.verificationType) {
                        VerificationType.OFFICIAL_GOVT -> Triple("🟢 आधिकारिक सरकारी योजना", VerifiedGovtGreen.copy(alpha = 0.12f), VerifiedGovtGreen)
                        VerificationType.VERIFIED_CSR -> Triple("🔵 सत्यानापित CSR / कॉर्पोरेट", VerifiedCsrBlue.copy(alpha = 0.12f), VerifiedCsrBlue)
                        VerificationType.UNVERIFIED -> Triple("⚠️ अप्रमाणित स्रोत", UnverifiedAlertRed.copy(alpha = 0.12f), UnverifiedAlertRed)
                    }

                    Surface(
                        color = badgeBg,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeTextColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Read Aloud / Voice TTS Button
                        IconButton(
                            onClick = {
                                val speechText = "$displayTitle. $displayDesc. लाभ: ${scheme.benefits.joinToString(", ")}"
                                onReadAloudClick(speechText)
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SaffronPrimary.copy(alpha = 0.1f))
                                .testTag("read_aloud_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "सुनें (Read Aloud)",
                                tint = SaffronPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Bookmark / Save Button
                        IconButton(
                            onClick = {
                                isSavedState = !isSavedState
                                onToggleSaveClick(scheme.id, isSavedState)
                                val msg = if (isSavedState) "योजना सहेजी गई (Scheme Saved)" else "योजना हटाई गई"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SaffronPrimary.copy(alpha = 0.1f))
                                .testTag("toggle_save_button")
                        ) {
                            Icon(
                                imageVector = if (isSavedState) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "सहेजें (Save Scheme)",
                                tint = SaffronPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Share Button
                        IconButton(
                            onClick = {
                                shareSchemeDetails(context, scheme, displayTitle)
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NavyDeep.copy(alpha = 0.08f))
                                .testTag("share_scheme_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "साझा करें (Share)",
                                tint = NavyDeep,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Ministry Tag
                Text(
                    text = scheme.ministryOrOrganization,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                // Scheme Main Title
                Text(
                    text = displayTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDeep,
                    lineHeight = 26.sp
                )

                if (scheme.titleEng.isNotBlank() && !selectedLanguage.startsWith("Eng")) {
                    Text(
                        text = scheme.titleEng,
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Normal
                    )
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))

                // Max Benefit Amount Banner Highlight
                Surface(
                    color = EmeraldContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "अधिकतम लाभ / राशि (Maximum Benefit)",
                                    fontSize = 11.sp,
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = scheme.maxBenefitAmount,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                            }
                        }

                        Surface(
                            color = EmeraldGreen,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "98% मैच",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick Overview Metadata Badges Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OverviewInfoChip(
                icon = Icons.Default.Category,
                label = "श्रेणी (Category)",
                value = "${scheme.category.icon} ${scheme.category.displayNameHindi}",
                modifier = Modifier.weight(1f)
            )
            OverviewInfoChip(
                icon = Icons.Default.Public,
                label = "क्षेत्र (Jurisdiction)",
                value = scheme.state,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OverviewInfoChip(
                icon = Icons.Default.Schedule,
                label = "आवेदन विंडो",
                value = scheme.applicationWindow,
                modifier = Modifier.weight(1f)
            )
            OverviewInfoChip(
                icon = Icons.Default.Verified,
                label = "सत्यापित तिथि",
                value = scheme.lastVerifiedDate,
                modifier = Modifier.weight(1f)
            )
        }

        // Scheme Overview Description Section
        DetailSectionCard(
            title = "📖 योजना का विवरण (Scheme Overview)",
            icon = Icons.Default.Description,
            iconTint = SaffronPrimary
        ) {
            Text(
                text = displayDesc,
                fontSize = 14.sp,
                color = Color(0xFF334155),
                lineHeight = 22.sp
            )
        }

        // Benefits Section (लाभ एवं विशेषताएं)
        DetailSectionCard(
            title = "🎁 प्रमुख लाभ (Key Benefits)",
            icon = Icons.Default.CardGiftcard,
            iconTint = EmeraldGreen
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                scheme.benefits.forEachIndexed { index, benefit ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldContainer,
                            modifier = Modifier
                                .size(22.dp)
                                .padding(top = 2.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(
                            text = benefit,
                            fontSize = 14.sp,
                            color = Color(0xFF1E293B),
                            lineHeight = 20.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Eligibility Criteria Section (पात्रता मापदंड)
        DetailSectionCard(
            title = "✅ पात्रता मापदंड (Eligibility Criteria)",
            icon = Icons.Default.CheckCircle,
            iconTint = VerifiedCsrBlue
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                scheme.eligibilityCriteria.forEach { criterion ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Rule,
                            contentDescription = null,
                            tint = VerifiedCsrBlue,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Text(
                            text = criterion,
                            fontSize = 14.sp,
                            color = Color(0xFF1E293B),
                            lineHeight = 20.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Required Documents Section (आवश्यक दस्तावेज)
        DetailSectionCard(
            title = "📄 आवश्यक दस्तावेज (Required Documents)",
            icon = Icons.Default.FolderSpecial,
            iconTint = SaffronPrimary
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                scheme.documentsRequired.forEach { doc ->
                    Surface(
                        color = Color(0xFFFFF7ED),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SaffronContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.InsertDriveFile,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = doc,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = NavyDeep
                            )
                        }
                    }
                }
            }
        }

        // Step-By-Step Application Guide Section
        if (scheme.howToApplySteps.isNotEmpty()) {
            DetailSectionCard(
                title = "📝 आवेदन कैसे करें (How to Apply)",
                icon = Icons.Default.FormatListNumbered,
                iconTint = NavyLight
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    scheme.howToApplySteps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = NavyDeep,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = step,
                                fontSize = 14.sp,
                                color = Color(0xFF1E293B),
                                lineHeight = 20.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons: Apply Online & CSC Slip Generation
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = WarmSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "🌐 आधिकारिक आवेदन व सहायता (Official Application)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDeep
                )

                // Primary Apply Online Button
                Button(
                    onClick = {
                        openOfficialPortal(context, scheme.officialUrl)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("apply_now_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "आधिकारिक पोर्टल पर आवेदन करें (Apply Online)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // CSC Slip Generator Button (Offline CSC Centre helper)
                if (onGenerateCscSlipClick != null) {
                    OutlinedButton(
                        onClick = { onGenerateCscSlipClick(scheme) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyDeep),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, NavyDeep),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("generate_csc_slip_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = NavyDeep,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CSC केंद्र पर्ची बनाएं (Generate CSC Slip)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "🔗 आधिकारिक वेब: ${scheme.officialUrl}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun OverviewInfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = WarmSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SaffronPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDeep,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = iconTint.copy(alpha = 0.12f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDeep
                )
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            content()
        }
    }
}

private fun openOfficialPortal(context: Context, url: String) {
    try {
        val validUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else {
            url
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "वेबसाइट खोलने में त्रुटि: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}

private fun shareSchemeDetails(context: Context, scheme: Scheme, title: String) {
    try {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "🏛️ *${title}*\n\n" +
                        "💡 *विवरण*: ${scheme.shortDescriptionHindi}\n" +
                        "💰 *लाभ*: ${scheme.maxBenefitAmount}\n" +
                        "🌐 *आधिकारिक आवेदन पोर्टल*: ${scheme.officialUrl}\n\n" +
                        "📱 'जन कल्याण योजना मित्र' ऐप द्वारा साझा किया गया।"
            )
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareIntent, "योजना साझा करें"))
    } catch (e: Exception) {
        Toast.makeText(context, "शेयर करने में समस्या हुई", Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun SchemeDetailViewPreview() {
    SchemeDetailView(
        scheme = MOCK_GOVT_SCHEME_DETAIL
    )
}
