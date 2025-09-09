package com.heartcare.agni.data.local.repository.crashlytics

interface CrashlyticsLogger {
    fun logException(
        exception: Throwable,
        message: String? = null,
        customKeys: Map<String, Any>? = null
    )

    fun logMessage(message: String)
    fun setUser(id: String)
}