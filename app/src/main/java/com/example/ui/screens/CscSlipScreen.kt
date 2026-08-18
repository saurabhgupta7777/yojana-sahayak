package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode2
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NavyLight
import com.example.ui.theme.SaffronPrimary

@Composable
fun CscSlipScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeSlip by viewModel.activeSlip.collectAsState()
    val savedCscSlips by viewModel.savedCscSlips.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val slipToShow = activeSlip ?: savedCscSlips.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
    ) {
        // Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmeraldGreen)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🖨️ योजना आवेदन पर्ची (Application Slip)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        if (slipToShow == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📑", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "कोई सीएससी पर्ची तैयार नहीं है",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "किसी भी योजना कार्ड से 'CSC पर्ची' बटन दबाएं।",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Official Printable Slip Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("csc_printable_slip_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Card Header Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFF3E0))
                                .border(1.5.dp, SaffronPrimary, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "🇮🇳 अधिकारिक योजना आवेदन पर्ची (Application Slip)",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = SaffronPrimary,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Yojana Sahayak Verification System • CSC Slip #${slipToShow.slipId}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Applicant Information Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "आवेदक का नाम:", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = slipToShow.applicantName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = EmeraldGreen
                                )
                            }

                            Column {
                                Text(text = "तारीख व समय:", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = slipToShow.generatedDateFormatted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = NavyLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "योजना वर्ग:", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = "${slipToShow.selectedCategory.icon} ${slipToShow.selectedCategory.displayNameHindi}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Column {
                                Text(text = "लाभार्थी:", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = "${slipToShow.recipientType.icon} ${slipToShow.recipientType.displayNameHindi}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFE0E0E0))
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Matched Schemes List
                        Text(
                            text = "🎯 चयनित पात्र योजनाएं (${slipToShow.matchedSchemesCount}):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EmeraldGreen
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        slipToShow.matchedSchemeTitles.forEach { title ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Check",
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF212121)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Document Checklist Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "📄 आवश्यक दस्तावेज़ चेकलिस्ट (साथ ले जाएं):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = EmeraldGreen
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                slipToShow.documentChecklist.forEach { doc ->
                                    Text(
                                        text = "☑️ $doc",
                                        fontSize = 12.sp,
                                        color = Color(0xFF2E7D32),
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nearest CSC Instructions
                        Text(
                            text = "💡 निर्देश: ${slipToShow.nearestCscNote}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE65100),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // QR / Verification Code Graphic Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF5F5F5))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = "QR Code",
                                    modifier = Modifier.size(48.dp),
                                    tint = NavyLight
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "CSC QR Verification Standard",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = NavyLight
                                    )
                                    Text(
                                        text = slipToShow.verificationQrCodeString,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val speechText = "जन सेवा केंद्र पर्ची. आवेदक: ${slipToShow.applicantName}. योजनाएं: ${slipToShow.matchedSchemeTitles.joinToString(", ")}. आवश्यक दस्तावेज़: ${slipToShow.documentChecklist.joinToString(", ")}."
                            viewModel.speakText(speechText)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SaffronPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Read Aloud",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🔊 बोलकर सुनें", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "🖨️ CSC पर्ची डाउनलोड हुई! (#${slipToShow.slipId})", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download Slip",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🖨️ पर्ची डाउनलोड", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
