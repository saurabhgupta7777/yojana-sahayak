package com.example.data.model

data class CscSlip(
    val slipId: String,
    val applicantName: String,
    val recipientType: RecipientType,
    val selectedCategory: CitizenCategory,
    val matchedSchemesCount: Int,
    val matchedSchemeTitles: List<String>,
    val documentChecklist: List<String>,
    val generatedDateFormatted: String,
    val verificationQrCodeString: String,
    val state: String,
    val nearestCscNote: String = "अपने नजदीकी जन सेवा केंद्र (CSC) में यह पर्ची और आधार कार्ड लेकर जाएं।"
)
