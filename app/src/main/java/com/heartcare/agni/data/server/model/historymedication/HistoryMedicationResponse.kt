package com.heartcare.agni.data.server.model.historymedication

import androidx.annotation.Keep

@Keep
data class HistoryMedicationResponse(
    val adherence: String?,
    val appUpdatedDate: String,
    val appointmentId: String,
    val fhirId: String?,
    val hasSideEffect: Boolean,
    val medicinePrescribed: List<String>,
    val medicinePrescribedOthers: String?,
    val patientId: String,
    val practitionerId: String?,
    val practitionerName: String?,
    val sideEffects: String?,
    val uuid: String
)