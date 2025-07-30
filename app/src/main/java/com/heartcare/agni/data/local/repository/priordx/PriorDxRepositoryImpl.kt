package com.heartcare.agni.data.local.repository.priordx

import com.heartcare.agni.data.local.roomdb.dao.PriorDxDao
import com.heartcare.agni.data.server.model.priordx.PriorDxResponse
import com.heartcare.agni.utils.converters.responseconverter.toPriorDxEntity
import com.heartcare.agni.utils.converters.responseconverter.toPriorDxResponse
import javax.inject.Inject

class PriorDxRepositoryImpl @Inject constructor(
    private val priorDxDao: PriorDxDao
): PriorDxRepository {
    override suspend fun insertPriorDx(vararg priorDxResponse: PriorDxResponse): List<Long> {
        return priorDxDao.insertPriorDxRecord(*priorDxResponse.map { it.toPriorDxEntity() }.toTypedArray())
    }

    override suspend fun getPriorDxRecords(patientId: String): List<PriorDxResponse> {
        return priorDxDao.getPriorDxRecords(patientId).map { it.toPriorDxResponse() }
    }
}