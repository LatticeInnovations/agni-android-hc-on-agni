package com.heartcare.agni.data.local.repository.priordx

import com.heartcare.agni.data.server.model.priordx.PriorDxResponse

interface PriorDxRepository {
    suspend fun insertPriorDx(vararg priorDxResponse: PriorDxResponse): List<Long>
    suspend fun getPriorDxRecordsByAppointmentIds(vararg appointmentIds: String): List<PriorDxResponse>
}