package com.heartcare.agni.data.local.repository.crashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
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
        Timber.d("Exception recording to firebase $exception")
        crashlytics.recordException(exception)
    }

    override fun logMessage(message: String) {
        crashlytics.log(message)
    }

    override fun setUser(id: String) {
        crashlytics.setUserId(id)
    }
}