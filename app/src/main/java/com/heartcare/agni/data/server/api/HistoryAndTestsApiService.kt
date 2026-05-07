package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.model.allergy.AllergyResponse
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.family.FamilyHistoryResponse
import com.heartcare.agni.data.server.model.historymedication.HistoryMedicationResponse
import com.heartcare.agni.data.server.model.priordx.PriorDxResponse
import com.heartcare.agni.data.server.model.risk.RiskFactorResponse
import com.heartcare.agni.data.server.model.tobacco.TobaccoCessationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface HistoryAndTestsApiService {
    @POST("{endPoint}")
    suspend fun postPriorDx(@Path("endPoint", encoded = true) endPoint: String, @Body priorDxResponse: List<PriorDxResponse>): Response<BaseResponse<List<CreateResponse>>>
    @GET("{endPoint}")
    suspend fun getPriorDx(@Path("endPoint", encoded = true) endPoint: String, @QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<PriorDxResponse>>>


    @POST("{endPoint}")
    suspend fun postHistoryMedication(@Path("endPoint", encoded = true) endPoint: String, @Body historyMedicationResponse: List<HistoryMedicationResponse>): Response<BaseResponse<List<CreateResponse>>>
    @GET("{endPoint}")
    suspend fun getHistoryMedication(@Path("endPoint", encoded = true) endPoint: String,@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<HistoryMedicationResponse>>>


    @POST("{endPoint}")
    suspend fun postFamilyHistory(@Path("endPoint", encoded = true) endPoint: String, @Body familyHistoryResponse: List<FamilyHistoryResponse>): Response<BaseResponse<List<CreateResponse>>>
    @GET("{endPoint}")
    suspend fun getFamilyHistory(@Path("endPoint", encoded = true) endPoint: String, @QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<FamilyHistoryResponse>>>


    @POST("{endPoint}")
    suspend fun postAllergy(@Path("endPoint", encoded = true) endPoint: String, @Body allergyResponse: List<AllergyResponse>): Response<BaseResponse<List<CreateResponse>>>
    @GET("{endPoint}")
    suspend fun getAllergy(@Path("endPoint", encoded = true) endPoint: String, @QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<AllergyResponse>>>


    @POST("{endPoint}")
    suspend fun postRiskFactor(@Path("endPoint", encoded = true) endPoint: String, @Body riskFactorResponse: List<RiskFactorResponse>): Response<BaseResponse<List<CreateResponse>>>
    @GET("{endPoint}")
    suspend fun getRiskFactors(@Path("endPoint", encoded = true) endPoint: String, @QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<RiskFactorResponse>>>


    @POST("{endPoint}")
    suspend fun postTobaccoCessation(@Path("endPoint", encoded = true) endPoint: String, @Body tobaccoCessationResponse: List<TobaccoCessationResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET("{endPoint}")
    suspend fun getTobaccoCessation(@Path("endPoint", encoded = true) endPoint: String, @QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<TobaccoCessationResponse>>>



}