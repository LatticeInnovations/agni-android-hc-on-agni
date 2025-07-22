package com.heartcare.agni.data.server.model.authentication

import androidx.annotation.Keep

@Keep
data class ErrorResponse(
    val status: String,
    val message: String,
    val timestamp: String
)