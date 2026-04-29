package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heartcare.agni.data.local.roomdb.entities.allergy.AllergyEntity

@Dao
interface AllergyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllergyRecord(vararg allergyEntity: AllergyEntity): List<Long>

    @Query("SELECT * FROM AllergyEntity WHERE appointmentId IN (:appointmentIds) OR campaignAppointmentId IN (:appointmentIds) ORDER BY appUpdatedDate DESC")
    fun getAllergyRecordsByAppointmentIds(vararg appointmentIds: String): List<AllergyEntity>

    @Query("SELECT * FROM AllergyEntity WHERE patientId = :patientId AND campaignId = :campaignId ORDER BY appUpdatedDate DESC LIMIT 1")
    suspend fun getLatestAllergyForCampaign(patientId: String, campaignId: String): AllergyEntity?

    @Query("UPDATE AllergyEntity SET fhirId = :fhirId WHERE uuid = :id")
    suspend fun updateFhirId(id: String, fhirId: String): Int

    @Query("DELETE FROM AllergyEntity WHERE uuid=:id")
    suspend fun deleteAllergy(id: String): Int
}