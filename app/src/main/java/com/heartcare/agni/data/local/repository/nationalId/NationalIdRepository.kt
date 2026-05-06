package com.heartcare.agni.data.local.repository.nationalId

import com.heartcare.agni.data.server.model.nationalId.NationalIdResponse

interface NationalIdRepository {
    suspend fun getNationalIdData(nationalId: String): NationalIdResponse?
}