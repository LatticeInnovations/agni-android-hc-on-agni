package com.heartcare.agni.data.local.repository.intervention

import com.heartcare.agni.data.local.roomdb.dao.InterventionDao
import com.heartcare.agni.data.server.model.intervention.InterventionMasterResponse
import com.heartcare.agni.utils.converters.responseconverter.toInterventionMasterEntity
import com.heartcare.agni.utils.converters.responseconverter.toInterventionMasterResponse
import javax.inject.Inject

class InterventionRepositoryImpl @Inject constructor(
    private val interventionDao: InterventionDao
) : InterventionRepository {
    override suspend fun insertInterventionMaster(vararg interventionMasterResponse: InterventionMasterResponse): List<Long> {
        return interventionDao.insertInterventionMaster(*interventionMasterResponse.map { it.toInterventionMasterEntity() }
            .toTypedArray())
    }

    override suspend fun getInterventionMasterList(): List<InterventionMasterResponse> {
        return interventionDao.getInterventionsMasterList()
            .map { it.toInterventionMasterResponse() }
    }
}