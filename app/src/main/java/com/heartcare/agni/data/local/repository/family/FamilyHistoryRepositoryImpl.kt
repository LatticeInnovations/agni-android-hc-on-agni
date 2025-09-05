package com.heartcare.agni.data.local.repository.family

import com.heartcare.agni.data.local.roomdb.dao.FamilyHistoryDao
import com.heartcare.agni.data.server.model.family.FamilyHistoryResponse
import com.heartcare.agni.utils.converters.responseconverter.toFamilyHistoryEntity
import com.heartcare.agni.utils.converters.responseconverter.toFamilyHistoryResponse
import javax.inject.Inject

class FamilyHistoryRepositoryImpl @Inject constructor(
    private val familyHistoryDao: FamilyHistoryDao
): FamilyHistoryRepository {
    override suspend fun insertFamilyHistory(vararg familyHistoryResponse: FamilyHistoryResponse): List<Long> {
        return familyHistoryDao.insertFamilyHistoryRecord(*familyHistoryResponse.map { it.toFamilyHistoryEntity() }.toTypedArray())
    }

    override suspend fun getFamilyHistoryRecordsByAppointmentIds(vararg appointmentIds: String): List<FamilyHistoryResponse> {
        return familyHistoryDao.getFamilyHistoryRecordsByAppointmentIds(*appointmentIds).map { it.toFamilyHistoryResponse() }
    }
}