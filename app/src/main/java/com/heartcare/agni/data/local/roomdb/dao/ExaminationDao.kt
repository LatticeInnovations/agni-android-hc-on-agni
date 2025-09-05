package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.heartcare.agni.data.local.roomdb.entities.examination.ExaminationEntity
import com.heartcare.agni.data.local.roomdb.entities.examination.ExaminationMasterEntity

@Dao
interface ExaminationDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExaminationMaster(vararg examinationMasterEntity: ExaminationMasterEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM ExaminationMasterEntity WHERE status=\"active\"")
    suspend fun getExaminationMaster(): List<ExaminationMasterEntity>

    @Transaction
    @Query("SELECT * FROM ExaminationMasterEntity WHERE fhirId=:fhirId")
    suspend fun getExaminationByFhirId(fhirId: String): ExaminationMasterEntity

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamination(vararg examinationEntity: ExaminationEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM ExaminationEntity WHERE appointmentId IN (:appointmentIds) ORDER BY appUpdatedDate DESC")
    suspend fun getExaminationsByAppointmentId(vararg appointmentIds: String): List<ExaminationEntity>

    @Query("UPDATE ExaminationEntity SET fhirId = :fhirId WHERE uuid = :id")
    suspend fun updateFhirId(id: String, fhirId: String): Int

    @Query("DELETE FROM ExaminationEntity WHERE uuid=:id")
    suspend fun deleteExamination(id: String): Int
}