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

    @Query("SELECT * FROM TobaccoCessationEntity WHERE appointmentId IN (:appointmentIds) OR campaignAppointmentId IN (:appointmentIds) ORDER BY appUpdatedDate DESC")
    fun getTobaccoCessationRecordsByAppointmentIds(vararg appointmentIds: String): List<TobaccoCessationEntity>

    @Query("SELECT * FROM TobaccoCessationEntity WHERE patientId = :patientId AND campaignId = :campaignId ORDER BY appUpdatedDate DESC LIMIT 1")
    fun getLatestTobaccoCessationForCampaign(patientId: String, campaignId: String): TobaccoCessationEntity?

    @Query("UPDATE TobaccoCessationEntity SET fhirId = :fhirId WHERE uuid = :id")
    suspend fun updateFhirId(id: String, fhirId: String): Int

    @Query("DELETE FROM TobaccoCessationEntity WHERE uuid=:id")
    suspend fun deleteTobaccoCessation(id: String): Int
}