package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.constants.EndPoints.FAMILY_HISTORY
import com.heartcare.agni.data.server.constants.EndPoints.HISTORY_MEDICATION
import com.heartcare.agni.data.server.constants.EndPoints.PRIOR_DX
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.family.FamilyHistoryResponse
import com.heartcare.agni.data.server.model.historymedication.HistoryMedicationResponse
import com.heartcare.agni.data.server.model.priordx.PriorDxResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface HistoryAndTestsApiService {
    @POST(PRIOR_DX)
    suspend fun postPriorDx(@Body priorDxResponse: List<PriorDxResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET(PRIOR_DX)
    suspend fun getPriorDx(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<PriorDxResponse>>>

    @POST(HISTORY_MEDICATION)
    suspend fun postHistoryMedication(@Body historyMedicationResponse: List<HistoryMedicationResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET(HISTORY_MEDICATION)
    suspend fun getHistoryMedication(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<HistoryMedicationResponse>>>

    @POST(FAMILY_HISTORY)
    suspend fun postFamilyHistory(@Body familyHistoryResponse: List<FamilyHistoryResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET(FAMILY_HISTORY)
    suspend fun getFamilyHistory(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<FamilyHistoryResponse>>>
}