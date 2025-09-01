package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.constants.EndPoints.REFERRAL
import com.heartcare.agni.data.server.model.healthfacility.HealthFacilityResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ReferralApiService {

    @GET("$REFERRAL/healthFacility")
    suspend fun getHealthFacility(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<HealthFacilityResponse>>>
}