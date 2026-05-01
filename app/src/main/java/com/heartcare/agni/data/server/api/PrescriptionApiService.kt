package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.prescription.medication.MedicationResponse
import com.heartcare.agni.data.server.model.prescription.medication.MedicineTimeResponse
import com.heartcare.agni.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.QueryMap

@JvmSuppressWildcards
interface PrescriptionApiService {

    @GET("Medication")
    suspend fun getAllMedications(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<MedicationResponse>>>

    @POST("{endPoint}")
    suspend fun postPrescriptionRelatedData(
        @Path("endPoint") endPoint: String,
        @Body prescriptionData: List<Any>
    ): Response<BaseResponse<List<CreateResponse>>>

    @GET("{endPoint}")
    suspend fun getPastPrescription(
        @Path("endPoint") endPoint: String,
        @QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<PrescriptionResponse>>>

    @GET("sct/medTime")
    suspend fun getMedicineTime(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<MedicineTimeResponse>>>

    @PUT("{endPoint}")
    suspend fun sendPrescriptionPut(@Path("endPoint") endPoint: String, @Body prescriptionResponses: List<PrescriptionResponse>): Response<BaseResponse<List<CreateResponse>>>
}