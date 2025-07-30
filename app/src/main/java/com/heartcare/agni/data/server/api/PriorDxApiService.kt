package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.constants.EndPoints.PRIOR_DX
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.priordx.PriorDxResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface PriorDxApiService {
    @POST(PRIOR_DX)
    suspend fun postPriorDx(@Body priorDxResponse: List<PriorDxResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET(PRIOR_DX)
    suspend fun getPriorDx(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<PriorDxResponse>>>
}