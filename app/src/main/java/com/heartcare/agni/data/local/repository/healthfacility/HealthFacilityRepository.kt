package com.heartcare.agni.data.local.repository.healthfacility

import com.heartcare.agni.data.server.model.healthfacility.HealthFacilityResponse
import com.heartcare.agni.data.server.model.levels.LevelResponse

interface HealthFacilityRepository {
    suspend fun insertHealthFacility(vararg healthFacilityResponse: HealthFacilityResponse): List<Long>
    suspend fun getHealthFacilityList(): List<HealthFacilityResponse>
    suspend fun getHealthFacilityInLevelResponse(): List<LevelResponse>
}