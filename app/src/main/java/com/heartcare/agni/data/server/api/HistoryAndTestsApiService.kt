package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.constants.EndPoints.ALLERGY
import com.heartcare.agni.data.server.constants.EndPoints.FAMILY_HISTORY
import com.heartcare.agni.data.server.constants.EndPoints.HISTORY_MEDICATION
import com.heartcare.agni.data.server.constants.EndPoints.PRIOR_DX
import com.heartcare.agni.data.server.constants.EndPoints.CAMPAIGN_PRIOR_DX
import com.heartcare.agni.data.server.constants.EndPoints.CAMPAIGN_HISTORY_MEDICATION
import com.heartcare.agni.data.server.constants.EndPoints.RISK_FACTOR
import com.heartcare.agni.data.server.constants.EndPoints.TOBACCO_CESSATION
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
import retrofit2.http.QueryMap

interface HistoryAndTestsApiService {
    @POST(PRIOR_DX)
    suspend fun postPriorDx(@Body priorDxResponse: List<PriorDxResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET(PRIOR_DX)
    suspend fun getPriorDx(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<PriorDxResponse>>>

    @POST(CAMPAIGN_PRIOR_DX)
    suspend fun postCampaignPriorDx(@Body priorDxResponse: List<PriorDxResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET(CAMPAIGN_PRIOR_DX)
    suspend fun getCampaignPriorDx(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<PriorDxResponse>>>

    @POST(HISTORY_MEDICATION)
    suspend fun postHistoryMedication(@Body historyMedicationResponse: List<HistoryMedicationResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET(HISTORY_MEDICATION)
    suspend fun getHistoryMedication(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<HistoryMedicationResponse>>>

    @POST(CAMPAIGN_HISTORY_MEDICATION)
    suspend fun postCampaignHistoryMedication(@Body historyMedicationResponse: List<HistoryMedicationResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET(CAMPAIGN_HISTORY_MEDICATION)
    suspend fun getCampaignHistoryMedication(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<HistoryMedicationResponse>>>

    @POST(FAMILY_HISTORY)
    suspend fun postFamilyHistory(@Body familyHistoryResponse: List<FamilyHistoryResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET(FAMILY_HISTORY)
    suspend fun getFamilyHistory(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<FamilyHistoryResponse>>>

    @POST(ALLERGY)
    suspend fun postAllergy(@Body allergyResponse: List<AllergyResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET(ALLERGY)
    suspend fun getAllergy(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<AllergyResponse>>>

    @POST(RISK_FACTOR)
    suspend fun postRiskFactor(@Body riskFactorResponse: List<RiskFactorResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET(RISK_FACTOR)
    suspend fun getRiskFactors(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<RiskFactorResponse>>>

    @POST(TOBACCO_CESSATION)
    suspend fun postTobaccoCessation(@Body tobaccoCessationResponse: List<TobaccoCessationResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET(TOBACCO_CESSATION)
    suspend fun getTobaccoCessation(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<TobaccoCessationResponse>>>
}