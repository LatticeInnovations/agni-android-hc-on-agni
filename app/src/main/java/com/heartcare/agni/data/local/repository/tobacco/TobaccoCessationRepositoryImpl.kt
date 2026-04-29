package com.heartcare.agni.data.local.repository.tobacco

import com.heartcare.agni.data.local.roomdb.dao.TobaccoCessationDao
import com.heartcare.agni.data.server.model.tobacco.TobaccoCessationResponse
import com.heartcare.agni.utils.converters.responseconverter.toTobaccoCessationEntity
import com.heartcare.agni.utils.converters.responseconverter.toTobaccoCessationResponse
import javax.inject.Inject

class TobaccoCessationRepositoryImpl@Inject constructor(
    private val tobaccoCessationDao: TobaccoCessationDao
): TobaccoCessationRepository {
    override suspend fun insertTobaccoCessation(vararg tobaccoCessationResponse: TobaccoCessationResponse): List<Long> {
        return tobaccoCessationDao.insertTobaccoCessationRecord(*tobaccoCessationResponse.map { it.toTobaccoCessationEntity() }.toTypedArray())
    }

    override suspend fun getTobaccoCessationRecordsByAppointmentIds(vararg appointmentIds: String): List<TobaccoCessationResponse> {
        return tobaccoCessationDao.getTobaccoCessationRecordsByAppointmentIds(*appointmentIds).map { it.toTobaccoCessationResponse() }
    }

    override suspend fun getLatestTobaccoCessationForCampaign(
        patientId: String,
        campaignId: String
    ): TobaccoCessationResponse? {
        return tobaccoCessationDao.getLatestTobaccoCessationForCampaign(patientId, campaignId)
            ?.toTobaccoCessationResponse()
    }
}