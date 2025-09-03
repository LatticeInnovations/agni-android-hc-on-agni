package com.heartcare.agni.data.local.model.config

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RiskConfig(
    @SerializedName("lt_10")
    val lt10: RiskItem,

    @SerializedName("10_19")
    val range10to19: RiskItem,

    @SerializedName("20_29")
    val range20to29: RiskItem,

    @SerializedName("gte_30")
    val gte30: RiskItem
)

@Keep
data class RiskItem(
    val action: String,
    val medicationGuidance: String,
    val appointmentDays: Int,
    val requiresReferral: Boolean
)

