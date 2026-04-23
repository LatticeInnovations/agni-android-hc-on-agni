package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.heartcare.agni.data.local.roomdb.entities.campaign.CampaignScheduleEntity
import java.util.Date

@Dao
interface CampaignScheduleDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleEntity(vararg scheduleEntity: CampaignScheduleEntity): List<Long>

    @Transaction
    @Query("UPDATE CampaignScheduleEntity SET scheduleFhirId=:fhirId WHERE id=:id")
    suspend fun updateScheduleFhirId(id: String, fhirId: String): Int

    @Transaction
    @Query("SELECT * FROM CampaignScheduleEntity WHERE id IN (:scheduleId)")
    suspend fun getScheduleById(vararg scheduleId: String): List<CampaignScheduleEntity>

    @Transaction
    @Query("SELECT startTime FROM CampaignScheduleEntity WHERE scheduleFhirId=:fhirId")
    suspend fun getScheduleStartTimeByFhirId(fhirId: String): Date?

    @Transaction
    @Query("SELECT scheduleFhirId FROM CampaignScheduleEntity WHERE startTime=:startTime AND campaignId=:campaignId")
    suspend fun getFhirIdByStartTime(startTime: Date, campaignId: String): String?

    @Transaction
    @Query("SELECT COALESCE((SELECT bookedSlots FROM CampaignScheduleEntity WHERE startTime=:start AND campaignId=:campaignId), :defaultValue)")
    suspend fun getBookedSlotsCountByStartTime(start: Long, campaignId: String, defaultValue: Int = 0): Int

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateScheduleEntity(scheduleEntity: CampaignScheduleEntity): Int

    @Transaction
    @Query("SELECT * FROM CampaignScheduleEntity WHERE startTime=:startTime AND campaignId=:campaignId")
    suspend fun getScheduleByStartTime(startTime: Long, campaignId: String): CampaignScheduleEntity?

    @Transaction
    @Query("SELECT * FROM CampaignScheduleEntity WHERE campaignId=:campaignId ORDER BY startTime DESC LIMIT 1")
    suspend fun getScheduleByCampaign(campaignId: String): CampaignScheduleEntity?
}
