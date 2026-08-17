package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppMode
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.SaffronPrimary
import kotlinx.coroutines.delay

/**
 * Data model for slides presented in the top visual app banner carousel.
 */
data class AppSlideItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val badge: String,
    val iconEmoji: String,
    val vectorIcon: ImageVector,
    val gradientColors: List<Color>,
    val actionText: String? = null,
    val targetAppMode: AppMode? = null,
    val isVoiceAction: Boolean = false
)

/**
 * Top horizontal auto-sliding banner carousel introducing core app capabilities
 * (Official Info, AI Voice Assistant, Eligibility Calculator, CSC Center Locator, Bookmark & Apply).
 *
 * @param onNavigateMode Callback to navigate to a target [AppMode] screen.
 * @param onVoiceActionClick Callback triggered when user clicks the Voice Assistant slide CTA button.
 * @param modifier Compose Layout Modifier.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppInfoImageSlider(
    onNavigateMode: (AppMode) -> Unit,
    onVoiceActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val slides = listOf(
        AppSlideItem(
            id = 1,
            title = "🇮🇳 योजना सहायक में स्वागत है",
            subtitle = "भारत सरकार व राज्य सरकारों की 50+ योजनाओं और छात्रवृत्तियों की सटीक जानकारी एक ही जगह पर प्राप्त करें।",
            badge = "100% आधिकारिक जानकारी",
            iconEmoji = "🏛️",
            vectorIcon = Icons.Default.AutoAwesome,
            gradientColors = listOf(Color(0xFF1B2A4A), Color(0xFF0F172A)),
            actionText = null
        ),
        AppSlideItem(
            id = 2,
            title = "🎙️ बोलकर योजनाएं खोजें",
            subtitle = "माइक बटन दबाएं और हिंदी या अपनी भाषा में बोलें। AI तुरंत आपके योग्य सरकारी योजनाएं खोज निकालेगा।",
            badge = "AI वॉइस असिस्टेंट",
            iconEmoji = "🎤",
            vectorIcon = Icons.Default.Mic,
            gradientColors = listOf(Color(0xFF065F46), Color(0xFF047857)),
            actionText = "अभी बोलकर देखें 🎙️",
            isVoiceAction = true
        ),
        AppSlideItem(
            id = 3,
            title = "🎯 पात्रता कैलकुलेटर",
            subtitle = "अपनी उम्र, आय, शिक्षा और श्रेणी दर्ज करके जानें कि आप किन-किन योजनाओं के लिए 100% योग्य हैं।",
            badge = "स्मार्ट मैचिंग",
            iconEmoji = "🧮",
            vectorIcon = Icons.Default.Calculate,
            gradientColors = listOf(Color(0xFF9A3412), Color(0xFFC2410C)),
            actionText = "पात्रता जांचें 🎯",
            targetAppMode = AppMode.ELIGIBILITY_CALCULATOR
        ),
        AppSlideItem(
            id = 4,
            title = "📍 नजदीकी CSC केंद्र खोजें",
            subtitle = "अपने ब्लॉक, पंचायत या शहर के जन सेवा केंद्र (CSC) और सरकारी कार्यालयों का GPS नक्शा व पता पाएं।",
            badge = "GPS लोकेशन",
            iconEmoji = "🗺️",
            vectorIcon = Icons.Default.LocationOn,
            gradientColors = listOf(Color(0xFF1E3A8A), Color(0xFF1D4ED8)),
            actionText = "नजदीकी केंद्र खोजें 📍",
            targetAppMode = AppMode.OFFICE_LOCATOR
        ),
        AppSlideItem(
            id = 5,
            title = "🔖 योजनाएं सेव करें व आवेदन करें",
            subtitle = "पसंदीदा योजनाओं को बुकमार्क करें और आधिकारिक (.gov.in) पोर्टल लिंक से सीधे ऑनलाइन आवेदन करें।",
            badge = "आधिकारिक लिंक",
            iconEmoji = "📑",
            vectorIcon = Icons.Default.Bookmark,
            gradientColors = listOf(Color(0xFF831843), Color(0xFF9D174D)),
            actionText = "सेव की गई योजनाएं देखें 🔖",
            targetAppMode = AppMode.SAVED_ITEMS
        )
    )

    val pagerState = rememberPagerState(pageCount = { slides.size })

    // Auto-scroll effect
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            if (!pagerState.isScrollInProgress) {
                val nextPage = (pagerState.currentPage + 1) % slides.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("app_info_image_slider_container")
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        ) { page ->
            val slide = slides[page]
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp)
                    .clickable {
                        if (slide.isVoiceAction) {
                            onVoiceActionClick()
                        } else if (slide.targetAppMode != null) {
                            onNavigateMode(slide.targetAppMode)
                        }
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(slide.gradientColors)
                        )
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Badge & Icon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.18f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color.White.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = slide.vectorIcon,
                                        contentDescription = null,
                                        tint = SaffronPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = slide.badge,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Slide indicator number e.g. 1/5
                            Text(
                                text = "${page + 1}/${slides.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        // Title & Subtitle
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                text = slide.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                lineHeight = 23.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = slide.subtitle,
                                fontSize = 12.5.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 17.5.sp,
                                maxLines = 3
                            )
                        }

                        // Bottom Action Button or Swipe Prompt
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (slide.actionText != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SaffronPrimary,
                                    modifier = Modifier.clickable {
                                        if (slide.isVoiceAction) {
                                            onVoiceActionClick()
                                        } else if (slide.targetAppMode != null) {
                                            onNavigateMode(slide.targetAppMode)
                                        }
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = slide.actionText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyDeep
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = NavyDeep,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "👉 स्वाइप करें और फीचर्स जानें",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Pager Dots Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            slides.forEachIndexed { index, _ ->
                val isSelected = pagerState.currentPage == index
                val width by animateDpAsState(targetValue = if (isSelected) 22.dp else 7.dp)
                val color = if (isSelected) EmeraldGreen else Color.LightGray

                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(7.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}
