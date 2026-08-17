package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CitizenCategory
import com.example.data.model.CscSlip
import com.example.data.model.RecipientType

@Entity(tableName = "csc_slips")
data class CscSlipEntity(
    @PrimaryKey val slipId: String,
    val applicantName: String,
    val recipientType: String,
    val selectedCategory: String,
    val matchedSchemesCount: Int,
    val matchedSchemeTitlesJson: String,
    val documentChecklistJson: String,
    val generatedDateFormatted: String,
    val verificationQrCodeString: String,
    val state: String,
    val nearestCscNote: String
)

fun CscSlipEntity.toDomainModel(): CscSlip {
    return CscSlip(
        slipId = slipId,
        applicantName = applicantName,
        recipientType = try { RecipientType.valueOf(recipientType) } catch (e: Exception) { RecipientType.MYSELF },
        selectedCategory = try { CitizenCategory.valueOf(selectedCategory) } catch (e: Exception) { CitizenCategory.ALL },
        matchedSchemesCount = matchedSchemesCount,
        matchedSchemeTitles = matchedSchemeTitlesJson.split("||").filter { it.isNotBlank() },
        documentChecklist = documentChecklistJson.split("||").filter { it.isNotBlank() },
        generatedDateFormatted = generatedDateFormatted,
        verificationQrCodeString = verificationQrCodeString,
        state = state,
        nearestCscNote = nearestCscNote
    )
}

fun CscSlip.toEntity(): CscSlipEntity {
    return CscSlipEntity(
        slipId = slipId,
        applicantName = applicantName,
        recipientType = recipientType.name,
        selectedCategory = selectedCategory.name,
        matchedSchemesCount = matchedSchemesCount,
        matchedSchemeTitlesJson = matchedSchemeTitles.joinToString("||"),
        documentChecklistJson = documentChecklist.joinToString("||"),
        generatedDateFormatted = generatedDateFormatted,
        verificationQrCodeString = verificationQrCodeString,
        state = state,
        nearestCscNote = nearestCscNote
    )
}
