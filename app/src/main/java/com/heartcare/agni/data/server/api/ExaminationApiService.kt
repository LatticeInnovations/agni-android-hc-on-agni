package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.constants.EndPoints.EXAMINATION
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.examination.ExaminationMasterResponse
import com.heartcare.agni.data.server.model.examination.ExaminationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.QueryMap

@JvmSuppressWildcards
interface ExaminationApiService {

    @GET("$EXAMINATION/master")
    suspend fun getExaminationMasterList(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<ExaminationMasterResponse>>>

    @GET(EXAMINATION)
    suspend fun getExaminations(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<ExaminationResponse>>>

    @POST(EXAMINATION)
    suspend fun postExamination(@Body examinationResponse: List<ExaminationResponse>): Response<BaseResponse<List<CreateResponse>>>

    @PUT(EXAMINATION)
    suspend fun putExamination(@Body examinationResponse: List<ExaminationResponse>): Response<BaseResponse<List<CreateResponse>>>
}