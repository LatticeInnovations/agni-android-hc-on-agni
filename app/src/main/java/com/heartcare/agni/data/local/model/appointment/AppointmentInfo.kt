package com.heartcare.agni.data.local.model.appointment

import androidx.annotation.Keep

@Keep
data class AppointmentInfo(
    val appointment: AppointmentResponseLocal?,
    val existsInOtherHospital: Boolean,
    val canAddAssessment: Boolean,
    val isAppointmentCompleted: Boolean,
    val ifAllSlotsBooked: Boolean
)