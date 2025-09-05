package com.heartcare.agni.data.local.repository.tobacco

import com.heartcare.agni.data.server.model.tobacco.TobaccoCessationResponse

interface TobaccoCessationRepository {
    suspend fun insertTobaccoCessation(vararg tobaccoCessationResponse: TobaccoCessationResponse): List<Long>
    suspend fun getTobaccoCessationRecordsByAppointmentIds(vararg appointmentIds: String): List<TobaccoCessationResponse>
}