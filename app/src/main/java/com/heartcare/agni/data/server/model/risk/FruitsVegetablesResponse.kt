package com.heartcare.agni.data.server.model.risk

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class FruitsVegetablesResponse(
    val consumptionInWeek: Boolean,
    val fruitServings: Int?,
    val fruitsDays: Int?,
    val vegetableDays: Int?,
    val vegetableServings: Int?
) : Parcelable