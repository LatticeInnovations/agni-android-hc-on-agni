package com.heartcare.agni.data.local.repository.healthfacility

import com.heartcare.agni.data.local.roomdb.dao.HealthFacilityDao
import com.heartcare.agni.data.server.model.healthfacility.HealthFacilityResponse
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.utils.converters.responseconverter.toHealthFacilityEntity
import com.heartcare.agni.utils.converters.responseconverter.toHealthFacilityResponse
import com.heartcare.agni.utils.converters.responseconverter.toLevelResponse
import javax.inject.Inject

class HealthFacilityRepositoryImpl @Inject constructor(
    private val healthFacilityDao: HealthFacilityDao
) : HealthFacilityRepository {
    override suspend fun insertHealthFacility(vararg healthFacilityResponse: HealthFacilityResponse): List<Long> {
        return healthFacilityDao.insertHealthFacility(*healthFacilityResponse.map { it.toHealthFacilityEntity() }
            .toTypedArray())
    }

    override suspend fun getHealthFacilityList(): List<HealthFacilityResponse> {
        return healthFacilityDao.getHealthFacility().map { it.toHealthFacilityResponse() }
    }

    override suspend fun getHealthFacilityInLevelResponse(): List<LevelResponse> {
        return healthFacilityDao.getHealthFacility().map { it.toLevelResponse() }
    }
}