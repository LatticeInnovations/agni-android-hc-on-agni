package com.heartcare.agni.data.local.repository.screeningsite

import com.heartcare.agni.data.server.model.campaign.ScreeningSiteMasterResponse

interface ScreeningSiteRepository {
    suspend fun insertScreeningSites(
        screeningSites: List<ScreeningSiteMasterResponse>
    ): List<Long>

    suspend fun getScreeningSites(): List<ScreeningSiteMasterResponse>

    suspend fun getScreeningSiteById(id: String): ScreeningSiteMasterResponse?

    suspend fun getActiveScreeningSites(): List<ScreeningSiteMasterResponse>
}