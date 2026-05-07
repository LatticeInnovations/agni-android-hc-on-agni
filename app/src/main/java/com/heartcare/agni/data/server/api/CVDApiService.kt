package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

@JvmSuppressWildcards
interface CVDApiService {

    @POST("{endPoint}")
    suspend fun createCVD(
        @Path("endPoint", encoded = true) endPoint: String,
        @Body cvdResponse: List<CVDResponse>
    ): Response<BaseResponse<List<CreateResponse>>>

    @GET("{endPoint}")
    suspend fun getCVD(
        @Path("endPoint", encoded = true) endPoint: String,
        @QueryMap(encoded = true) map: Map<String, String>?
    ): Response<BaseResponse<List<CVDResponse>>>

    @PATCH("{endPoint}")
    @JvmSuppressWildcards
    suspend fun patchListOfChanges(
        @Path("endPoint") endPoint: String,
        @Body patchLogs: List<Map<String, Any>>
    ): Response<BaseResponse<List<CreateResponse>>>
}