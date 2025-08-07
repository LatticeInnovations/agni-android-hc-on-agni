package com.heartcare.agni.data.server.model.risk

import androidx.annotation.Keep

@Keep
data class SaltResponse(
    val saltAddCooking: Int?,
    val saltAddMeal: Int?,
    val saltAmount: Int?,
    val saltProcessedFood: Int?
)