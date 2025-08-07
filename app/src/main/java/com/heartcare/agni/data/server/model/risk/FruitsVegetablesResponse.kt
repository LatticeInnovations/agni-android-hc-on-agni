package com.heartcare.agni.data.server.model.risk

import androidx.annotation.Keep

@Keep
data class FruitsVegetablesResponse(
    val consumptionInWeek: Boolean,
    val fruitServings: Int?,
    val fruitsDays: Int?,
    val vegetableDays: Int?,
    val vegetableServings: Int?
)