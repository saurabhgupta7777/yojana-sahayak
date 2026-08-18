package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RecipientType
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SaffronPrimary

@Composable
fun RecipientProfileSelector(
    selectedRecipient: RecipientType,
    onRecipientSelected: (RecipientType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("recipient_selector_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎯",
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "योजना किसके लिए चाहिए? (Recipient Profile)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = EmeraldGreen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RecipientType.values().forEach { recipient ->
                    val isSelected = selectedRecipient == recipient
                    val bgColor = if (isSelected) EmeraldGreen else Color(0xFFF5F5F0)
                    val textColor = if (isSelected) Color.White else Color(0xFF333333)
                    val borderColor = if (isSelected) SaffronPrimary else Color(0xFFE0E0E0)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable { onRecipientSelected(recipient) }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = recipient.icon,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = recipient.displayNameHindi,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = textColor,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
