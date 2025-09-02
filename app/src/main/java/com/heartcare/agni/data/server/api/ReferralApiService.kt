package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.constants.EndPoints.REFERRAL
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.healthfacility.HealthFacilityResponse
import com.heartcare.agni.data.server.model.referral.ReferralResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface ReferralApiService {

    @GET("$REFERRAL/healthFacility")
    suspend fun getHealthFacility(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<HealthFacilityResponse>>>

    @GET(REFERRAL)
    suspend fun getReferrals(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<ReferralResponse>>>

    @POST(REFERRAL)
    suspend fun postReferral(@Body referralResponse: List<ReferralResponse>): Response<BaseResponse<List<CreateResponse>>>
}