package com.heartcare.agni.data.local.repository.nationalId

import android.content.Context
import com.heartcare.agni.di.dispatcher.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File

class NationalIdRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : NationalIdRepository {
    private val fileName = "national_id_data.json"

    private fun getFile(): File {
        return File(context.filesDir, fileName)
    }

    override suspend fun saveNationalIdData(json: String) {
        withContext(ioDispatcher) {
            val file = getFile()
            val tempFile = File(context.filesDir, "temp_$fileName")

            // atomic write (prevents corruption)
            tempFile.writeText(json)
            tempFile.renameTo(file)
        }
    }
}