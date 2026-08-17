package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.core.content.ContextCompat
import com.example.data.model.ChatMessage
import com.example.ui.MainViewModel
import com.example.ui.components.UnverifiedWarningBanner
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.WhatsAppBubbleBot
import com.example.ui.theme.WhatsAppBubbleUser
import com.example.ui.theme.WhatsAppChatBg
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppHeaderGreen

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as lazyRowItems
import androidx.compose.ui.text.TextStyle

@Composable
fun WhatsAppChatScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages by viewModel.chatMessages.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spokenMatches = result.data?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
            if (!spokenMatches.isNullOrEmpty()) {
                val spokenText = spokenMatches[0]
                inputText = spokenText
                Toast.makeText(context, "🎤 पहचाना गया: \"$spokenText\"", Toast.LENGTH_SHORT).show()
                viewModel.sendWhatsAppMessage(spokenText)
            }
        }
    }

    val triggerSpeech = {
        val intent = viewModel.getSpeechRecognitionIntent("व्हाट्सएप पर पूछने के लिए बोलें...")
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
            Toast.makeText(context, "🎙️ माइक्रोफोन अनुमति अस्वीकृत (Permission Denied)", Toast.LENGTH_LONG).show()
        }
    }

    val handleVoiceInput = {
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

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WhatsAppChatBg)
    ) {
        // Top Bar Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(WhatsAppHeaderGreen)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🤖", fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "योजना सहायक AI",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "24x7 सरकारी योजना सहायक • 🟢 Online",
                        color = Color(0xFFB2DFDB),
                        fontSize = 12.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Messages Feed
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                WhatsAppMessageBubble(
                    message = msg,
                    onQuickReplyClick = { viewModel.sendWhatsAppMessage(it) },
                    onReadAloud = { viewModel.speakText(it) },
                    onGenerateCscSlip = { scheme -> viewModel.generateCscSlipForScheme(scheme) }
                )
            }

            if (isAiThinking) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = WhatsAppGreen,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "योजना सहायक AI उत्तर तैयार कर रहा है...",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // Compact Horizontal Scrollable Quick Suggestions Row above input
        val defaultPrompts = listOf("🌾 PM Kisan Status", "🎓 छात्रवृत्ति (Scholarship)", "👵 70+ Ayushman Card", "👩 Ladli Behna", "🖨️ आवेदन पर्ची")
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
        ) {
            lazyRowItems(defaultPrompts) { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, WhatsAppGreen, RoundedCornerShape(14.dp))
                        .clickable { viewModel.sendWhatsAppMessage(prompt) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = prompt, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WhatsAppGreen)
                }
            }
        }

        if (isListening) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFEBEE))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🎙️ सुन रहे हैं... बोलें अपनी योजना का सवाल (Listening... Speak now)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        }

        // Input Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("whatsapp_input_field"),
                    placeholder = { Text("मैसेज टाइप करें या बोलें...", fontSize = 13.sp, color = Color.Gray) },
                    shape = RoundedCornerShape(24.dp),
                    textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedBorderColor = WhatsAppGreen,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (isListening) Color.Red else WhatsAppGreen)
                        .clickable {
                            if (inputText.isNotBlank()) {
                                viewModel.sendWhatsAppMessage(inputText)
                                inputText = ""
                            } else {
                                handleVoiceInput()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (inputText.isBlank()) Icons.Default.Mic else Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WhatsAppMessageBubble(
    message: ChatMessage,
    onQuickReplyClick: (String) -> Unit,
    onReadAloud: (String) -> Unit,
    onGenerateCscSlip: (com.example.data.model.Scheme) -> Unit
) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isUser) WhatsAppBubbleUser else WhatsAppBubbleBot

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = if (isUser) 12.dp else 0.dp,
                bottomEnd = if (isUser) 0.dp else 12.dp
            ),
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth(0.88f)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                if (!isUser) {
                    Text(
                        text = "🤖 ${message.senderName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhatsAppGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = message.messageText,
                    fontSize = 13.5.sp,
                    color = Color(0xFF111111),
                    lineHeight = 19.sp
                )

                if (message.isWarningAlert && message.warningMessageText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    UnverifiedWarningBanner(customMessage = message.warningMessageText)
                }

                // If message matched a scheme card
                if (message.matchedScheme != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE8F5E9))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "✨ Matched Scheme: ${message.matchedScheme.titleHindi}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = EmeraldGreen
                            )
                            Text(
                                text = "Max Benefit: ${message.matchedScheme.maxBenefitAmount}",
                                fontSize = 11.sp,
                                color = SaffronPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = { onGenerateCscSlip(message.matchedScheme) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(text = "🖨️ आवेदन पर्ची बनाएं", fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isUser) {
                        IconButton(
                            onClick = { onReadAloud(message.messageText) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Read Aloud",
                                tint = SaffronPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Text(
                        text = message.timestampFormatted,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Quick Reply buttons under bot messages
        if (!isUser && message.quickReplies.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(0.88f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                message.quickReplies.take(2).forEach { reply ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, WhatsAppGreen, RoundedCornerShape(12.dp))
                            .clickable { onQuickReplyClick(reply) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = reply, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WhatsAppGreen)
                    }
                }
            }
        }
    }
}
