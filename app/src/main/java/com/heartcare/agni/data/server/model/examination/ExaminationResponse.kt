package com.heartcare.agni.data.server.model.examination

import androidx.annotation.Keep
import java.util.Date

@Keep
data class ExaminationResponse(
    val uuid: String?,
    val fhirId: String?,
    val appUpdatedDate: Date,
    val appointmentId: String,
    val patientId: String,
    val practitionerId: String?,
    val practitionerName: String?,
    val examinations: List<String>,
    val campaignId: String? = null
)