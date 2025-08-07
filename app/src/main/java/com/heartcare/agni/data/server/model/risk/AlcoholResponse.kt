package com.heartcare.agni.data.server.model.risk

import androidx.annotation.Keep

@Keep
data class AlcoholResponse(
    val consumedWithin30Days: Boolean,
    val alcoholQ1: Int?,
    val alcoholQ2: Int?,
    val alcoholQ3: Int?
)