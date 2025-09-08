package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.heartcare.agni.data.local.enums.PrescriptionType
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionEntity

@Dao
interface PrescriptionDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescription(vararg prescriptionEntity: PrescriptionEntity): List<Long>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescriptionMedicines(vararg prescriptionDirectionsEntity: PrescriptionDirectionsEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM PrescriptionEntity prescription WHERE appointmentId IN (:appointmentIds) AND prescriptionType=:prescriptionType ORDER BY prescription.prescriptionDate DESC")
    suspend fun getPastPrescriptionsByAppointmentId(
        vararg appointmentIds: String,
        prescriptionType: String = PrescriptionType.FORM.type
    ): List<PrescriptionAndMedicineRelation>

    @Transaction
    @Query("UPDATE PrescriptionEntity SET prescriptionFhirId = :prescriptionFhirId WHERE id = :prescriptionId")
    suspend fun updatePrescriptionFhirId(prescriptionId: String, prescriptionFhirId: String)

    @Transaction
    @Query("SELECT * FROM PrescriptionEntity WHERE appointmentId = :appointmentId")
    suspend fun getPrescriptionByAppointmentId(appointmentId: String): List<PrescriptionAndMedicineRelation>

    @Delete
    suspend fun deletePrescriptionEntity(prescriptionEntity: PrescriptionEntity): Int

    @Transaction
    @Query("UPDATE PrescriptionDirectionsEntity SET medReqFhirId = :medReqFhirId WHERE id = :medReqUuid")
    suspend fun updateMedReqFhirId(medReqUuid: String, medReqFhirId: String)

    @Transaction
    @Query("SELECT * FROM PrescriptionEntity WHERE id IN (:prescriptionId)")
    suspend fun getPrescriptionById(vararg prescriptionId: String): List<PrescriptionEntity>

    @Transaction
    @Query("SELECT * FROM PrescriptionDirectionsEntity WHERE id IN (:medReqUuid)")
    suspend fun getMedReqById(vararg medReqUuid: String): List<PrescriptionDirectionsEntity>

    @Transaction
    @Query("SELECT id FROM PrescriptionEntity WHERE prescriptionFhirId=:fhirId")
    suspend fun getPrescriptionIdByFhirId(fhirId: String): String

    @Transaction
    @Query("SELECT * FROM PrescriptionEntity where prescriptionType=\"form\"")
    suspend fun getAllFormPrescriptions() : List<PrescriptionEntity>

    @Delete
    suspend fun deletePrescriptionDirectionEntity(prescriptionDirectionsEntity: PrescriptionDirectionsEntity): Int
}