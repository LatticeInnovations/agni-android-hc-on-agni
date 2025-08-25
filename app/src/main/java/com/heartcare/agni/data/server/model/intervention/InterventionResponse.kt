package com.heartcare.agni.data.server.model.intervention

import androidx.annotation.Keep
import java.util.Date

@Keep
data class InterventionResponse(
    val uuid: String?,
    val fhirId: String?,
    val appUpdatedDate: Date,
    val appointmentId: String,
    val patientId: String,
    val practitionerId: String?,
    val practitionerName: String?,
    val interventions: List<String>
)