package com.heartcare.agni.data.local.repository.schedule

import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.data.server.model.scheduleandappointment.schedule.ScheduleResponse

interface ScheduleRepository {
    suspend fun getBookedSlotsCount(startTime: Long, hospitalCode: String): Int
    suspend fun insertSchedule(scheduleResponse: ScheduleResponse, recordType: RecordType? = RecordType.FACILITY): List<Long>
    suspend fun updateSchedule(scheduleResponse: ScheduleResponse, recordType: RecordType? = RecordType.FACILITY): Int
    suspend fun getScheduleById(id: String): ScheduleResponse
    suspend fun getScheduleByStartTime(startTime: Long, hospitalCode: String): ScheduleResponse?
    
    // Campaign Specific Methods
    suspend fun getCampaignScheduleByStartTime(startTime: Long, campaignId: String): ScheduleResponse?
    suspend fun getCampaignScheduleByCampaign(campaignId: String): ScheduleResponse?
    suspend fun getCampaignBookedSlotsCount(startTime: Long, campaignId: String): Int
}