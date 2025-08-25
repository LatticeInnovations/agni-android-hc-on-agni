package com.heartcare.agni.data.local.model

import androidx.annotation.Keep
import java.util.Date

@Keep
data class InterventionResponseLocal(
    val uuid: String,
    val fhirId: String?,
    val appUpdatedDate: Date,
    val appointmentId: String,
    val patientId: String,
    val practitionerId: String,
    val practitionerName: String,
    val interventions: List<InterventionItem>
)

@Keep
data class InterventionItem(
    val fhirId: String,
    val code: String,
    val display: String
)