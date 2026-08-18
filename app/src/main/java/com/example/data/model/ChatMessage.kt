package com.example.data.model

data class ChatMessage(
    val id: String,
    val senderName: String,
    val messageText: String,
    val isUser: Boolean,
    val timestampFormatted: String,
    val isVoiceNote: Boolean = false,
    val matchedScheme: Scheme? = null,
    val quickReplies: List<String> = emptyList(),
    val isWarningAlert: Boolean = false,
    val warningMessageText: String? = null
)
