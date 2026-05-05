package com.heartcare.agni.data.local.repository.nationalId

import android.content.Context
import com.heartcare.agni.data.local.roomdb.dao.NationalIdDao
import com.heartcare.agni.data.server.model.nationalId.NationalIdResponse
import com.heartcare.agni.utils.converters.responseconverter.toNationalIdEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import java.io.File

class NationalIdRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val nationalIdDao: NationalIdDao
) : NationalIdRepository {
    private val fileName = "national_id_data.json"

    private fun getFile(): File {
        return File(context.filesDir, fileName)
    }

    override suspend fun insertNationalIdRecord(vararg nationalIdResponse: NationalIdResponse): List<Long> {
        return nationalIdDao.insertNationalIdRecord(*nationalIdResponse.map { it.toNationalIdEntity() }.toTypedArray())
    }

    override suspend fun getNationalIdData(): String? {
        val file = getFile()
        return if (file.exists()) file.readText() else null
    }
}