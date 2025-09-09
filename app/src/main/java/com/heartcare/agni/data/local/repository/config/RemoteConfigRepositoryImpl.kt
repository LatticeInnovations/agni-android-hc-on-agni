package com.heartcare.agni.data.local.repository.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.heartcare.agni.data.local.model.config.RiskConfig
import com.heartcare.agni.data.local.repository.crashlytics.CrashlyticsLogger
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class RemoteConfigRepositoryImpl @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
    private val gson: Gson,
    private val crashlyticsLogger: CrashlyticsLogger
) : RemoteConfigRepository {

    override suspend fun getRiskConfig(): RiskConfig {
        try {
            // Try to fetch and activate new values if online
            firebaseRemoteConfig.fetchAndActivate().await()
        } catch (e: Exception) {
            // If offline or fails → fallback to cached/default automatically
            Timber.e(e, e.localizedMessage)
            crashlyticsLogger.logException(e)
        }

        val json = firebaseRemoteConfig.getString(RISK_CONFIG_KEY)
        return gson.fromJson(json, RiskConfig::class.java)
    }

    companion object {
        private const val RISK_CONFIG_KEY = "risk_predictor_config_v1"
    }
}
