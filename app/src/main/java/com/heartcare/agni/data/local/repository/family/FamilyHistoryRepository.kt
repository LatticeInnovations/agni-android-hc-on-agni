package com.heartcare.agni.data.local.repository.family

import com.heartcare.agni.data.server.model.family.FamilyHistoryResponse

interface FamilyHistoryRepository {
    suspend fun insertFamilyHistory(vararg familyHistoryResponse: FamilyHistoryResponse): List<Long>
    suspend fun getFamilyHistoryRecords(patientId: String): List<FamilyHistoryResponse>
}