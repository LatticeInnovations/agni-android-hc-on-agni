package com.heartcare.agni.data.local.repository.risk

import com.heartcare.agni.data.local.roomdb.dao.RiskFactorDao
import com.heartcare.agni.data.server.model.risk.RiskFactorResponse
import com.heartcare.agni.utils.converters.responseconverter.toRiskFactorEntity
import com.heartcare.agni.utils.converters.responseconverter.toRiskFactorResponse
import javax.inject.Inject

class RiskFactorRepositoryImpl @Inject constructor(
    private val riskFactorDao: RiskFactorDao
) : RiskFactorRepository {
    override suspend fun insertRiskFactor(vararg riskFactorResponse: RiskFactorResponse): List<Long> {
        return riskFactorDao.insertRiskFactorRecord(*riskFactorResponse.map { it.toRiskFactorEntity() }
            .toTypedArray())
    }

    override suspend fun getRiskFactorRecordsByAppointmentIds(vararg appointmentIds: String): List<RiskFactorResponse> {
        return riskFactorDao.getRiskFactorRecordsByAppointmentIds(*appointmentIds).map { it.toRiskFactorResponse() }
    }

    override fun getLatestRiskFactorForCampaign(
        patientId: String,
        campaignId: String
    ): RiskFactorResponse? {
        return riskFactorDao.getLatestRiskFactorForCampaign(patientId, campaignId)?.toRiskFactorResponse()
    }
}