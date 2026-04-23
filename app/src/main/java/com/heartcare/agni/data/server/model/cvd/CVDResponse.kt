package com.heartcare.agni.data.server.model.cvd

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.Date

@Keep
data class CVDResponse(
    val cvdFhirId: String?,
    @SerializedName("uuid")
    val cvdUuid: String,
    val appointmentId: String,
    val patientId: String,
    val bmi: Double,
    val bpDiastolic: Int,
    val bpSystolic: Int,
    val cholesterol: Double?,
    val cholesterolUnit: String?,
    val diabetic: Int,
    val heightCm: Double?,
    val heightFt: Int?,
    val heightInch: Double?,
    val practitionerName: String?,
    val risk: Int,
    val smoker: Int,
    val weight: Double,
    val createdOn: Date,
    val appUpdatedDate: Date?,
    val weightUnit: String,
    val chiefComplaint: String?,
    val screeningDate: Date,
    val heartAttackHistory: Int,
    val campaignId: String?
)