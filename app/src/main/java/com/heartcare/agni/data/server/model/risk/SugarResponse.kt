package com.heartcare.agni.data.server.model.risk

import androidx.annotation.Keep

@Keep
data class SugarResponse(
    val juiceFrequency: Int?,
    val softDrinkFrequency: Int?
)