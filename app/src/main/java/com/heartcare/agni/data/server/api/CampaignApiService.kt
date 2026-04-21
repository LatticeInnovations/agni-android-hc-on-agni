package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.model.campaign.ScreeningSiteMasterResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface CampaignApiService {

    @GET("campaign/screening-site")
    suspend fun getScreeningSites(@QueryMap map: Map<String, String>): Response<BaseResponse<List<ScreeningSiteMasterResponse>>>
}
