package com.heartcare.agni.data.server.model.vitals

import androidx.annotation.Keep
import java.util.Date

@Keep
data class VitalResponse(
    val uuid: String,
    val fhirId: String?,
    val patientId: String,
    val appointmentId: String,
    val campaignId: String? = null,
    val appUpdatedDate: Date,
    val practitionerName: String?,
    val bloodGlucose: UnitValue?,
    val footExamination: String?,
    val eyeExamination: String?,
    val abdominalCircumference: UnitValue?,
    val hipCircumference: UnitValue?,
    val hbA1cPercentage: Double?,
    val serumCreatinine: UnitValue?,
    val serumPotassium: UnitValue?,
    val urineProtein: String?,
    val urineKetones: String?,
    val others: String?,
    val screeningSiteName: String? = null
)