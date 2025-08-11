package com.heartcare.agni.data.server.model.risk

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SaltResponse(
    val saltAddCooking: Int?,
    val saltAddMeal: Int?,
    val saltAmount: Int?,
    val saltProcessedFood: Int?
) : Parcelable