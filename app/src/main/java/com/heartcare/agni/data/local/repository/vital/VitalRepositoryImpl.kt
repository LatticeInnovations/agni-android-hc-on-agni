package com.heartcare.agni.data.local.repository.vital

import com.heartcare.agni.data.local.roomdb.dao.VitalDao
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.utils.converters.responseconverter.toVitalEntity
import com.heartcare.agni.utils.converters.responseconverter.toVitalResponse
import javax.inject.Inject

class VitalRepositoryImpl @Inject constructor(
    private val vitalDao: VitalDao
) : VitalRepository {
    override suspend fun insertVital(vararg vitalResponse: VitalResponse): List<Long> {
        return vitalDao.insertVital(*vitalResponse.map { it.toVitalEntity() }.toTypedArray())
    }

    override suspend fun getLastVital(patientId: String): List<VitalResponse> {
        return vitalDao.getPastVitals(patientId).map { it.toVitalResponse() }
    }

    override suspend fun getVitalByAppointmentId(appointmentId: String): List<VitalResponse> {
        return vitalDao.getVitalsByAppointmentId(appointmentId).map { it.toVitalResponse() }
    }

    override suspend fun updateVital(vitalResponse: VitalResponse): Int {
        return vitalDao.updateVitalData(vitalResponse.toVitalEntity())
    }
}