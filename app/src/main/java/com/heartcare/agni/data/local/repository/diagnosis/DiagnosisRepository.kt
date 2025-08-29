package com.heartcare.agni.data.local.repository.diagnosis

import com.heartcare.agni.data.local.roomdb.entities.diagnosis.DiagnosisLocal

interface DiagnosisRepository {

    suspend fun insertDiagnosis(local: DiagnosisLocal): List<Long>
    suspend fun getPastDiagnosis(patientId: String): List<DiagnosisLocal>
    suspend fun updateDiagnosisFhirId(diagnosisUuid: String, fhirId: String)
    suspend fun getDiagnosisByAppointmentId(appointmentId: String): List<DiagnosisLocal>
    suspend fun updateDiagnosisData(diagnosisLocal: DiagnosisLocal): Int
}