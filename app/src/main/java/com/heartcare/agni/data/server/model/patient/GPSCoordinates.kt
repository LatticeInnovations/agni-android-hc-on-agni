package com.heartcare.agni.data.server.model.patient

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class GPSCoordinates (
    val latitude: Double?,
    val longitude: Double?
) : Parcelable