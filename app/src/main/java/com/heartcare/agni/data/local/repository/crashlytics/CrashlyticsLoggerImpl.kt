package com.heartcare.agni.data.local.repository.crashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

class CrashlyticsLoggerImpl @Inject constructor(
    private val crashlytics: FirebaseCrashlytics
) : CrashlyticsLogger {

    override fun logException(
        exception: Throwable,
        message: String?,
        customKeys: Map<String, Any>?
    ) {
        message?.let { crashlytics.log(it) }

        customKeys?.forEach { (key, value) ->
            crashlytics.setCustomKey(key, value.toString())
        }

        crashlytics.recordException(exception)
    }

    override fun logMessage(message: String) {
        crashlytics.log(message)
    }

    override fun setUser(id: String) {
        crashlytics.setUserId(id)
    }
}