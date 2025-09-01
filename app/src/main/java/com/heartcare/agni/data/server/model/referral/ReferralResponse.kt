package com.heartcare.agni.data.server.model.referral

import androidx.annotation.Keep
import java.util.Date

@Keep
data class ReferralResponse(
    val uuid: String,
    val fhirId: String?,
    val appUpdatedDate: Date,
    val appointmentId: String,
    val healthFacilityId: String,
    val note: String,
    val patientId: String,
    val practitionerId: String?,
    val practitionerName: String?
)