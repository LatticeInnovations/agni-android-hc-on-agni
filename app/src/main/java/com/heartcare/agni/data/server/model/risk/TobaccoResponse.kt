package com.heartcare.agni.data.server.model.risk

import androidx.annotation.Keep

@Keep
data class TobaccoResponse(
    val tobaccoUser: Boolean,
    val tobaccoItemType: Int?,
    val tobaccoOther: String?,
    val consumptionAmount: Int?,
    val consumptionUnit: String?,
    val startAge: Int?,
    val willingToQuit: Boolean?
)