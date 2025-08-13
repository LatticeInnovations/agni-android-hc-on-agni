package com.heartcare.agni.data.local.repository.vital

import com.heartcare.agni.data.server.model.vitals.VitalResponse

interface VitalRepository {
    suspend fun insertVital(vararg vitalResponse: VitalResponse): List<Long>
    suspend fun getLastVital(patientId: String): List<VitalResponse>
    suspend fun getVitalByAppointmentId(appointmentId: String): List<VitalResponse>
    suspend fun updateVital(vitalResponse: VitalResponse): Int
}