package com.heartcare.agni.data.server.model.risk

import androidx.annotation.Keep

@Keep
data class FatAndOilResponse(
    val oilUsed: Int?,
    val fatFoodFrequency: Int?,
    val otherFatAndOils: String?
)