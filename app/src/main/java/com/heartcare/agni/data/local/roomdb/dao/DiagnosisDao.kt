package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.heartcare.agni.data.local.roomdb.entities.diagnosis.DiagnosisEntity
import com.heartcare.agni.data.local.roomdb.entities.diagnosis.DiagnosisMasterEntity

@Dao
interface DiagnosisDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosisMasterEntity(vararg diagnosisMasterEntity: DiagnosisMasterEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM DiagnosisMasterEntity")
    suspend fun getDiagnosisMasterEntity(): List<DiagnosisMasterEntity>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosis(vararg diagnosisEntity: DiagnosisEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM DiagnosisEntity WHERE appointmentId IN (:appointmentIds) ORDER BY createdOn DESC")
    suspend fun getPastDiagnosisByAppointmentId(vararg appointmentIds: String): List<DiagnosisEntity>

    @Transaction
    @Query("UPDATE DiagnosisEntity SET fhirId = :fhirId WHERE diagnosisUuid = :diagnosisUuid")
    suspend fun updateDiagnosisFhirId(diagnosisUuid: String, fhirId: String)

    @Transaction
    @Query("SELECT * FROM DiagnosisEntity WHERE appointmentId = :appointmentId")
    suspend fun getDiagnosisByAppointmentId(appointmentId: String): List<DiagnosisEntity>

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDiagnosisData(diagnosisEntity: DiagnosisEntity): Int
}