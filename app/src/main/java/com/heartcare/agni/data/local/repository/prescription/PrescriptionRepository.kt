package com.heartcare.agni.data.local.repository.prescription

import com.heartcare.agni.data.local.model.prescription.PrescriptionResponseLocal
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity

interface PrescriptionRepository {

    suspend fun insertPrescription(prescriptionResponseLocal: PrescriptionResponseLocal): Long
    suspend fun getLastPrescriptionAndMedicineByAppointmentId(vararg appointmentIds: String): List<PrescriptionAndMedicineRelation>
    suspend fun getPrescriptionByAppointmentId(appointmentId: String): List<PrescriptionResponseLocal>
    suspend fun deletePrescriptionDirectionEntity(prescriptionDirectionsEntity: PrescriptionDirectionsEntity): Int
}