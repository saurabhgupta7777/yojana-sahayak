package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun QuestionnaireScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val step by viewModel.questionnaireStep.collectAsState()
    val applicantName by viewModel.questionnaireName.collectAsState()
    val age by viewModel.questionnaireAge.collectAsState()
    val income by viewModel.questionnaireIncome.collectAsState()
    val occupation by viewModel.questionnaireOccupation.collectAsState()
    val state by viewModel.questionnaireState.collectAsState()
    val isBpl by viewModel.questionnaireIsBpl.collectAsState()
    val recipient by viewModel.selectedRecipient.collectAsState()
    val category by viewModel.selectedCategory.collectAsState()
    val isChecking by viewModel.isCheckingQuestionnaireEligibility.collectAsState()
    val resultText by viewModel.questionnaireResult.collectAsState()
    val allSchemes by viewModel.allSchemes.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    val scrollState = rememberScrollState()

    val matchedSchemes = remember(category, recipient, income, allSchemes) {
        allSchemes.filter { scheme ->
            val matchesCat = category == CitizenCategory.ALL || scheme.category == category
            val matchesRec = scheme.targetRecipients.contains(recipient)
            val matchesInc = income <= 250000 || scheme.verificationType == com.example.data.model.VerificationType.OFFICIAL_GOVT
            matchesCat || matchesRec || matchesInc
        }.take(4)
    }

    val statesList = listOf("All India", "Uttar Pradesh", "Madhya Pradesh", "Bihar", "Rajasthan", "Maharashtra", "Gujarat", "Tamil Nadu")
    val quickOccupations = listOf("🌾 Kisan (किसान)", "🧵 Worker/Artisan (कारीगर)", "🎓 Student (छात्र)", "🏡 Housewife (गृहणी)", "💼 Small Business (छोटा व्यापारी)")

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "📋 पात्रता प्रश्नावली (AI Assessor)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Step-by-step Scheme Questionnaire",
                            fontSize = 11.sp,
                            color = Color(0xFFC8E6C9)
                        )
                    }
                }

                if (step < 4) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SaffronPrimary)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Step ${step + 1} / 4",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Progress Indicator Bar
        if (step < 4) {
            LinearProgressIndicator(
                progress = { (step + 1) / 4f },
                modifier = Modifier.fillMaxWidth(),
                color = SaffronPrimary,
                trackColor = Color(0xFFFFE0B2)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (step) {
                // ---------------- STEP 0: Personal & Recipient Profile ----------------
                0 -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("questionnaire_card_step_0"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "👤 चरण 1: व्यक्तिगत विवरण (Basic Profile)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = EmeraldGreen
                            )

                            Text(
                                text = "योजना का लाभ किसके लिए प्राप्त करना चाहते हैं?",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )

                            RecipientProfileSelector(
                                selectedRecipient = recipient,
                                onRecipientSelected = { viewModel.setRecipient(it) }
                            )

                            OutlinedTextField(
                                value = applicantName,
                                onValueChange = { viewModel.updateQuestionnaireName(it) },
                                label = { Text("आवेदक का नाम (Applicant Name)") },
                                placeholder = { Text("जैसे: राम शरण / सीता देवी") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("questionnaire_name_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") }
                            )

                            Button(
                                onClick = { viewModel.setQuestionnaireStep(1) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("questionnaire_next_step_0"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                            ) {
                                Text(text = "आगे बढ़ें (Next Step)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                            }
                        }
                    }
                }

                // ---------------- STEP 1: Age & Resident State ----------------
                1 -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("questionnaire_card_step_1"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "🎂 चरण 2: आयु एवं राज्य (Age & Resident State)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = EmeraldGreen
                            )

                            // Age Slider
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "आयु (Age in years):", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = "$age वर्ष", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                                }
                                Slider(
                                    value = age.toFloat(),
                                    onValueChange = { viewModel.updateQuestionnaireAge(it.toInt()) },
                                    valueRange = 5f..95f,
                                    colors = SliderDefaults.colors(thumbColor = SaffronPrimary, activeTrackColor = SaffronPrimary),
                                    modifier = Modifier.testTag("questionnaire_age_slider")
                                )
                            }

                            // State Selector Chips
                            Column {
                                Text(
                                    text = "निवास राज्य (Resident State):",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    statesList.take(4).forEach { st ->
                                        val isSel = state == st
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(if (isSel) EmeraldGreen else Color(0xFFEEEEEE))
                                                .clickable { viewModel.updateQuestionnaireState(st) }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = st,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSel) Color.White else Color.DarkGray
                                            )
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.setQuestionnaireStep(0) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = "पीछे (Back)")
                                }

                                Button(
                                    onClick = { viewModel.setQuestionnaireStep(2) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("questionnaire_next_step_1"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                                ) {
                                    Text(text = "आगे बढ़ें", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // ---------------- STEP 2: Income & Ration Status ----------------
                2 -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("questionnaire_card_step_2"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "💰 चरण 3: वार्षिक आय एवं राशन कार्ड (Financial Status)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = EmeraldGreen
                            )

                            // Annual Income Slider
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "वार्षिक पारिवारिक आय:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = "₹$income", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                                }
                                Slider(
                                    value = income.toFloat(),
                                    onValueChange = { viewModel.updateQuestionnaireIncome(it.toLong()) },
                                    valueRange = 20000f..500000f,
                                    steps = 15,
                                    colors = SliderDefaults.colors(thumbColor = EmeraldGreen, activeTrackColor = EmeraldGreen),
                                    modifier = Modifier.testTag("questionnaire_income_slider")
                                )
                            }

                            // BPL Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "बीपीएल / अंत्योदय राशन कार्ड?", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = "BPL Ration card holder status", fontSize = 11.sp, color = Color.Gray)
                                }
                                Switch(
                                    checked = isBpl,
                                    onCheckedChange = { viewModel.updateQuestionnaireIsBpl(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = EmeraldGreen, checkedTrackColor = Color(0xFFC8E6C9)),
                                    modifier = Modifier.testTag("questionnaire_bpl_switch")
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.setQuestionnaireStep(1) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = "पीछे (Back)")
                                }

                                Button(
                                    onClick = { viewModel.setQuestionnaireStep(3) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("questionnaire_next_step_2"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                                ) {
                                    Text(text = "आगे बढ़ें", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // ---------------- STEP 3: Occupation & Category ----------------
                3 -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("questionnaire_card_step_3"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "💼 चरण 4: व्यवसाय एवं वर्ग (Occupation & Category)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = EmeraldGreen
                            )

                            OutlinedTextField(
                                value = occupation,
                                onValueChange = { viewModel.updateQuestionnaireOccupation(it) },
                                label = { Text("व्यवसाय / कार्य (Occupation)") },
                                placeholder = { Text("जैसे: किसान, दर्जी, मजदूर, छात्र, गृहणी") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("questionnaire_occupation_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Quick Suggestions
                            Column {
                                Text(text = "त्वरित विकल्प (Quick select):", fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    quickOccupations.take(3).forEach { occ ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color(0xFFE8F5E9))
                                                .clickable { viewModel.updateQuestionnaireOccupation(occ) }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(text = occ, fontSize = 11.sp, color = EmeraldGreen, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "नागरिक वर्ग चुनिए (Citizen Category):",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            CategoryFilterChips(
                                selectedCategory = category,
                                onCategorySelected = { viewModel.setCategory(it) }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.setQuestionnaireStep(2) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = "पीछे (Back)")
                                }

                                Button(
                                    onClick = { viewModel.submitQuestionnaireForGeminiCheck() },
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .testTag("questionnaire_submit_btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "Gemini AI")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Gemini AI जांचें", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // ---------------- STEP 4: Results & Gemini Assessment ----------------
                4 -> {
                    if (isChecking) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("questionnaire_loading_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(32.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator(color = SaffronPrimary)
                                Text(
                                    text = "🤖 Gemini AI आपकी पात्रता का विश्लेषण कर रहा है...",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = EmeraldGreen
                                )
                                Text(
                                    text = "आयु, आय, व्यवसाय एवं बीपीएल राशन कार्ड स्थिति के आधार पर सरकारी और सीएसआर योजनाओं का मिलान किया जा रहा है।",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        // AI Report Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("questionnaire_result_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "Gemini AI Result",
                                            tint = SaffronPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "✨ AI पात्रता रिपोर्ट (Eligibility Report)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = EmeraldGreen
                                        )
                                    }

                                    if (!resultText.isNullOrBlank()) {
                                        IconButton(onClick = { viewModel.speakText(resultText!!) }) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Read Aloud",
                                                tint = SaffronPrimary
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = resultText ?: "कोई AI रिपोर्ट उपलब्ध नहीं है।",
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    color = Color.DarkGray
                                )

                                Button(
                                    onClick = {
                                        viewModel.generateCscSlipForMultiple(matchedSchemes, applicantName)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("questionnaire_generate_slip_btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                                ) {
                                    Icon(Icons.Default.Print, contentDescription = "CSC Slip")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "🖨️ इन सभी योजनाओं की CSC पर्ची बनाएं", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Matched Schemes
                        Text(
                            text = "🎯 आपके लिए पात्र योजनाएं (${matchedSchemes.size}):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = EmeraldGreen
                        )

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

                        OutlinedButton(
                            onClick = { viewModel.resetQuestionnaire() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("questionnaire_restart_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Restart")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "🔄 पुनः प्रश्नावली भरें (Restart Questionnaire)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
