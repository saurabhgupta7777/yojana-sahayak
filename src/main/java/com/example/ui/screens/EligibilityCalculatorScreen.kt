package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CitizenCategory
import com.example.data.model.RecipientType
import com.example.ui.MainViewModel
import com.example.ui.components.CategoryFilterChips
import com.example.ui.components.RecipientProfileSelector
import com.example.ui.components.SchemeCard
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SaffronPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EligibilityCalculatorScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val citizenProfile by viewModel.citizenProfile.collectAsState()
    val selectedRecipient by viewModel.selectedRecipient.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val allSchemes by viewModel.allSchemes.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val scrollState = rememberScrollState()

    var applicantName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf(citizenProfile.age.toFloat()) }
    var incomeText by remember { mutableStateOf(citizenProfile.annualIncomeRupees.toString()) }
    var income by remember { mutableStateOf(citizenProfile.annualIncomeRupees.toFloat()) }
    var isBpl by remember { mutableStateOf(citizenProfile.isBplCardHolder) }
    var gender by remember { mutableStateOf(citizenProfile.gender) }
    var qualification by remember { mutableStateOf(citizenProfile.qualification) }
    var socialCategory by remember { mutableStateOf(citizenProfile.socialCategory) }
    var selectedState by remember { mutableStateOf(citizenProfile.state) }

    val genders = listOf("महिला (Female)", "पुरुष (Male)", "अन्य / ट्रांसजेंडर")
    val qualifications = listOf("अनपढ़ / निरक्षर", "10वीं पास", "12वीं पास", "स्नातक (Graduate)", "पोस्ट ग्रेजुएट", "आईटीआई / डिप्लोमा")
    val socialCategories = listOf("General (सामान्य)", "OBC (पिछड़ा वर्ग)", "SC (अनुसूचित जाति)", "ST (अनुसूचित जनजाति)", "EWS")
    val states = listOf("All India (केंद्र सरकार)", "Uttar Pradesh", "Madhya Pradesh", "Bihar", "Rajasthan", "Maharashtra", "Delhi", "Gujarat", "Punjab", "Haryana", "West Bengal")

    // Dynamic Recipient & Category Scheme Filter
    val matchedSchemes = remember(selectedRecipient, selectedCategory, age, income, isBpl, gender, qualification, socialCategory, selectedState, allSchemes) {
        allSchemes.filter { scheme ->
            val matchesCategory = selectedCategory == CitizenCategory.ALL || scheme.category == selectedCategory
            val matchesRecipient = scheme.targetRecipients.contains(selectedRecipient)
            val matchesState = scheme.state == "All India" || selectedState == "All India (केंद्र सरकार)" || scheme.state.contains(selectedState, ignoreCase = true)
            
            // Gender match check
            val matchesGender = if (gender.contains("महिला")) {
                scheme.category == CitizenCategory.WOMEN || scheme.titleHindi.contains("महिला") || scheme.titleHindi.contains("कन्या") || scheme.titleHindi.contains("लाडली") || true
            } else true

            matchesCategory && matchesRecipient && matchesState && matchesGender
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FA))
    ) {
        // Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmeraldGreen)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🎯 पात्रता कैलकुलेटर (Eligibility Assessor)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Assessment Inputs Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("eligibility_form_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "📋 अपनी जानकारी दर्ज करें (Full Profile Filter)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = EmeraldGreen
                    )

                    // Applicant Name
                    OutlinedTextField(
                        value = applicantName,
                        onValueChange = { applicantName = it },
                        label = { Text("आवेदक का नाम (Applicant Name)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
                    )

                    // Recipient Selector
                    Text(text = "1. लाभार्थी प्रोफ़ाइल (Recipient Profile):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                    RecipientProfileSelector(
                        selectedRecipient = selectedRecipient,
                        onRecipientSelected = { viewModel.setRecipient(it) }
                    )

                    // Category Selector
                    Text(text = "2. योजना वर्ग (Scheme Category):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                    CategoryFilterChips(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { viewModel.setCategory(it) }
                    )

                    // Gender Selector
                    Column {
                        Text(text = "3. लिंग (Gender):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            genders.forEach { item ->
                                val selected = gender == item
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) SaffronPrimary else Color(0xFFF0F0F0))
                                        .clickable {
                                            gender = item
                                            viewModel.updateCitizenProfile(citizenProfile.copy(gender = item))
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = item,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }

                    // Qualification Selector
                    Column {
                        Text(text = "4. शैक्षणिक योग्यता (Qualification):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            qualifications.forEach { item ->
                                val selected = qualification == item
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) EmeraldGreen else Color(0xFFF0F0F0))
                                        .clickable {
                                            qualification = item
                                            viewModel.updateCitizenProfile(citizenProfile.copy(qualification = item))
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = item,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }

                    // Social Category (Caste) Selector
                    Column {
                        Text(text = "5. सामाजिक वर्ग (Social Category / Caste):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            socialCategories.forEach { item ->
                                val selected = socialCategory == item
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) SaffronPrimary else Color(0xFFF0F0F0))
                                        .clickable {
                                            socialCategory = item
                                            viewModel.updateCitizenProfile(citizenProfile.copy(socialCategory = item))
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = item,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }

                    // State Selector
                    Column {
                        Text(text = "6. राज्य / केंद्र (State or Central):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(states) { st ->
                                val selected = selectedState == st
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) EmeraldGreen else Color(0xFFF0F0F0))
                                        .clickable {
                                            selectedState = st
                                            viewModel.updateCitizenProfile(citizenProfile.copy(state = st))
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = st,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }

                    // Manual Income Input & Slider Sync
                    Column {
                        Text(text = "7. पारिवारिक वार्षिक आय (Annual Income in ₹):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = incomeText,
                            onValueChange = { input ->
                                incomeText = input
                                input.toLongOrNull()?.let {
                                    income = it.toFloat()
                                    viewModel.updateCitizenProfile(citizenProfile.copy(annualIncomeRupees = it))
                                }
                            },
                            label = { Text("वार्षिक आय मैनुअल लिखें (₹ में लिखें)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
                        )

                        Slider(
                            value = income.coerceIn(10000f, 1000000f),
                            onValueChange = {
                                income = it
                                incomeText = it.toInt().toString()
                                viewModel.updateCitizenProfile(citizenProfile.copy(annualIncomeRupees = it.toLong()))
                            },
                            valueRange = 10000f..1000000f,
                            colors = SliderDefaults.colors(thumbColor = EmeraldGreen, activeTrackColor = EmeraldGreen)
                        )
                    }

                    // Age Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "8. उम्र (Age):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            Text(text = "${age.toInt()} वर्ष", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                        }
                        Slider(
                            value = age,
                            onValueChange = {
                                age = it
                                viewModel.updateCitizenProfile(citizenProfile.copy(age = it.toInt()))
                            },
                            valueRange = 5f..90f,
                            colors = SliderDefaults.colors(thumbColor = SaffronPrimary, activeTrackColor = SaffronPrimary)
                        )
                    }

                    // BPL Ration Card Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "क्या बीपीएल (BPL) / राशन कार्डधारक हैं?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                        Switch(
                            checked = isBpl,
                            onCheckedChange = {
                                isBpl = it
                                viewModel.updateCitizenProfile(citizenProfile.copy(isBplCardHolder = it))
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldGreen, checkedTrackColor = Color(0xFFC8E6C9))
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.generateCscSlipForMultiple(matchedSchemes, applicantName)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Calculate, contentDescription = "Calculate")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "🖨️ पात्र योजनाओं की आवेदन पर्ची बनाएं", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Results Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✨ आपके लिए पात्र योजनाएं (${matchedSchemes.size}):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = EmeraldGreen
                )
            }

            if (matchedSchemes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "कोई योजना मैच नहीं हुई। कृपया श्रेणी या राज्य फ़िल्टर बदलें।",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                matchedSchemes.forEach { scheme ->
                    SchemeCard(
                        scheme = scheme,
                        selectedLanguage = selectedLanguage,
                        onReadAloudClick = { viewModel.speakText(it) },
                        onGenerateCscSlipClick = { viewModel.generateCscSlipForScheme(it, applicantName) },
                        onToggleSaveClick = { id, saved -> viewModel.toggleSaveScheme(id, saved) },
                        onToggleAlertClick = { id, sub -> viewModel.toggleAlertScheme(id, sub) },
                        onSchemeClick = { viewModel.showSchemeDetails(it) }
                    )
                }
            }
        }
    }
}
