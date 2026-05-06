package com.heartcare.agni.data.server.model.nationalId

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class NationalIdResponse(
    val nationalId: String,
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val dob: Date,
    val gender: String,
    val updatedAt: Long,
    val lastSyncedAt: Date
): Parcelable