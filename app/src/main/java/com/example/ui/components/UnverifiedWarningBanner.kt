package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.UnverifiedAlertRed

@Composable
fun UnverifiedWarningBanner(
    modifier: Modifier = Modifier,
    customMessage: String? = null
) {
    val messageText = customMessage ?: "⚠️ Ye scheme kisi official sarkari ya verified CSR source se confirm nahi ho payi hai. Kripya dhyan dein aur kisi ko bhi paise ya OTP na dein. Kripya nazdiki Common Service Centre (CSC) ya sarkari karyalaya se confirm karein."

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFF3E0))
            .border(1.5.dp, UnverifiedAlertRed, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Unverified Alert",
                tint = UnverifiedAlertRed
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "सुरक्षा चेतावनी / Safety Warning",
                    fontWeight = FontWeight.Bold,
                    color = UnverifiedAlertRed,
                    fontSize = 14.sp
                )
                Text(
                    text = messageText,
                    fontSize = 13.sp,
                    color = Color(0xFF3E2723),
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
