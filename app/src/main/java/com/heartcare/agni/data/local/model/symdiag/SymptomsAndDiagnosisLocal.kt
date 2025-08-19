package com.heartcare.agni.data.local.model.symdiag

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.Date

@Keep
data class SymptomsAndDiagnosisData(
    val patientId: String?,
    val appointmentId: String,
    @SerializedName("uuid")
    val symDiagUuid: String,
    @SerializedName("appUpdatedDate")
    val createdOn: Date,
    val diagnosis: List<String>,
    val symptoms: List<String>?
)