package com.heartcare.agni.data.server.model.risk

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class AlcoholResponse(
    val consumedWithin30Days: Boolean,
    val alcoholQ1: Int?,
    val alcoholQ2: Int?,
    val alcoholQ3: Int?
) : Parcelable