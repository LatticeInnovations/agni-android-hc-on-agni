package com.heartcare.agni.data.local.repository.referral

import com.heartcare.agni.data.server.model.referral.ReferralResponse

interface ReferralRepository {
    suspend fun insertReferral(vararg referralResponse: ReferralResponse): List<Long>
    suspend fun getReferralRecordsByAppointmentIds(vararg appointmentIds: String): List<ReferralResponse>
    suspend fun getReferralByAppointmentId(appointmentId: String): ReferralResponse?
}