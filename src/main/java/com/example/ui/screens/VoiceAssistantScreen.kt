package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SaffronPrimary

@Composable
fun VoiceAssistantScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isListening by viewModel.isListening.collectAsState()
    val isThinking by viewModel.isVoiceAssistantThinking.collectAsState()
    val transcript by viewModel.voiceAssistantTranscript.collectAsState()
    val aiResponse by viewModel.voiceAssistantResponse.collectAsState()

    // Activity Result Launcher for Voice Speech Dialog
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spokenMatches = result.data?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
            if (!spokenMatches.isNullOrEmpty()) {
                val spokenText = spokenMatches[0]
                Toast.makeText(context, "🎤 पहचाना गया: \"$spokenText\"", Toast.LENGTH_SHORT).show()
                viewModel.submitVoiceQuery(spokenText)
            }
        }
    }

    val triggerSpeech = {
        val intent = viewModel.getSpeechRecognitionIntent("अपनी योजना या सहायता का सवाल बोलें...")
        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            viewModel.toggleVoiceInput()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "🎤 माइक्रोफोन अनुमति स्वीकृत", Toast.LENGTH_SHORT).show()
            triggerSpeech()
        } else {
            Toast.makeText(context, "🎙️ माइक्रोफोन अनुमति अस्वीकृत - सेटिंग्स में अनुमति दें", Toast.LENGTH_LONG).show()
        }
    }

    val handleMicClick = {
        val permissionCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            triggerSpeech()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                shadowElevation = 4.dp,
                color = SaffronPrimary
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("voice_assistant_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "वापस जाएं",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "🎙️ केवल बोलकर उपयोग करें",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7FAFC))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Friendly Instruction Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFEB2B2)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📢 लिखना आवश्यक नहीं है!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC53030)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "नीचे दिए गए बड़े माइक (Mic) बटन को दबाएं और अपनी कोई भी समस्या या सवाल हिंदी/स्थानीय भाषा में बोलें। एआई आपको बोलकर उत्तर देगा।",
                        fontSize = 14.sp,
                        color = Color(0xFF2D3748),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main Pulsing Mic Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                if (isListening) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(SaffronPrimary.copy(alpha = 0.3f))
                    )
                }

                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = if (isListening) listOf(SaffronPrimary, Color(0xFFE53E3E))
                                else listOf(EmeraldGreen, Color(0xFF2F855A))
                            )
                        )
                        .clickable { handleMicClick() }
                        .testTag("voice_assistant_mic_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "बोलें",
                        tint = Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = when {
                    isListening -> "🔴 सुन रहा हूँ... बोलिए!"
                    isThinking -> "⏳ AI जवाब सोच रहा है..."
                    else -> "👉 माइक पर टैप करके बोलें"
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isListening) Color(0xFFE53E3E) else EmeraldGreen
            )

            if (isThinking) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = SaffronPrimary
                )
            }

            // User Transcript
            if (transcript.isNotBlank()) {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF2F7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🗣️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "आपने कहा: \"$transcript\"",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2D3748)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // AI Voice Answer Output Box
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🤖 एआई योजना सहायक की सलाह:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                        IconButton(
                            onClick = { viewModel.speakText(aiResponse) },
                            modifier = Modifier.testTag("voice_assistant_replay_tts")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "फिर से सुनें",
                                tint = EmeraldGreen,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = aiResponse,
                        fontSize = 16.sp,
                        color = Color(0xFF1A202C),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.speakText(aiResponse) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "🔊 दोबारा पूरा बोलकर सुनें", fontSize = 16.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Quick One-Touch Spoken Prompts
            Text(
                text = "या इनमें से किसी एक पर टैप करें:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A5568),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            val exampleVoiceQueries = listOf(
                "🌾 'मुझे किसान सम्मान निधि का पैसा मिलेगा क्या?'",
                "👵 '70 साल से ऊपर बुजुर्गों का मुफ्त आयुष्मान कार्ड कैसे बनेगा?'",
                "👩 'लाडली बहना या महिला योजना की जानकारी दो'",
                "🎓 'छात्राओं को छात्रवृत्ति कैसे मिलती है?'",
                "🖨️ 'आवेदन के लिए CSC पर्ची बना दो'"
            )

            exampleVoiceQueries.forEach { prompt ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            val cleanPrompt = prompt.replace(Regex("[🌾👵👩🎓🖨️'']"), "").trim()
                            viewModel.processVoiceAssistantQuery(cleanPrompt)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = prompt,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2B6CB0)
                        )
                    }
                }
            }
        }
    }
}
