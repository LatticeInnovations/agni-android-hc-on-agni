package com.heartcare.agni.data.local.repository.diagnosis

import com.heartcare.agni.data.local.roomdb.dao.DiagnosisDao
import com.heartcare.agni.data.local.roomdb.entities.diagnosis.DiagnosisLocal
import com.heartcare.agni.utils.converters.responseconverter.toDiagnosisEntity
import com.heartcare.agni.utils.converters.responseconverter.toDiagnosisLocal
import javax.inject.Inject

class DiagnosisRepositoryImpl @Inject constructor(private val dao: DiagnosisDao) :
    DiagnosisRepository {
    override suspend fun insertDiagnosis(local: DiagnosisLocal): List<Long> {
        return dao.insertDiagnosis(local.toDiagnosisEntity())
    }

    override suspend fun getPastDiagnosisByAppointmentId(vararg appointmentIds: String): List<DiagnosisLocal> {
        return dao.getPastDiagnosisByAppointmentId(*appointmentIds).map { it.toDiagnosisLocal() }
    }

    override suspend fun updateDiagnosisFhirId(diagnosisUuid: String, fhirId: String) {
        dao.updateDiagnosisFhirId(diagnosisUuid, fhirId)
    }

    override suspend fun getDiagnosisByAppointmentId(appointmentId: String): List<DiagnosisLocal> {
        return dao.getDiagnosisByAppointmentId(appointmentId).map { it.toDiagnosisLocal() }
    }

    override suspend fun updateDiagnosisData(diagnosisLocal: DiagnosisLocal): Int {
        return dao.updateDiagnosisData(diagnosisEntity = diagnosisLocal.toDiagnosisEntity())
    }
}