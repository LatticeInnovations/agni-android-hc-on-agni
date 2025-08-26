package com.heartcare.agni.data.local.repository.examination

import com.heartcare.agni.data.server.model.examination.ExaminationMasterResponse

interface ExaminationRepository {
    suspend fun insertExaminationMaster(vararg examinationMasterResponse: ExaminationMasterResponse): List<Long>
    suspend fun getExaminationMaster(): List<ExaminationMasterResponse>
}