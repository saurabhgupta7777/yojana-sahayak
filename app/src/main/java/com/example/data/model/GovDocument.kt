package com.example.data.model

data class GovDocument(
    val id: String,
    val titleHindi: String,
    val titleEng: String,
    val issuingAuthority: String,
    val officialFormLink: String,
    val requiredDocuments: List<String>,
    val whereToApply: String,
    val sourceUrl: String,
    val lastVerifiedTimestamp: String = "Verified Live: 01 Aug 2026",
    val descriptionHindi: String = ""
)

data class DocValidityResult(
    val documentName: String,
    val docNumberMasked: String,
    val issueDate: String,
    val expiryDate: String,
    val isValid: Boolean,
    val statusText: String, // 🟢 VALID, 🔴 EXPIRED, 🟠 RENEWAL_NEEDED
    val govtPolicyDetails: String,
    val renewalSteps: List<String>,
    val officialRenewalUrl: String
)
