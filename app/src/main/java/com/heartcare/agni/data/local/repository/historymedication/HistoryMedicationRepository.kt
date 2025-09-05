package com.heartcare.agni.data.local.repository.historymedication

import com.heartcare.agni.data.server.model.historymedication.HistoryMedicationResponse

interface HistoryMedicationRepository {
    suspend fun insertHistoryMedication(vararg historyMedicationResponse: HistoryMedicationResponse): List<Long>
    suspend fun getHistoryMedicationRecordsByAppointmentIds(vararg appointmentIds: String): List<HistoryMedicationResponse>
}