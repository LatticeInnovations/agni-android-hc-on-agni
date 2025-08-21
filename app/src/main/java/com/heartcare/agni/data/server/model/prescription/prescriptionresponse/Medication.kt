package com.heartcare.agni.data.server.model.prescription.prescriptionresponse

import androidx.annotation.Keep

@Keep
data class Medication(
    val medReqUuid: String,
    val medReqFhirId: String?,
    val doseForm: String,
    val duration: Int,
    val frequency: Int,
    val medFhirId: String,
    val note: String?,
    val qtyPerDose: Double,
    val qtyPrescribed: Double,
    val timing: String?,
    val brandName: String?,
    val doseFormCode: String?
)