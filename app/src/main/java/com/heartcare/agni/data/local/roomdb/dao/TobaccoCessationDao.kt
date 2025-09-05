package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heartcare.agni.data.local.roomdb.entities.tobacco.TobaccoCessationEntity

@Dao
interface TobaccoCessationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTobaccoCessationRecord(vararg tobaccoCessationEntity: TobaccoCessationEntity): List<Long>

    @Query("SELECT * FROM TobaccoCessationEntity WHERE appointmentId IN (:appointmentIds) ORDER BY appUpdatedDate DESC")
    fun getTobaccoCessationRecordsByAppointmentIds(vararg appointmentIds: String): List<TobaccoCessationEntity>

    @Query("UPDATE TobaccoCessationEntity SET fhirId = :fhirId WHERE uuid = :id")
    suspend fun updateFhirId(id: String, fhirId: String): Int

    @Query("DELETE FROM TobaccoCessationEntity WHERE uuid=:id")
    suspend fun deleteTobaccoCessation(id: String): Int
}