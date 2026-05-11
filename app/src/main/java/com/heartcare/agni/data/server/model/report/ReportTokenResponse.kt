package com.heartcare.agni.data.server.model.report

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ReportTokenResponse(
    val appointmentId: String,
    val token: String,
    val reportType: String
) : Parcelable