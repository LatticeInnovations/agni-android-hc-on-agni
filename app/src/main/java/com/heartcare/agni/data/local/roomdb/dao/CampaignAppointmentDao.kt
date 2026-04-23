package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.heartcare.agni.data.local.roomdb.entities.campaign.CampaignAppointmentEntity

@Dao
interface CampaignAppointmentDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointmentEntity(vararg appointmentEntity: CampaignAppointmentEntity): List<Long>

    @Transaction
    @Query("SELECT id FROM CampaignAppointmentEntity WHERE appointmentFhirId = :appointmentFhirId")
    suspend fun getAppointmentIdByFhirId(appointmentFhirId: String): String

    @Transaction
    @Query("SELECT appointmentFhirId FROM CampaignAppointmentEntity WHERE id = :appointmentId")
    suspend fun getFhirIdByAppointmentId(appointmentId: String): String?

    @Transaction
    @Query("UPDATE CampaignAppointmentEntity SET appointmentFhirId=:fhirId WHERE id=:id")
    suspend fun updateAppointmentFhirId(id: String, fhirId: String): Int

    @Transaction
    @Query("SELECT * FROM CampaignAppointmentEntity WHERE patientId=:patientId AND status=:status ORDER BY startTime")
    suspend fun getAppointmentsOfPatientByStatus(
        patientId: String,
        status: String
    ): List<CampaignAppointmentEntity>

    @Transaction
    @Query("SELECT * FROM CampaignAppointmentEntity WHERE startTime BETWEEN :startOfDay AND :endOfDay AND status<>\"cancelled\" ORDER BY startTime")
    suspend fun getAppointmentsByDate(startOfDay: Long, endOfDay: Long): List<CampaignAppointmentEntity>

    @Transaction
    @Query("SELECT * FROM CampaignAppointmentEntity WHERE patientId=:patientId AND status<>\"cancelled\" AND startTime BETWEEN :startOfDay AND :endOfDay")
    suspend fun getAppointmentOfPatientByDate(
        patientId: String,
        startOfDay: Long,
        endOfDay: Long
    ): List<CampaignAppointmentEntity>

    @Query("SELECT * FROM CampaignAppointmentEntity WHERE patientId=:patientId AND status<>\"cancelled\" ORDER BY startTime DESC")
    suspend fun getAppointmentsOfPatient(
        patientId: String
    ): List<CampaignAppointmentEntity>

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAppointmentEntity(appointmentEntity: CampaignAppointmentEntity): Int

    @Transaction
    @Query("SELECT * FROM CampaignAppointmentEntity WHERE id IN (:appointmentId)")
    suspend fun getAppointmentById(vararg appointmentId: String): List<CampaignAppointmentEntity>

    @Transaction
    @Query("SELECT * FROM CampaignAppointmentEntity WHERE patientId=:patientId AND campaignId=:campaignId AND status<>\"cancelled\" ORDER BY startTime DESC LIMIT 1")
    suspend fun getAppointmentOfPatientByCampaign(
        patientId: String,
        campaignId: String
    ): CampaignAppointmentEntity?
}
