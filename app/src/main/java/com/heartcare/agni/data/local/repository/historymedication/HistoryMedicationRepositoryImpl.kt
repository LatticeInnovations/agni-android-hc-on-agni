package com.heartcare.agni.data.local.repository.historymedication

import com.heartcare.agni.data.local.roomdb.dao.HistoryMedicationDao
import com.heartcare.agni.data.server.model.historymedication.HistoryMedicationResponse
import com.heartcare.agni.utils.converters.responseconverter.toHistoryMedicationEntity
import com.heartcare.agni.utils.converters.responseconverter.toHistoryMedicationResponse
import javax.inject.Inject

class HistoryMedicationRepositoryImpl @Inject constructor(
    private val historyMedicationDao: HistoryMedicationDao
): HistoryMedicationRepository {
    override suspend fun insertHistoryMedication(vararg historyMedicationResponse: HistoryMedicationResponse): List<Long> {
        return historyMedicationDao.insertHistoryMedicationRecord(*historyMedicationResponse.map { it.toHistoryMedicationEntity() }.toTypedArray())
    }

    override suspend fun getHistoryMedicationRecords(patientId: String): List<HistoryMedicationResponse> {
        return historyMedicationDao.getHistoryMedicationRecords(patientId).map { it.toHistoryMedicationResponse() }
    }
}