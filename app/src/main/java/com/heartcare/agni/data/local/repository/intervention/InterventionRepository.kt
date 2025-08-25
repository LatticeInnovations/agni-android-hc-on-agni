package com.heartcare.agni.data.local.repository.intervention

import com.heartcare.agni.data.local.model.InterventionResponseLocal
import com.heartcare.agni.data.server.model.intervention.InterventionMasterResponse
import com.heartcare.agni.data.server.model.intervention.InterventionResponse

interface InterventionRepository {
    suspend fun insertInterventionMaster(vararg interventionMasterResponse: InterventionMasterResponse): List<Long>
    suspend fun getInterventionMasterList(): List<InterventionMasterResponse>
    suspend fun getInterventionMasterByFhirId(fhirId: String): InterventionMasterResponse
    suspend fun insertIntervention(vararg interventionResponse: InterventionResponse): List<Long>
    suspend fun getInterventionList(patientId: String): List<InterventionResponseLocal>
}