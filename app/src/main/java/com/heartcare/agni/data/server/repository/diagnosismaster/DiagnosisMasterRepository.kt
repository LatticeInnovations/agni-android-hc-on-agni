package com.heartcare.agni.data.server.repository.diagnosismaster

import com.heartcare.agni.data.server.model.diagnosis.DiagnosisMasterResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ResponseMapper

interface DiagnosisMasterRepository {

    suspend fun insertDiagnosis(): ResponseMapper<List<DiagnosisMasterResponse>>

    suspend fun getDiagnosis(): List<DiagnosisMasterResponse>
}