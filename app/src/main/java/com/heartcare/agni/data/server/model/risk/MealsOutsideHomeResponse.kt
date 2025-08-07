package com.heartcare.agni.data.server.model.risk

import androidx.annotation.Keep

@Keep
data class MealsOutsideHomeResponse(
    val eatsOut: Boolean,
    val mealsPerWeek: Int?
)