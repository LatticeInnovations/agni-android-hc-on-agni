package com.heartcare.agni.data.server.model.vitals

import androidx.annotation.Keep

@Keep
data class UnitValue(
    val unit: String,
    val value: Double,
    val type: String?
)
