package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.QueryMap

import retrofit2.http.Path

@JvmSuppressWildcards
interface ScheduleAndAppointmentApiService {

    @GET("{endPoint}")
    suspend fun getScheduleList(
        @Path("endPoint", encoded = true) endPoint: String,
        @QueryMap(encoded = true) map: Map<String, String>?
    ): Response<BaseResponse<List<ScheduleResponse>>>

    @POST("{endPoint}")
    suspend fun postScheduleData(
        @Path("endPoint", encoded = true) endPoint: String,
        @Body scheduleResponses: List<Any>
    ): Response<BaseResponse<List<CreateResponse>>>

    @GET("{endPoint}")
    suspend fun getAppointmentList(
        @Path("endPoint", encoded = true) endPoint: String,
        @QueryMap(encoded = true) map: Map<String, String>?
    ): Response<BaseResponse<List<AppointmentResponse>>>

    @POST("{endPoint}")
    suspend fun createAppointment(
        @Path("endPoint", encoded = true) endPoint: String,
        @Body appointmentResponse: List<Any>
    ): Response<BaseResponse<List<CreateResponse>>>

    @PATCH("Appointment")
    suspend fun patchListOfChanges(@Body patchLogs: List<Map<String, Any>>): Response<BaseResponse<List<CreateResponse>>>
}