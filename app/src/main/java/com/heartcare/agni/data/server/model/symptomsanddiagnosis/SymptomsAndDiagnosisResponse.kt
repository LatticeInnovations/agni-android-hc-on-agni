package com.heartcare.agni.data.server.model.symptomsanddiagnosis

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.Date

@Keep
data class SymptomsAndDiagnosisResponse(
    val appointmentId: String,
    val patientId: String,
    val createdOn: Date,
    val diagnosis: List<SymptomsAndDiagnosisItem>,
    val practitionerName: String,
    @SerializedName("fhirId")
    val symDiagFhirId: String,
    @SerializedName("uuid")
    val symDiagUuid: String,
    val symptoms: List<SymptomsAndDiagnosisItem>,
    val progressNote: String?
)
