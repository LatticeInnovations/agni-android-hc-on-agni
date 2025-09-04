package com.heartcare.agni.data.local.repository.referral

import com.heartcare.agni.data.local.roomdb.dao.ReferralDao
import com.heartcare.agni.data.server.model.referral.ReferralResponse
import com.heartcare.agni.utils.converters.responseconverter.toReferralEntity
import com.heartcare.agni.utils.converters.responseconverter.toReferralResponse
import javax.inject.Inject

class ReferralRepositoryImpl @Inject constructor(
    private val referralDao: ReferralDao
): ReferralRepository {
    override suspend fun insertReferral(vararg referralResponse: ReferralResponse): List<Long> {
        return referralDao.insertReferralRecord(*referralResponse.map { it.toReferralEntity() }.toTypedArray())
    }

    override suspend fun getReferralRecords(patientId: String): List<ReferralResponse> {
        return referralDao.getReferralRecords(patientId).map { it.toReferralResponse() }
    }

    override suspend fun getReferralByAppointmentId(appointmentId: String): ReferralResponse? {
        return referralDao.getReferralByAppointmentId(appointmentId)?.toReferralResponse()
    }
}