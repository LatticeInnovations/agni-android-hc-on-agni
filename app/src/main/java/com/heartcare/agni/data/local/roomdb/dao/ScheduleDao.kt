package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.heartcare.agni.data.local.roomdb.entities.schedule.ScheduleEntity
import java.util.Date

@Dao
interface ScheduleDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleEntity(vararg scheduleEntity: ScheduleEntity): List<Long>

    @Transaction
    @Query("UPDATE ScheduleEntity SET scheduleFhirId=:fhirId WHERE id=:id")
    suspend fun updateScheduleFhirId(id: String, fhirId: String): Int

    @Transaction
    @Query("SELECT * FROM ScheduleEntity WHERE id IN (:scheduleId)")
    suspend fun getScheduleById(vararg scheduleId: String): List<ScheduleEntity>

    @Transaction
    @Query("SELECT startTime FROM ScheduleEntity WHERE scheduleFhirId=:fhirId")
    suspend fun getScheduleStartTimeByFhirId(fhirId: String): Date?

    @Transaction
    @Query("SELECT hospitalCode FROM ScheduleEntity WHERE scheduleFhirId=:fhirId")
    suspend fun getScheduleHospitalCodeByFhirId(fhirId: String): String?

    @Transaction
    @Query("SELECT scheduleFhirId FROM ScheduleEntity WHERE startTime=:startTime AND hospitalCode=:hospitalCode")
    suspend fun getFhirIdByStartTime(startTime: Date, hospitalCode: String): String?

    @Transaction
    @Query("SELECT COALESCE((SELECT bookedSlots FROM ScheduleEntity WHERE startTime=:start AND hospitalCode=:hospitalCode), :defaultValue)")
    suspend fun getBookedSlotsCountByStartTime(start: Long, hospitalCode: String, defaultValue: Int = 0): Int

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateScheduleEntity(scheduleEntity: ScheduleEntity): Int

    @Transaction
    @Query("SELECT * FROM ScheduleEntity WHERE startTime=:startTime AND hospitalCode=:hospitalCode")
    suspend fun getScheduleByStartTime(startTime: Long, hospitalCode: String): ScheduleEntity?
}