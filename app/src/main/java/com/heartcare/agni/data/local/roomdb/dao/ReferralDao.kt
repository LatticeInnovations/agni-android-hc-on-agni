package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heartcare.agni.data.local.roomdb.entities.referral.ReferralEntity

@Dao
interface ReferralDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReferralRecord(vararg referralEntity: ReferralEntity): List<Long>

    @Query("SELECT * FROM ReferralEntity WHERE patientId=:patientId ORDER BY appUpdatedDate DESC")
    fun getReferralRecords(patientId: String): List<ReferralEntity>

    @Query("UPDATE ReferralEntity SET fhirId = :fhirId WHERE uuid = :id")
    suspend fun updateFhirId(id: String, fhirId: String): Int

    @Query("DELETE FROM ReferralEntity WHERE uuid=:id")
    suspend fun deleteReferral(id: String): Int

    @Query("SELECT * FROM ReferralEntity WHERE appointmentId=:appointmentId")
    suspend fun getReferralByAppointmentId(appointmentId: String): ReferralEntity?
}