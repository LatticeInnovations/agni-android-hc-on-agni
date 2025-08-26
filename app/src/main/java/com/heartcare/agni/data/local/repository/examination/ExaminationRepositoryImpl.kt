package com.heartcare.agni.data.local.repository.examination

import com.heartcare.agni.data.local.roomdb.dao.ExaminationDao
import com.heartcare.agni.data.server.model.examination.ExaminationMasterResponse
import com.heartcare.agni.utils.converters.responseconverter.toExaminationMasterEntity
import com.heartcare.agni.utils.converters.responseconverter.toExaminationMasterResponse
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
}