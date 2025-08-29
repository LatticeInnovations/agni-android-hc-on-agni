package com.heartcare.agni.data.server.model.diagnosis

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class DiagnosisItem(
    val code: String,
    val display: String
) : Parcelable