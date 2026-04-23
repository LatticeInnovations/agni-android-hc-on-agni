package com.heartcare.agni.data.local.repository.schedule

import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.data.local.roomdb.dao.ScheduleDao
import com.heartcare.agni.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.heartcare.agni.utils.converters.responseconverter.toScheduleEntity
import com.heartcare.agni.utils.converters.responseconverter.toScheduleResponse
import javax.inject.Inject

import com.heartcare.agni.data.local.roomdb.dao.CampaignScheduleDao
import com.heartcare.agni.utils.converters.responseconverter.*

class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: ScheduleDao,
    private val campaignScheduleDao: CampaignScheduleDao
) : ScheduleRepository {

    override suspend fun getBookedSlotsCount(startTime: Long, hospitalCode: String): Int {
        return scheduleDao.getBookedSlotsCountByStartTime(startTime, hospitalCode)
    }

    override suspend fun insertSchedule(scheduleResponse: ScheduleResponse, recordType: RecordType?): List<Long> {
        return if (recordType == RecordType.SCREENING_SITE) {
            campaignScheduleDao.insertScheduleEntity(scheduleResponse.toCampaignScheduleEntity())
        } else {
            scheduleDao.insertScheduleEntity(scheduleResponse.toScheduleEntity())
        }
    }

    override suspend fun getScheduleById(id: String): ScheduleResponse {
        // Try facility first, then campaign
        val facilitySchedule = scheduleDao.getScheduleById(id)
        if (facilitySchedule.isNotEmpty()) return facilitySchedule[0].toScheduleResponse()
        
        val campaignSchedule = campaignScheduleDao.getScheduleById(id)
        if (campaignSchedule.isNotEmpty()) return campaignSchedule[0].toScheduleResponse()
        
        throw NoSuchElementException("Schedule not found with id: $id")
    }

    override suspend fun updateSchedule(scheduleResponse: ScheduleResponse, recordType: RecordType?): Int {
        return if (recordType == RecordType.SCREENING_SITE) {
            campaignScheduleDao.updateScheduleEntity(scheduleResponse.toCampaignScheduleEntity())
        } else {
            scheduleDao.updateScheduleEntity(scheduleResponse.toScheduleEntity())
        }
    }

    override suspend fun getScheduleByStartTime(startTime: Long, hospitalCode: String): ScheduleResponse? {
        return scheduleDao.getScheduleByStartTime(startTime, hospitalCode)?.toScheduleResponse()
    }

    override suspend fun getCampaignScheduleByStartTime(startTime: Long, campaignId: String): ScheduleResponse? {
        return campaignScheduleDao.getScheduleByStartTime(startTime, campaignId)?.toScheduleResponse()
    }

    override suspend fun getCampaignScheduleByCampaign(campaignId: String): ScheduleResponse? {
        return campaignScheduleDao.getScheduleByCampaign(campaignId)?.toScheduleResponse()
    }

    override suspend fun getCampaignBookedSlotsCount(startTime: Long, campaignId: String): Int {
        return campaignScheduleDao.getBookedSlotsCountByStartTime(startTime, campaignId)
    }
}