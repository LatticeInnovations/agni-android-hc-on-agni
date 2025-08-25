package com.heartcare.agni.data.local.repository.intervention

import com.heartcare.agni.data.local.model.InterventionResponseLocal
import com.heartcare.agni.data.local.roomdb.dao.InterventionDao
import com.heartcare.agni.data.server.model.intervention.InterventionMasterResponse
import com.heartcare.agni.data.server.model.intervention.InterventionResponse
import com.heartcare.agni.utils.converters.responseconverter.toInterventionEntity
import com.heartcare.agni.utils.converters.responseconverter.toInterventionMasterEntity
import com.heartcare.agni.utils.converters.responseconverter.toInterventionMasterResponse
import com.heartcare.agni.utils.converters.responseconverter.toInterventionResponseLocal
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

    override suspend fun getInterventionMasterByFhirId(fhirId: String): InterventionMasterResponse {
        return interventionDao.getInterventionByFhirId(fhirId).toInterventionMasterResponse()
    }

    override suspend fun insertIntervention(vararg interventionResponse: InterventionResponse): List<Long> {
        return interventionDao.insertIntervention(*interventionResponse.map { it.toInterventionEntity() }.toTypedArray())
    }

    override suspend fun getInterventionList(patientId: String): List<InterventionResponseLocal> {
        return interventionDao.getInterventions(patientId).map { it.toInterventionResponseLocal(interventionDao) }
    }
}