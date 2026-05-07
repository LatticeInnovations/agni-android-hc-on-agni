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
import retrofit2.http.Path
import retrofit2.http.QueryMap

@JvmSuppressWildcards
interface ExaminationApiService {

    @GET("$EXAMINATION/master")
    suspend fun getExaminationMasterList(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<ExaminationMasterResponse>>>

    @GET("{endPoint}")
    suspend fun getExaminations(
        @Path("endPoint", encoded = true) endPoint: String,
        @QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<ExaminationResponse>>>

    @POST("{endPoint}")
    suspend fun postExamination(
        @Path("endPoint", encoded = true) endPoint: String,
        @Body examinationResponse: List<ExaminationResponse>): Response<BaseResponse<List<CreateResponse>>>

    @PUT("{endPoint}")
    suspend fun putExamination(
        @Path("endPoint", encoded = true) endPoint: String,
        @Body examinationResponse: List<ExaminationResponse>): Response<BaseResponse<List<CreateResponse>>>
}