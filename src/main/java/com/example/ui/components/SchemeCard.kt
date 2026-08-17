package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.theme.NavyLight
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.UnverifiedAlertRed
import com.example.ui.theme.VerifiedCsrBlue
import com.example.ui.theme.VerifiedGovtGreen
import com.example.util.LanguageTranslator
import com.example.util.LocalAppLanguage

@Composable
fun SchemeCard(
    scheme: Scheme,
    selectedLanguage: String = LocalAppLanguage.current,
    onReadAloudClick: (String) -> Unit,
    onGenerateCscSlipClick: ((Scheme) -> Unit)? = null,
    onToggleSaveClick: (String, Boolean) -> Unit,
    onToggleAlertClick: ((String, Boolean) -> Unit)? = null,
    onSchemeClick: ((Scheme) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val localizedTitle = LanguageTranslator.getLocalizedTitle(scheme, selectedLanguage)
    val localizedDesc = LanguageTranslator.getLocalizedShortDesc(scheme, selectedLanguage)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (onSchemeClick != null) {
                    onSchemeClick(scheme)
                } else {
                    expanded = !expanded
                }
            }
            .testTag("scheme_card_${scheme.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row: Verification Badge & Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Verification Badge
                val (badgeBg, badgeText, badgeIconColor) = when (scheme.verificationType) {
                    VerificationType.OFFICIAL_GOVT -> Triple(
                        Color(0xFFE8F5E9),
                        "🟢 Official Govt (.gov.in)",
                        VerifiedGovtGreen
                    )
                    VerificationType.VERIFIED_CSR -> Triple(
                        Color(0xFFE3F2FD),
                        "🔵 Verified CSR (Private)",
                        VerifiedCsrBlue
                    )
                    VerificationType.UNVERIFIED -> Triple(
                        Color(0xFFFFEBEE),
                        "⚠️ Unverified / Suspect Link",
                        UnverifiedAlertRed
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeIconColor
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Match Score Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFF3E0))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🎯 ${scheme.matchPercentage}% Match",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Bookmark Icon Button
                    IconButton(
                        onClick = {
                            val nextSavedState = !scheme.isSaved
                            onToggleSaveClick(scheme.id, scheme.isSaved)
                            val msg = if (nextSavedState) "🔖 योजना सेव कर ली गई!" else "🗑️ योजना हटा दी गई।"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("scheme_bookmark_${scheme.id}")
                    ) {
                        Icon(
                            imageVector = if (scheme.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Scheme",
                            tint = if (scheme.isSaved) SaffronPrimary else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scheme Titles
            Text(
                text = localizedTitle,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldGreen,
                lineHeight = 22.sp
            )

            if (scheme.titleEng.isNotBlank() && scheme.titleEng != localizedTitle && !selectedLanguage.lowercase().contains("english")) {
                Text(
                    text = scheme.titleEng,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = "🏛️ ${scheme.ministryOrOrganization}",
                fontSize = 11.5.sp,
                color = NavyLight,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Short Description
            Text(
                text = localizedDesc,
                fontSize = 13.sp,
                color = Color(0xFF333333),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Application Dates, Status & Qualification Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Application Window Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (scheme.isApplicationOpen) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (scheme.isApplicationOpen) "🟢 ${scheme.applicationWindow}" else "⏳ ${scheme.applicationWindow}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (scheme.isApplicationOpen) Color(0xFF2E7D32) else Color(0xFFE65100)
                    )
                }

                // Qualification Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE1F5FE))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🎓 ${scheme.targetQualification}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0277BD)
                    )
                }
            }

            // Portal Notification Alert Button for Closed/Upcoming Schemes
            if (!scheme.isApplicationOpen || scheme.isAlertSubscribed) {
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        val nextState = !scheme.isAlertSubscribed
                        onToggleAlertClick?.invoke(scheme.id, nextState)
                        val msg = if (nextState) "🔔 अलर्ट सेट किया गया! पोर्टल खुलते ही आपको नोटिफिकेशन मिलेगा।" else "🔕 अलर्ट हटा दिया गया।"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (scheme.isAlertSubscribed) Color(0xFF2E7D32) else Color(0xFFE65100)
                    )
                ) {
                    Icon(
                        imageVector = if (scheme.isAlertSubscribed) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                        contentDescription = "Alert",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (scheme.isAlertSubscribed) "🔔 पोर्टल अलर्ट सक्रिय है (Alert Active)" else "🔔 आवेदन पोर्टल शुरू होने पर अलर्ट पाएँ (Set Opening Alert)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Benefit Tag Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LanguageTranslator.getLocalizedText("max_benefit", selectedLanguage),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerifiedGovtGreen
                    )
                    Text(
                        text = scheme.maxBenefitAmount,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldGreen
                    )
                }
            }

            // Expandable details section
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(
                        text = "📋 ${LanguageTranslator.getLocalizedText("key_benefits", selectedLanguage)}:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = EmeraldGreen
                    )
                    scheme.benefits.forEach { benefit ->
                        Row(modifier = Modifier.padding(top = 4.dp)) {
                            Text(text = "• ", fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            Text(text = benefit, fontSize = 12.sp, color = Color(0xFF424242))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "✅ ${LanguageTranslator.getLocalizedText("who_is_eligible", selectedLanguage)}:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = EmeraldGreen
                    )
                    scheme.eligibilityCriteria.forEach { crit ->
                        Row(modifier = Modifier.padding(top = 4.dp)) {
                            Text(text = "• ", fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            Text(text = crit, fontSize = 12.sp, color = Color(0xFF424242))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "📄 ${LanguageTranslator.getLocalizedText("documents_required", selectedLanguage)}:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = EmeraldGreen
                    )
                    scheme.documentsRequired.forEach { doc ->
                        Row(modifier = Modifier.padding(top = 4.dp)) {
                            Text(text = "• ", fontWeight = FontWeight.Bold, color = SaffronPrimary)
                            Text(text = doc, fontSize = 12.sp, color = Color(0xFF424242))
                        }
                    }

                    if (scheme.howToApplySteps.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "💡 ${LanguageTranslator.getLocalizedText("how_to_apply", selectedLanguage)}:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NavyLight
                        )
                        scheme.howToApplySteps.forEachIndexed { index, step ->
                            Row(modifier = Modifier.padding(top = 4.dp)) {
                                Text(text = "${index + 1}. ", fontWeight = FontWeight.Bold, color = NavyLight)
                                Text(text = step, fontSize = 12.sp, color = Color(0xFF424242))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Official Website Apply Button
            if (scheme.officialUrl.isNotBlank()) {
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scheme.officialUrl))
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "Error opening official portal", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerifiedCsrBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Official Website",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🌐 ${LanguageTranslator.getLocalizedText("apply_online", selectedLanguage)}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bottom Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 🔊 Read Aloud Button
                OutlinedButton(
                    onClick = {
                        val fullSpeechText = "$localizedTitle. $localizedDesc. ${LanguageTranslator.getLocalizedText("max_benefit", selectedLanguage)}: ${scheme.maxBenefitAmount}."
                        onReadAloudClick(fullSpeechText)
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SaffronPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Read Aloud",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = LanguageTranslator.getLocalizedText("read_aloud", selectedLanguage),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 🔖 Bookmark / View Details Button
                Button(
                    onClick = {
                        if (onSchemeClick != null) {
                            onSchemeClick(scheme)
                        } else {
                            val nextSavedState = !scheme.isSaved
                            onToggleSaveClick(scheme.id, scheme.isSaved)
                            val msg = if (nextSavedState) "🔖 ${LanguageTranslator.getLocalizedText("saved", selectedLanguage)}" else "🗑️ Removed"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
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
                        text = if (scheme.isSaved) LanguageTranslator.getLocalizedText("saved", selectedLanguage) else LanguageTranslator.getLocalizedText("save_scheme", selectedLanguage),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Expand / Collapse details text link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (onSchemeClick != null) {
                            onSchemeClick(scheme)
                        } else {
                            expanded = !expanded
                        }
                    }
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) LanguageTranslator.getLocalizedText("less_details", selectedLanguage) else LanguageTranslator.getLocalizedText("more_details", selectedLanguage),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyLight
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle Expand",
                    tint = NavyLight,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
