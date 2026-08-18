package com.example.data.model

data class CitizenProfile(
    val recipient: RecipientType = RecipientType.MYSELF,
    val category: CitizenCategory = CitizenCategory.ALL,
    val age: Int = 30,
    val annualIncomeRupees: Long = 180000,
    val state: String = "All India",
    val occupation: String = "Farmer / Agriculture",
    val isBplCardHolder: Boolean = true,
    val gender: String = "Female",
    val qualification: String = "12th Pass",
    val socialCategory: String = "OBC",
    val disabilityPercentage: Int = 0
)

