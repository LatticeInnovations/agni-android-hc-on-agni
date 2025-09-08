package com.heartcare.agni.data.local.repository.examination

import com.heartcare.agni.data.local.model.examination.ExaminationResponseLocal
import com.heartcare.agni.data.server.model.examination.ExaminationMasterResponse
import com.heartcare.agni.data.server.model.examination.ExaminationResponse

interface ExaminationRepository {
    suspend fun insertExaminationMaster(vararg examinationMasterResponse: ExaminationMasterResponse): List<Long>
    suspend fun getExaminationMaster(): List<ExaminationMasterResponse>
    suspend fun getExaminationMasterByFhirId(fhirId: String): ExaminationMasterResponse
    suspend fun insertExamination(vararg examinationResponse: ExaminationResponse): List<Long>
    suspend fun getExaminationListByAppointmentId(vararg appointmentIds: String): List<ExaminationResponseLocal>
}