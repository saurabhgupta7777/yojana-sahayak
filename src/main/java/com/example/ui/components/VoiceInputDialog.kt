package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SaffronPrimary
import com.example.util.SpeechToText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceInputDialog(
    selectedLanguage: String = "Hindi (हिंदी)",
    isListening: Boolean,
    onDismissRequest: () -> Unit,
    onSubmitQuery: (String) -> Unit,
    onRetryVoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var queryText by remember { mutableStateOf("") }
    var isLocalListening by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("माइक दबाकर बोलें या विकल्प चुनें") }
    val scrollState = rememberScrollState()

    val speechRecognizerUtil = remember {
        SpeechToText(
            context = context,
            onResult = { text ->
                isLocalListening = false
                queryText = text
                statusMessage = "✅ पहचाना गया: \"$text\""
                Toast.makeText(context, "🎤 $text", Toast.LENGTH_SHORT).show()
                onSubmitQuery(text)
            },
            onError = { error ->
                isLocalListening = false
                statusMessage = error
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizerUtil.destroy()
        }
    }

    val langCode = when {
        selectedLanguage.contains("Hindi") -> "hi-IN"
        selectedLanguage.contains("Bengali") -> "bn-IN"
        selectedLanguage.contains("Telugu") -> "te-IN"
        selectedLanguage.contains("Marathi") -> "mr-IN"
        selectedLanguage.contains("Tamil") -> "ta-IN"
        selectedLanguage.contains("Gujarati") -> "gu-IN"
        selectedLanguage.contains("Kannada") -> "kn-IN"
        selectedLanguage.contains("Malayalam") -> "ml-IN"
        selectedLanguage.contains("Punjabi") -> "pa-IN"
        selectedLanguage.contains("Odia") -> "or-IN"
        selectedLanguage.contains("Urdu") -> "ur-IN"
        selectedLanguage.contains("English") -> "en-IN"
        else -> "hi-IN"
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isLocalListening = false
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spokenMatches = result.data?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
            if (!spokenMatches.isNullOrEmpty()) {
                val recognizedText = spokenMatches[0]
                queryText = recognizedText
                statusMessage = "✅ पहचाना गया: \"$recognizedText\""
                Toast.makeText(context, "🎤 $recognizedText", Toast.LENGTH_SHORT).show()
                onSubmitQuery(recognizedText)
            }
        }
    }

    val startVoiceRecognition = {
        isLocalListening = true
        statusMessage = "🔴 सुन रहे हैं... बोलिए!"

        val intent = speechRecognizerUtil.createSpeechIntent(langCode, "बोलिए... (जैसे: किसान योजना, स्कॉलरशिप)")
        if (intent.resolveActivity(context.packageManager) != null) {
            try {
                speechLauncher.launch(intent)
            } catch (e: Exception) {
                speechRecognizerUtil.startListening(langCode)
            }
        } else {
            speechRecognizerUtil.startListening(langCode)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startVoiceRecognition()
        } else {
            statusMessage = "🎙️ माइक्रोफोन अनुमति आवश्यक है"
            Toast.makeText(context, "🎙️ माइक्रोफोन अनुमति आवश्यक है", Toast.LENGTH_SHORT).show()
        }
    }

    val handleMicTap = {
        val permissionCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            if (isLocalListening) {
                speechRecognizerUtil.stopListening()
                isLocalListening = false
                statusMessage = "माइक बंद किया गया"
            } else {
                startVoiceRecognition()
            }
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Trigger on initial open safely
    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            startVoiceRecognition()
        }
    }

    val activeListening = isListening || isLocalListening

    val infiniteTransition = rememberInfiniteTransition(label = "voice_dialog_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val quickQueries = remember {
        listOf(
            "🌾 पीएम किसान 19वीं किस्त",
            "🎓 राष्ट्रीय छात्रवृत्ति 2026",
            "🏥 आयुष्मान कार्ड 5 लाख",
            "👩 लाडली बहना योजना",
            "👵 वृद्धावस्था पेंशन योजना",
            "🏠 पीएम आवास योजना ग्रामीण",
            "🪪 आधार कार्ड बायोमेट्रिक",
            "⚡ पीएम सूर्य घर मुफ्त बिजली"
        )
    }

    AlertDialog(
        onDismissRequest = {
            speechRecognizerUtil.cancel()
            onDismissRequest()
        },
        modifier = modifier.testTag("voice_input_dialog"),
        shape = RoundedCornerShape(24.dp),
        containerColor = Color(0xFFF8FAFC),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SaffronPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = "Voice Search",
                            tint = SaffronPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "🎤 बोलकर खोजें (Voice Search)",
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = statusMessage,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (activeListening) Color(0xFFE53E3E) else Color(0xFF64748B)
                        )
                    }
                }

                IconButton(onClick = {
                    speechRecognizerUtil.cancel()
                    onDismissRequest()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // Microphone Big Action Center
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (activeListening) Color(0xFFE53E3E) else SaffronPrimary
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .scale(if (activeListening) pulseScale else 1.0f)
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(if (activeListening) Color(0xFFE53E3E) else Color(0xFF1B2A4A))
                                .clickable { handleMicTap() }
                                .testTag("dialog_mic_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Mic",
                                tint = SaffronPrimary,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (activeListening) "🔴 आवाज सुन रहे हैं... बोलिए!" else "माइक दबाकर बोलें (Tap to Speak)",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeListening) Color(0xFFE53E3E) else EmeraldGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Input Field for Voice Transcript / Manual Text
                OutlinedTextField(
                    value = queryText,
                    onValueChange = { queryText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_query_input"),
                    placeholder = {
                        Text("योजना का नाम बोलें या लिखें...", fontSize = 13.sp, color = Color.Gray)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = SaffronPrimary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Spoken Queries List
                Text(
                    text = "💡 लोकप्रिय योजनाएं (Popular Searches):",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    quickQueries.forEach { query ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier.clickable {
                                val cleanText = query.substringAfter(" ").trim()
                                queryText = cleanText
                                onSubmitQuery(cleanText)
                            }
                        ) {
                            Text(
                                text = query,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (queryText.isNotBlank()) {
                        onSubmitQuery(queryText.trim())
                    } else {
                        handleMicTap()
                    }
                },
                modifier = Modifier.testTag("dialog_submit_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (queryText.isNotBlank()) "खोजें (Search)" else "बोलकर खोजें", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                speechRecognizerUtil.cancel()
                onDismissRequest()
            }) {
                Text("बंद करें", color = Color.Gray)
            }
        }
    )
}
