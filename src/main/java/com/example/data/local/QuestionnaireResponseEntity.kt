package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questionnaire_responses")
data class QuestionnaireResponseEntity(
    @PrimaryKey val id: Int = 1,
    val currentStep: Int = 0,
    val applicantName: String = "",
    val age: Int = 35,
    val income: Long = 120000L,
    val occupation: String = "",
    val state: String = "All India",
    val isBpl: Boolean = false,
    val selectedRecipientName: String = "MYSELF",
    val selectedCategoryName: String = "ALL",
    val lastAssessmentResult: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
