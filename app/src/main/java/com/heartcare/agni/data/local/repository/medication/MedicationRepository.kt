package com.heartcare.agni.data.local.repository.medication

import com.heartcare.agni.data.local.roomdb.entities.medication.MedicationEntity
import com.heartcare.agni.data.local.roomdb.entities.medication.MedicineTimingEntity
import com.heartcare.agni.data.server.model.prescription.medication.MedicationResponse

interface MedicationRepository {

    suspend fun getActiveIngredients(): List<String>
    suspend fun getMedicationByActiveIngredient(activeIngredient: String): List<MedicationResponse>
    suspend fun getAllMedicationDirections(): List<MedicineTimingEntity>
    suspend fun getAllMedication(): List<MedicationEntity>
    suspend fun getOTCMedication(): List<MedicationEntity>
}