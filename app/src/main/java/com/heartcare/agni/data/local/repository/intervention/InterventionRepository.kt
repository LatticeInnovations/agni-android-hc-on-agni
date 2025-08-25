package com.heartcare.agni.data.local.repository.intervention

import com.heartcare.agni.data.server.model.intervention.InterventionMasterResponse

interface InterventionRepository {
    suspend fun insertInterventionMaster(vararg interventionMasterResponse: InterventionMasterResponse): List<Long>
    suspend fun getInterventionMasterList(): List<InterventionMasterResponse>
}