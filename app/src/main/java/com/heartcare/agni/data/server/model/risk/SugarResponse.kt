package com.heartcare.agni.data.server.model.risk

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SugarResponse(
    val juiceFrequency: Int?,
    val softDrinkFrequency: Int?
) : Parcelable