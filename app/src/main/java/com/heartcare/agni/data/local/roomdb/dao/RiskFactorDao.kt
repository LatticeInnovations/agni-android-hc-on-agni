package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heartcare.agni.data.local.roomdb.entities.risk.RiskFactorEntity

@Dao
interface RiskFactorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRiskFactorRecord(vararg riskFactorEntity: RiskFactorEntity): List<Long>

    @Query("SELECT * FROM RiskFactorEntity WHERE appointmentId IN (:appointmentIds) OR campaignAppointmentId IN (:appointmentIds) ORDER BY appUpdatedDate DESC")
    fun getRiskFactorRecordsByAppointmentIds(vararg appointmentIds: String): List<RiskFactorEntity>

    @Query("SELECT * FROM RiskFactorEntity WHERE patientId = :patientId AND campaignId = :campaignId ORDER BY appUpdatedDate DESC LIMIT 1")
    fun getLatestRiskFactorForCampaign(patientId: String, campaignId: String): RiskFactorEntity?

    @Query("UPDATE RiskFactorEntity SET fhirId = :fhirId WHERE uuid = :id")
    suspend fun updateFhirId(id: String, fhirId: String): Int

    @Query("DELETE FROM RiskFactorEntity WHERE uuid=:id")
    suspend fun deleteRiskFactor(id: String): Int
}