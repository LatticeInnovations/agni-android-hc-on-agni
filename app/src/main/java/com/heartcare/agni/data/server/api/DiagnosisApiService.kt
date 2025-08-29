package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.local.model.diagnosis.DiagnosisData
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.diagnosis.DiagnosisMasterResponse
import com.heartcare.agni.data.server.model.diagnosis.DiagnosisResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface DiagnosisApiService {

    @GET("diagnosis/list?name=diagnosisList")
    suspend fun getDiagnosis(): Response<BaseResponse<List<DiagnosisMasterResponse>>>

    @GET("{endPoint}")
    suspend fun getListData(
        @Path("endPoint") endPoint: String,
        @QueryMap(encoded = true) map: Map<String, String>?
    ): Response<BaseResponse<List<DiagnosisResponse>>>

    @POST("{endPoint}")
    suspend fun createData(
        @Path("endPoint") endPoint: String,
        @Body diag: List<DiagnosisData>
    ): Response<BaseResponse<List<CreateResponse>>>
}