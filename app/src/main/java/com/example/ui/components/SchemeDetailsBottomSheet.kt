package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Scheme
import com.example.data.model.VerificationType
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyLight
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.UnverifiedAlertRed
import com.example.ui.theme.VerifiedCsrBlue
import com.example.ui.theme.VerifiedGovtGreen
import com.example.util.LanguageTranslator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchemeDetailsBottomSheet(
    scheme: Scheme,
    selectedLanguage: String = "Hindi (हिंदी)",
    onDismissRequest: () -> Unit,
    onReadAloudClick: (String) -> Unit,
    onGenerateCscSlipClick: ((Scheme) -> Unit)? = null,
    onToggleSaveClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val localizedTitle = LanguageTranslator.getLocalizedTitle(scheme, selectedLanguage)
    val localizedDesc = LanguageTranslator.getLocalizedShortDesc(scheme, selectedLanguage)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color(0xFFF8FAFC),
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp),
                color = Color.LightGray,
                shape = CircleShape
            ) {}
        },
        modifier = modifier.testTag("scheme_details_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            // Top Bar: Category/Badge & Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SaffronPrimary.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = scheme.category.icon, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (selectedLanguage.lowercase().contains("english")) scheme.category.displayNameEng else scheme.category.displayNameHindi,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Bookmark Toggle Button
                    IconButton(
                        onClick = {
                            val nextSaveState = !scheme.isSaved
                            onToggleSaveClick(scheme.id, scheme.isSaved)
                            val msg = if (nextSaveState) "🔖 योजना सेव कर ली गई!" else "🗑️ योजना हटा दी गई।"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("details_bookmark_button")
                    ) {
                        Icon(
                            imageVector = if (scheme.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (scheme.isSaved) SaffronPrimary else Color.Gray
                        )
                    }

                    // Share Button
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "🏛️ ${scheme.titleHindi}\n💰 ${scheme.maxBenefitAmount}\n🌐 ${scheme.officialUrl}\n\nयोजना सहायक ऐप द्वारा साझा किया गया"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Scheme Details"))
                        },
                        modifier = Modifier.testTag("details_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = NavyLight
                        )
                    }

                    // Close Button
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("details_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
                // Verification Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    val (badgeText, badgeColor, icon) = when (scheme.verificationType) {
                        VerificationType.OFFICIAL_GOVT -> Triple("🟢 " + LanguageTranslator.getLocalizedText("verified_schemes", selectedLanguage), VerifiedGovtGreen, Icons.Default.CheckCircle)
                        VerificationType.VERIFIED_CSR -> Triple("🔵 CSR Verified", VerifiedCsrBlue, Icons.Default.CheckCircle)
                        VerificationType.UNVERIFIED -> Triple("⚠️ Non-Official / Unverified", UnverifiedAlertRed, Icons.Default.Info)
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = "Verification Status",
                        tint = badgeColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = badgeText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${scheme.state}",
                        fontSize = 11.5.sp,
                        color = Color.Gray
                    )
                }

                // Scheme Main Title
                Text(
                    text = localizedTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EmeraldGreen,
                    lineHeight = 26.sp
                )

                if (scheme.titleEng.isNotBlank() && scheme.titleEng != localizedTitle && !selectedLanguage.lowercase().contains("english")) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = scheme.titleEng,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Ministry / Organization
                Text(
                    text = "🏛️ ${scheme.ministryOrOrganization}",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyLight
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Max Benefit Hero Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = VerifiedGovtGreen.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VerifiedGovtGreen.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = LanguageTranslator.getLocalizedText("max_benefit", selectedLanguage),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = VerifiedGovtGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = scheme.maxBenefitAmount,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldGreen
                            )
                        }

                        // Application Window Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            Text(
                                text = scheme.applicationWindow,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Short Description / Overview
                Text(
                    text = localizedDesc,
                    fontSize = 14.sp,
                    color = Color(0xFF334155),
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Normal
                )

                if (scheme.detailedDescriptionHindi.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = scheme.detailedDescriptionHindi,
                        fontSize = 13.5.sp,
                        color = Color(0xFF475569),
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(16.dp))

                // Key Benefits Section
                if (scheme.benefits.isNotEmpty()) {
                    Text(
                        text = "📋 मुख्य लाभ व विशेषताएं (Key Benefits)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = EmeraldGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    scheme.benefits.forEach { benefit ->
                        Row(
                            modifier = Modifier.padding(bottom = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = benefit,
                                fontSize = 13.sp,
                                color = Color(0xFF1E293B),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Eligibility Criteria Section
                if (scheme.eligibilityCriteria.isNotEmpty()) {
                    Text(
                        text = "✅ पात्रता मानदंड (Eligibility Criteria)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = EmeraldGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    scheme.eligibilityCriteria.forEach { crit ->
                        Row(
                            modifier = Modifier.padding(bottom = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(SaffronPrimary)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = crit,
                                fontSize = 13.sp,
                                color = Color(0xFF1E293B),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Required Documents Section
                if (scheme.documentsRequired.isNotEmpty()) {
                    Text(
                        text = "📄 आवश्यक दस्तावेज सूची (Documents Required)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NavyDeep
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    scheme.documentsRequired.forEach { doc ->
                        Row(
                            modifier = Modifier.padding(bottom = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = doc,
                                fontSize = 13.sp,
                                color = Color(0xFF334155)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // How To Apply Steps
                if (scheme.howToApplySteps.isNotEmpty()) {
                    Text(
                        text = "💡 ऑनलाइन/ऑफलाइन आवेदन प्रक्रिया (How to Apply)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NavyLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    scheme.howToApplySteps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.padding(bottom = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = NavyLight.copy(alpha = 0.1f),
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NavyLight
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = step,
                                fontSize = 13.sp,
                                color = Color(0xFF1E293B),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Bottom CTA Section
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                color = Color.Transparent
            ) {
                Column {
                    // Primary 'Apply' CTA Button
                    Button(
                        onClick = {
                            if (scheme.officialUrl.isNotBlank()) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scheme.officialUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "वेबसाइट लिंक खोलने में त्रुटि", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "आधिकारिक पोर्टल यूआरएल उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("apply_now_cta_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerifiedCsrBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Apply Now",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🌐 आधिकारिक पोर्टल पर आवेदन करें",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Secondary Row: Read Aloud & Save Bookmark
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Read Aloud
                        OutlinedButton(
                            onClick = {
                                val fullSpeechText = "$localizedTitle. $localizedDesc. ${LanguageTranslator.getLocalizedText("max_benefit", selectedLanguage)}: ${scheme.maxBenefitAmount}."
                                onReadAloudClick(fullSpeechText)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("details_read_aloud_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Read Aloud",
                                tint = SaffronPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "🔊 सुनें (Speak)",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary
                            )
                        }

                        // Bookmark Save Toggle
                        Button(
                            onClick = {
                                val nextSavedState = !scheme.isSaved
                                onToggleSaveClick(scheme.id, scheme.isSaved)
                                val msg = if (nextSavedState) "🔖 योजना सेव कर ली गई!" else "🗑️ योजना हटा दी गई।"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("details_save_scheme_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (scheme.isSaved) SaffronPrimary else EmeraldGreen
                            )
                        ) {
                            Icon(
                                imageVector = if (scheme.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save Scheme",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (scheme.isSaved) "सेव्ड (Saved)" else "🔖 सेव करें",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
