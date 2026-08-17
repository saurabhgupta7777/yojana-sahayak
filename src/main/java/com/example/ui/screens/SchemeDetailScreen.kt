package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import kotlinx.coroutines.delay

@Composable
fun SchemeDetailScreen(
    scheme: Scheme,
    selectedLanguage: String = "Hindi (हिंदी)",
    onBackClick: () -> Unit,
    onReadAloudClick: (String) -> Unit,
    onGenerateCscSlipClick: (Scheme) -> Unit,
    onToggleSaveClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val localizedTitle = LanguageTranslator.getLocalizedTitle(scheme, selectedLanguage)
    val localizedDesc = LanguageTranslator.getLocalizedShortDesc(scheme, selectedLanguage)

    var isContentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        isContentVisible = true
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("scheme_detail_screen"),
        topBar = {
            Surface(
                shadowElevation = 4.dp,
                color = EmeraldGreen
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("detail_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "वापस जाएं (Back)",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Text(
                            text = LanguageTranslator.getLocalizedText("scheme_details_title", selectedLanguage),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Bookmark Toggle
                        IconButton(
                            onClick = {
                                val nextState = !scheme.isSaved
                                onToggleSaveClick(scheme.id, scheme.isSaved)
                                val msg = if (nextState) "🔖 ${LanguageTranslator.getLocalizedText("saved", selectedLanguage)}" else "🗑️ Removed"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("detail_top_bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (scheme.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save Scheme",
                                tint = if (scheme.isSaved) SaffronPrimary else Color.White
                            )
                        }

                        // Share
                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "🏛️ $localizedTitle\n💰 Benefit: ${scheme.maxBenefitAmount}\n🌐 Portal: ${scheme.officialUrl}\n\nShared via ${LanguageTranslator.getLocalizedText("app_name", selectedLanguage)}"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Scheme"))
                            },
                            modifier = Modifier.testTag("detail_top_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = Color.White,
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Apply Online CTA Button
                    Button(
                        onClick = {
                            if (scheme.officialUrl.isNotBlank()) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scheme.officialUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Unable to open official portal", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Official portal URL not available", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("detail_bottom_apply_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerifiedCsrBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Apply Online",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = LanguageTranslator.getLocalizedText("apply_online", selectedLanguage),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Generate CSC Slip Button
                        Button(
                            onClick = { onGenerateCscSlipClick(scheme) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("detail_bottom_csc_slip_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = "CSC Slip",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = LanguageTranslator.getLocalizedText("csc_slip", selectedLanguage),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Listen Aloud Button
                        OutlinedButton(
                            onClick = {
                                val fullSpeechText = "$localizedTitle. $localizedDesc. ${LanguageTranslator.getLocalizedText("max_benefit", selectedLanguage)}: ${scheme.maxBenefitAmount}."
                                onReadAloudClick(fullSpeechText)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("detail_bottom_listen_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, SaffronPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Listen",
                                tint = SaffronPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = LanguageTranslator.getLocalizedText("read_aloud", selectedLanguage),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = isContentVisible,
            enter = fadeIn(spring(stiffness = Spring.StiffnessLow)) + slideInVertically(
                initialOffsetY = { 60 },
                animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF7FAFC))
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Header Category & Verification Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Chip
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SaffronPrimary.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = scheme.category.icon, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = LanguageTranslator.getLocalizedCategory(scheme.category, selectedLanguage),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary
                            )
                        }
                    }

                    // Verification Pill
                    val (badgeText, badgeColor, icon) = when (scheme.verificationType) {
                        VerificationType.OFFICIAL_GOVT -> Triple(LanguageTranslator.getLocalizedText("central_state", selectedLanguage), VerifiedGovtGreen, Icons.Default.CheckCircle)
                        VerificationType.VERIFIED_CSR -> Triple("🔵 CSR Verified", VerifiedCsrBlue, Icons.Default.CheckCircle)
                        VerificationType.UNVERIFIED -> Triple("⚠️ Non-Official", UnverifiedAlertRed, Icons.Default.Info)
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = badgeColor.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = badgeText, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scheme Title
                Text(
                    text = localizedTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A),
                    lineHeight = 28.sp
                )

                if (scheme.titleEng.isNotBlank() && scheme.titleEng != localizedTitle && !selectedLanguage.lowercase().contains("english")) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = scheme.titleEng,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Ministry / Department
                Text(
                    text = "🏛️ ${scheme.ministryOrOrganization} • ${scheme.state}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyLight
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Benefit Card with Gradient Accent
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        VerifiedGovtGreen.copy(alpha = 0.12f),
                                        Color(0xFFE8F5E9).copy(alpha = 0.3f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = LanguageTranslator.getLocalizedText("max_benefit", selectedLanguage),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VerifiedGovtGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = scheme.maxBenefitAmount,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldGreen
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Text(
                                    text = "📅 ${scheme.applicationWindow}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = LanguageTranslator.getLocalizedText("overview", selectedLanguage),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = localizedDesc,
                            fontSize = 14.sp,
                            color = Color(0xFF334155),
                            lineHeight = 22.sp
                        )

                        if (scheme.detailedDescriptionHindi.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = scheme.detailedDescriptionHindi,
                                fontSize = 13.5.sp,
                                color = Color(0xFF475569),
                                lineHeight = 21.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Key Benefits
                if (scheme.benefits.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = LanguageTranslator.getLocalizedText("key_benefits", selectedLanguage),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            scheme.benefits.forEach { benefit ->
                                Row(
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = benefit,
                                        fontSize = 13.5.sp,
                                        color = Color(0xFF1E293B),
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Eligibility Criteria
                if (scheme.eligibilityCriteria.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = LanguageTranslator.getLocalizedText("who_is_eligible", selectedLanguage),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            scheme.eligibilityCriteria.forEach { crit ->
                                Row(
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 6.dp)
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(SaffronPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = crit,
                                        fontSize = 13.5.sp,
                                        color = Color(0xFF1E293B),
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Documents Required
                if (scheme.documentsRequired.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = LanguageTranslator.getLocalizedText("documents_required", selectedLanguage),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDeep
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            scheme.documentsRequired.forEach { doc ->
                                Row(
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = SaffronPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = doc,
                                        fontSize = 13.5.sp,
                                        color = Color(0xFF334155),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // How To Apply Steps
                if (scheme.howToApplySteps.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = LanguageTranslator.getLocalizedText("how_to_apply", selectedLanguage),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyLight
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            scheme.howToApplySteps.forEachIndexed { index, step ->
                                Row(
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = NavyLight.copy(alpha = 0.12f),
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NavyLight
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = step,
                                        fontSize = 13.5.sp,
                                        color = Color(0xFF1E293B),
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
