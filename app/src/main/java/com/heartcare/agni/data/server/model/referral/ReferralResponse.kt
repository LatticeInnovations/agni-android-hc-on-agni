package com.heartcare.agni.data.server.model.referral

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class ReferralResponse(
    val uuid: String,
    val fhirId: String?,
    val appUpdatedDate: Date,
    val appointmentId: String,
    val healthFacilityId: String,
    val note: String?,
    val patientId: String,
    val practitionerId: String?,
    val practitionerName: String?,
    val sourceHealthFacilityId: String?,
    val sourceIslandId: String?
): Parcelable