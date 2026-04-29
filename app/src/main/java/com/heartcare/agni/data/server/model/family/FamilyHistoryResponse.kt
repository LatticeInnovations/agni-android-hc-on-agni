package com.heartcare.agni.data.server.model.family

import androidx.annotation.Keep
import java.util.Date

@Keep
data class FamilyHistoryResponse(
    val uuid: String,
    val fhirId: String?,
    val patientId: String,
    val appointmentId: String,
    val appUpdatedDate: Date,
    val familyDiseases: List<String>,
    val occurrenceAgeData: String?,
    val practitionerId: String?,
    val practitionerName: String?,
    val campaignId: String? = null,
    val screeningSiteName: String? = null
)