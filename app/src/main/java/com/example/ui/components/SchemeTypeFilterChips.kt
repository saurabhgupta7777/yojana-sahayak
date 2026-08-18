package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.SchemeTypeFilter
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SaffronPrimary

@Composable
fun SchemeTypeFilterChips(
    selectedSchemeType: SchemeTypeFilter,
    onSchemeTypeSelected: (SchemeTypeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SchemeTypeFilter.values().forEach { type ->
            val isSelected = selectedSchemeType == type
            val activeColor = when (type) {
                SchemeTypeFilter.ALL -> Color(0xFF1B2A4A)
                SchemeTypeFilter.GOVT -> EmeraldGreen
                SchemeTypeFilter.CSR -> Color(0xFF1E88E5)
            }
            val bgColor = if (isSelected) activeColor else Color.White
            val textColor = if (isSelected) Color.White else Color(0xFF2D3748)
            val borderColor = if (isSelected) activeColor else Color(0xFFCBD5E0)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(bgColor)
                    .border(1.5.dp, borderColor, RoundedCornerShape(20.dp))
                    .clickable { onSchemeTypeSelected(type) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("scheme_type_chip_${type.name}"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = type.icon,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = type.displayNameHindi,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}
