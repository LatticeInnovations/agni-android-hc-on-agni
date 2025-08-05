package com.heartcare.agni.data.server.model.allergy

import androidx.annotation.Keep

@Keep
data class AllergyResponse(
    val uuid: String,
    val fhirId: String?,
    val allergy: String?,
    val appUpdatedDate: String,
    val appointmentId: String,
    val patientId: String,
    val practitionerId: String?,
    val practitionerName: String?
)