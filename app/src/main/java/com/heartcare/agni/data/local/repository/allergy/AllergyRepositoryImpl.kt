package com.heartcare.agni.data.local.repository.allergy

import com.heartcare.agni.data.local.roomdb.dao.AllergyDao
import com.heartcare.agni.data.server.model.allergy.AllergyResponse
import com.heartcare.agni.utils.converters.responseconverter.toAllergyEntity
import com.heartcare.agni.utils.converters.responseconverter.toAllergyResponse
import javax.inject.Inject

class AllergyRepositoryImpl@Inject constructor(
    private val allergyDao: AllergyDao
): AllergyRepository {
    override suspend fun insertAllergy(vararg allergyResponse: AllergyResponse): List<Long> {
        return allergyDao.insertAllergyRecord(*allergyResponse.map { it.toAllergyEntity() }.toTypedArray())
    }

    override suspend fun getAllergyRecordsByAppointmentIds(vararg appointmentIds: String): List<AllergyResponse> {
        return allergyDao.getAllergyRecordsByAppointmentIds(*appointmentIds).map { it.toAllergyResponse() }
    }
}