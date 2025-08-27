package com.heartcare.agni.data.local.repository.examination

import com.heartcare.agni.data.local.model.examination.ExaminationResponseLocal
import com.heartcare.agni.data.local.roomdb.dao.ExaminationDao
import com.heartcare.agni.data.server.model.examination.ExaminationMasterResponse
import com.heartcare.agni.data.server.model.examination.ExaminationResponse
import com.heartcare.agni.utils.converters.responseconverter.toExaminationEntity
import com.heartcare.agni.utils.converters.responseconverter.toExaminationMasterEntity
import com.heartcare.agni.utils.converters.responseconverter.toExaminationMasterResponse
import com.heartcare.agni.utils.converters.responseconverter.toExaminationResponseLocal
import javax.inject.Inject

class ExaminationRepositoryImpl @Inject constructor(
    private val examinationDao: ExaminationDao
): ExaminationRepository {
    override suspend fun insertExaminationMaster(vararg examinationMasterResponse: ExaminationMasterResponse): List<Long> {
        return examinationDao.insertExaminationMaster(*examinationMasterResponse.map { it.toExaminationMasterEntity() }.toTypedArray())
    }

    override suspend fun getExaminationMaster(): List<ExaminationMasterResponse> {
        return examinationDao.getExaminationMaster().map { it.toExaminationMasterResponse() }
    }

    override suspend fun getExaminationMasterByFhirId(fhirId: String): ExaminationMasterResponse {
        return examinationDao.getExaminationByFhirId(fhirId).toExaminationMasterResponse()
    }

    override suspend fun insertExamination(vararg examinationResponse: ExaminationResponse): List<Long> {
        return examinationDao.insertExamination(*examinationResponse.map { it.toExaminationEntity() }.toTypedArray())
    }

    override suspend fun getExaminationList(patientId: String): List<ExaminationResponseLocal> {
        return examinationDao.getExaminations(patientId).map { it.toExaminationResponseLocal(examinationDao) }
    }
}