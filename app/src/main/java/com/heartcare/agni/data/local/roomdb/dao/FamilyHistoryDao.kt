package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heartcare.agni.data.local.roomdb.entities.family.FamilyHistoryEntity

@Dao
interface FamilyHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFamilyHistoryRecord(vararg familyHistoryEntity: FamilyHistoryEntity): List<Long>

    @Query("SELECT * FROM FamilyHistoryEntity WHERE appointmentId IN (:appointmentIds) OR campaignAppointmentId IN (:appointmentIds) ORDER BY appUpdatedDate DESC")
    fun getFamilyHistoryRecordsByAppointmentIds(vararg appointmentIds: String): List<FamilyHistoryEntity>

    @Query("SELECT * FROM FamilyHistoryEntity WHERE patientId = :patientId AND campaignId = :campaignId ORDER BY appUpdatedDate DESC LIMIT 1")
    suspend fun getLatestFamilyHistoryForCampaign(patientId: String, campaignId: String): FamilyHistoryEntity?

    @Query("UPDATE FamilyHistoryEntity SET fhirId = :fhirId WHERE uuid = :id")
    suspend fun updateFhirId(id: String, fhirId: String): Int

    @Query("DELETE FROM FamilyHistoryEntity WHERE uuid=:id")
    suspend fun deleteFamilyHistory(id: String): Int
}