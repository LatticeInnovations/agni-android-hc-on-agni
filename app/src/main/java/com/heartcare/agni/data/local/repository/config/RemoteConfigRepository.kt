package com.heartcare.agni.data.local.repository.config

import com.heartcare.agni.data.local.model.config.RiskConfig

interface RemoteConfigRepository {
    suspend fun getRiskConfig(): RiskConfig
}

