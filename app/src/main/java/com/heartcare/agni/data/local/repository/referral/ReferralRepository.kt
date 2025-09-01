package com.heartcare.agni.data.local.repository.referral

import com.heartcare.agni.data.server.model.referral.ReferralResponse

interface ReferralRepository {
    suspend fun insertReferral(vararg referralResponse: ReferralResponse): List<Long>
    suspend fun getReferralRecords(patientId: String): List<ReferralResponse>
}