package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heartcare.agni.data.local.roomdb.entities.historymedication.HistoryMedicationEntity

@Dao
interface HistoryMedicationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistoryMedicationRecord(vararg historyMedicationEntity: HistoryMedicationEntity): List<Long>

    @Query("SELECT * FROM HistoryMedicationEntity WHERE patientId=:patientId ORDER BY appUpdatedDate DESC")
    fun getHistoryMedicationRecords(patientId: String): List<HistoryMedicationEntity>

    @Query("UPDATE HistoryMedicationEntity SET fhirId = :fhirId WHERE uuid = :id")
    suspend fun updateFhirId(id: String, fhirId: String): Int

    @Query("DELETE FROM HistoryMedicationEntity WHERE uuid=:id")
    suspend fun deleteHistoryMedication(id: String): Int
}