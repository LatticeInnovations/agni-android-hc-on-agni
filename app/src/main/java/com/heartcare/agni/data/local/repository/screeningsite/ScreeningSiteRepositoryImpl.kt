package com.heartcare.agni.data.local.repository.screeningsite

import com.heartcare.agni.data.local.roomdb.dao.ScreeningSiteDao
import com.heartcare.agni.data.server.model.campaign.ScreeningSiteMasterResponse
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toIsoDate
import com.heartcare.agni.utils.converters.responseconverter.toScreeningSiteMasterEntity
import com.heartcare.agni.utils.converters.responseconverter.toScreeningSiteMasterResponse
import jakarta.inject.Inject
import java.util.Date

class ScreeningSiteRepositoryImpl @Inject constructor(
    private val dao: ScreeningSiteDao
) : ScreeningSiteRepository {

    override suspend fun insertScreeningSites(
        screeningSites: List<ScreeningSiteMasterResponse>
    ): List<Long> {
        return dao.insertScreeningSiteMaster(*screeningSites.map { it.toScreeningSiteMasterEntity() }.toTypedArray())
    }

    override suspend fun getScreeningSites(): List<ScreeningSiteMasterResponse> {
        return dao.getScreeningSiteMaster().map { it.toScreeningSiteMasterResponse() }
    }

    override suspend fun getScreeningSiteById(id: String): ScreeningSiteMasterResponse? {
        return dao.getScreeningSiteById(id)?.toScreeningSiteMasterResponse()
    }

    override suspend fun getActiveScreeningSites(): List<ScreeningSiteMasterResponse> {
        val today = Date().toIsoDate()
        return dao.getActiveScreeningSites(today)
            .map { it.toScreeningSiteMasterResponse() }
    }
}