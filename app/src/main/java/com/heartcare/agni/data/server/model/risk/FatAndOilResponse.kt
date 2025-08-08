package com.heartcare.agni.data.server.model.risk

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class FatAndOilResponse(
    val oilUsed: Int?,
    val fatFoodFrequency: Int?,
    val otherFatAndOils: String?
) : Parcelable