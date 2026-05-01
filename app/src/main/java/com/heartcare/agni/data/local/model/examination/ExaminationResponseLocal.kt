package com.heartcare.agni.data.local.model.examination

import androidx.annotation.Keep
import java.util.Date

@Keep
data class ExaminationResponseLocal (
    val uuid: String,
    val fhirId: String?,
    val appUpdatedDate: Date,
    val appointmentId: String,
    val patientId: String,
    val practitionerId: String,
    val practitionerName: String,
    val examinations: List<ExaminationItem>,
    val campaignId: String? = null,
    val screeningSiteName: String? = null
)

@Keep
data class ExaminationItem(
    val fhirId: String,
    val code: String,
    val display: String
)