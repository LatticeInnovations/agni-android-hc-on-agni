package com.heartcare.agni.data.local.model.patch

import androidx.annotation.Keep
import java.util.Date

@Keep
data class AppointmentPatchRequest(
    val appointmentId: String,
    val patientId: String,
    val appUpdatedDate: Date,
    val generatedOn: Date?,
    val status: ChangeRequest?,
    val slot: ChangeRequest?,
    val scheduleId: ChangeRequest?,
    val createdOn: ChangeRequest?
)