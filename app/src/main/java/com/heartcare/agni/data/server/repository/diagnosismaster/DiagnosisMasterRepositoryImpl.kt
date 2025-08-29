package com.heartcare.agni.data.server.repository.diagnosismaster

import com.heartcare.agni.data.local.roomdb.dao.DiagnosisDao
import com.heartcare.agni.data.server.api.DiagnosisApiService
import com.heartcare.agni.data.server.model.diagnosis.DiagnosisMasterResponse
import com.heartcare.agni.utils.converters.responseconverter.toDiagnosisMasterResponse
import com.heartcare.agni.utils.converters.responseconverter.toDiagnosisMasterEntity
import com.heartcare.agni.utils.converters.server.responsemapper.ApiEndResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiResponseConverter
import com.heartcare.agni.utils.converters.server.responsemapper.ResponseMapper
import javax.inject.Inject

class DiagnosisMasterRepositoryImpl @Inject constructor(
    private val diagnosisApiService: DiagnosisApiService,
    private val diagnosisDao: DiagnosisDao
) : DiagnosisMasterRepository {

    override suspend fun insertDiagnosis(): ResponseMapper<List<DiagnosisMasterResponse>> {
        return ApiResponseConverter.convert(
            diagnosisApiService.getDiagnosis(),
            false
        ).run {
            when (this) {
                is ApiEndResponse -> {
                    diagnosisDao.insertDiagnosisMasterEntity(*body.map { it.toDiagnosisMasterEntity() }.toTypedArray())
                    this
                }

                else -> this
            }
        }
    }

    override suspend fun getDiagnosis(): List<DiagnosisMasterResponse> {
        return diagnosisDao.getDiagnosisMasterEntity().map { it.toDiagnosisMasterResponse() }

    }
}