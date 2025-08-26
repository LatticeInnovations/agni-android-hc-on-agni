package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.constants.EndPoints.EXAMINATION
import com.heartcare.agni.data.server.model.examination.ExaminationMasterResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

@JvmSuppressWildcards
interface ExaminationApiService {

    @GET("$EXAMINATION/master")
    suspend fun getExaminationMasterList(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<ExaminationMasterResponse>>>
}