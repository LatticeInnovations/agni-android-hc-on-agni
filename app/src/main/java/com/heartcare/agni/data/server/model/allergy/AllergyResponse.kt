package com.heartcare.agni.data.server.model.allergy

import androidx.annotation.Keep
import java.util.Date

@Keep
data class AllergyResponse(
    val uuid: String,
    val fhirId: String?,
    val allergy: String?,
    val appUpdatedDate: Date,
    val appointmentId: String,
    val patientId: String,
    val practitionerId: String?,
    val practitionerName: String?
)