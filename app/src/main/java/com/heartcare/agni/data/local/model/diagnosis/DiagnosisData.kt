package com.heartcare.agni.data.local.model.diagnosis

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.Date

@Keep
data class DiagnosisData(
    val patientId: String?,
    val appointmentId: String,
    @SerializedName("uuid")
    val diagnosisUuid: String,
    @SerializedName("appUpdatedDate")
    val createdOn: Date,
    val diagnosis: List<String>,
    val symptoms: List<String>?,
    val campaignId: String?
)