package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CitizenCategory
import com.example.data.model.RecipientType
import com.example.data.model.Scheme
import com.example.data.model.VerificationType

@Entity(tableName = "schemes")
data class SchemeEntity(
    @PrimaryKey val id: String,
    val titleHindi: String,
    val titleEng: String,
    val ministryOrOrganization: String,
    val category: String,
    val sector: String = "ALL",
    val verificationType: String,
    val shortDescriptionHindi: String,
    val detailedDescriptionHindi: String,
    val maxBenefitAmount: String,
    val benefitsJson: String,
    val eligibilityCriteriaJson: String,
    val documentsRequiredJson: String,
    val officialUrl: String,
    val targetRecipientsJson: String,
    val state: String = "All India",
    val isPIBRecent: Boolean = false,
    val matchPercentage: Int = 95,
    val isSaved: Boolean = false,
    val applicationWindow: String = "आवेदन हमेशा जारी (Open All Year)",
    val howToApplyStepsJson: String = "",
    val targetQualification: String = "सभी के लिए (All Qualifications)",
    val isApplicationOpen: Boolean = true,
    val isAlertSubscribed: Boolean = false,
    val lastVerifiedDate: String = "03 Aug 2026"
)

fun SchemeEntity.toDomainModel(): Scheme {
    val benefitsList = benefitsJson.split("||").filter { it.isNotBlank() }
    val eligibilityList = eligibilityCriteriaJson.split("||").filter { it.isNotBlank() }
    val documentsList = documentsRequiredJson.split("||").filter { it.isNotBlank() }
    val applyStepsList = howToApplyStepsJson.split("||").filter { it.isNotBlank() }
    val recipientsList = targetRecipientsJson.split("||")
        .filter { it.isNotBlank() }
        .mapNotNull {
            try { RecipientType.valueOf(it) } catch (e: Exception) { null }
        }

    return Scheme(
        id = id,
        titleHindi = titleHindi,
        titleEng = titleEng,
        ministryOrOrganization = ministryOrOrganization,
        category = try { CitizenCategory.valueOf(category) } catch (e: Exception) { CitizenCategory.ALL },
        sector = try { com.example.data.model.SchemeSector.valueOf(sector) } catch (e: Exception) { com.example.data.model.SchemeSector.ALL },
        verificationType = try { VerificationType.valueOf(verificationType) } catch (e: Exception) { VerificationType.OFFICIAL_GOVT },
        shortDescriptionHindi = shortDescriptionHindi,
        detailedDescriptionHindi = detailedDescriptionHindi,
        maxBenefitAmount = maxBenefitAmount,
        benefits = benefitsList,
        eligibilityCriteria = eligibilityList,
        documentsRequired = documentsList,
        officialUrl = officialUrl,
        targetRecipients = if (recipientsList.isEmpty()) listOf(RecipientType.MYSELF) else recipientsList,
        state = state,
        isPIBRecent = isPIBRecent,
        matchPercentage = matchPercentage,
        isSaved = isSaved,
        applicationWindow = applicationWindow,
        howToApplySteps = applyStepsList,
        targetQualification = targetQualification,
        isApplicationOpen = isApplicationOpen,
        isAlertSubscribed = isAlertSubscribed,
        lastVerifiedDate = lastVerifiedDate
    )
}

fun Scheme.toEntity(): SchemeEntity {
    return SchemeEntity(
        id = id,
        titleHindi = titleHindi,
        titleEng = titleEng,
        ministryOrOrganization = ministryOrOrganization,
        category = category.name,
        sector = sector.name,
        verificationType = verificationType.name,
        shortDescriptionHindi = shortDescriptionHindi,
        detailedDescriptionHindi = detailedDescriptionHindi,
        maxBenefitAmount = maxBenefitAmount,
        benefitsJson = benefits.joinToString("||"),
        eligibilityCriteriaJson = eligibilityCriteria.joinToString("||"),
        documentsRequiredJson = documentsRequired.joinToString("||"),
        officialUrl = officialUrl,
        targetRecipientsJson = targetRecipients.map { it.name }.joinToString("||"),
        state = state,
        isPIBRecent = isPIBRecent,
        matchPercentage = matchPercentage,
        isSaved = isSaved,
        applicationWindow = applicationWindow,
        howToApplyStepsJson = howToApplySteps.joinToString("||"),
        targetQualification = targetQualification,
        isApplicationOpen = isApplicationOpen,
        isAlertSubscribed = isAlertSubscribed,
        lastVerifiedDate = lastVerifiedDate
    )
}
