package com.heartcare.agni.data.local.repository.risk

import com.heartcare.agni.data.server.model.risk.RiskFactorResponse


interface RiskFactorRepository {
    suspend fun insertRiskFactor(vararg riskFactorResponse: RiskFactorResponse): List<Long>
    suspend fun getRiskFactorRecordsByAppointmentIds(vararg appointmentIds: String): List<RiskFactorResponse>
}