package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.constants.EndPoints.INTERVENTION
import com.heartcare.agni.data.server.model.intervention.InterventionMasterResponse
import com.heartcare.agni.data.server.model.intervention.InterventionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

@JvmSuppressWildcards
interface InterventionApiService {

    @GET("$INTERVENTION/master")
    suspend fun getInterventionMasterList(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<InterventionMasterResponse>>>

    @GET(INTERVENTION)
    suspend fun getInterventions(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<InterventionResponse>>>
}