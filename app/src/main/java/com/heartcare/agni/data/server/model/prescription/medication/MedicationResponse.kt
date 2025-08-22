package com.heartcare.agni.data.server.model.prescription.medication

import androidx.annotation.Keep

@Keep
data class MedicationResponse(
    val medFhirId: String,
    val code: String,
    val name: String,
    val isOTC: Boolean,
    val doseForm: String,
    val doseFormCode: String,
    val status: String,
    val activeIngredient: String,
    val activeIngredientCode: String,
    val medUnit: String,
    val medNumeratorVal: Double,
    val classId: String,
    val className: String,
    val categoryId: String,
    val categoryName: String,
    val brandList: List<String>
)