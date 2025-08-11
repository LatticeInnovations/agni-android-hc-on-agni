package com.heartcare.agni.data.server.model.risk

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class TobaccoResponse(
    val tobaccoUser: Boolean,
    val tobaccoItemType: Int?,
    val tobaccoOther: String?,
    val consumptionAmount: Int?,
    val consumptionUnit: String?,
    val startAge: Int?,
    val willingToQuit: Boolean?
): Parcelable