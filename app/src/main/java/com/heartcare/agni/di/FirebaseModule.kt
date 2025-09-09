package com.heartcare.agni.di

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.google.gson.Gson
import com.heartcare.agni.R
import com.heartcare.agni.data.local.repository.config.RemoteConfigRepository
import com.heartcare.agni.data.local.repository.config.RemoteConfigRepositoryImpl
import com.heartcare.agni.data.local.repository.crashlytics.CrashlyticsLogger
import com.heartcare.agni.data.local.repository.crashlytics.CrashlyticsLoggerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideCrashlytics(): FirebaseCrashlytics {
        return FirebaseCrashlytics.getInstance()
    }

    @Provides
    @Singleton
    fun provideCrashlyticsLogger(firebaseCrashlytics: FirebaseCrashlytics): CrashlyticsLogger {
        return CrashlyticsLoggerImpl(firebaseCrashlytics)
    }

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = Firebase.remoteConfig
        val settings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 hour
        }
        remoteConfig.setConfigSettingsAsync(settings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        return remoteConfig
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideRemoteConfigRepository(
        firebaseRemoteConfig: FirebaseRemoteConfig,
        gson: Gson,
        crashlyticsLogger: CrashlyticsLogger
    ): RemoteConfigRepository {
        return RemoteConfigRepositoryImpl(firebaseRemoteConfig, gson, crashlyticsLogger)
    }
}
