package com.heartcare.agni.data.server.model.diagnosis

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.Date

@Keep
data class DiagnosisResponse(
    val appointmentId: String,
    val patientId: String,
    val createdOn: Date,
    val diagnosis: List<DiagnosisItem>,
    val practitionerName: String,
    @SerializedName("fhirId")
    val diagnosisFhirId: String,
    @SerializedName("uuid")
    val diagnosisUuid: String,
    val symptoms: List<DiagnosisItem>,
    val progressNote: String?,
    val campaignId: String?,
    val screeningSiteName: String? = null
)
