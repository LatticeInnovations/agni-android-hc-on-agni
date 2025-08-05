package com.heartcare.agni.data.local.repository.allergy

import com.heartcare.agni.data.server.model.allergy.AllergyResponse

interface AllergyRepository {
    suspend fun insertAllergy(vararg allergyResponse: AllergyResponse): List<Long>
    suspend fun getAllergyRecords(patientId: String): List<AllergyResponse>
}