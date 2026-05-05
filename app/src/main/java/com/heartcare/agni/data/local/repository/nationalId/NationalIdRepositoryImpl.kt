package com.heartcare.agni.data.local.repository.nationalId

import com.heartcare.agni.data.local.roomdb.dao.NationalIdDao
import com.heartcare.agni.data.server.model.nationalId.NationalIdResponse
import com.heartcare.agni.utils.converters.responseconverter.toNationalIdResponse
import jakarta.inject.Inject

class NationalIdRepositoryImpl @Inject constructor(
    private val nationalIdDao: NationalIdDao
) : NationalIdRepository {

    override suspend fun getNationalIdData(nationalId: String): NationalIdResponse? {
        return nationalIdDao.getNationalIdData(nationalId).firstOrNull()?.toNationalIdResponse()
    }
}