package com.heartcare.agni.data.local.repository.nationalId

interface NationalIdRepository {
    suspend fun saveNationalIdData(json: String)
    suspend fun getNationalIdData(): String?
}