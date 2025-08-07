package com.heartcare.agni.data.server.model.risk

import androidx.annotation.Keep

@Keep
data class PhysicalActivityResponse(
    val weeklyEngagement: Boolean,
    val moderateDays: Int?,
    val moderateTime: Int?,
    val vigorousDays: Int?,
    val vigorousTime: Int?
)