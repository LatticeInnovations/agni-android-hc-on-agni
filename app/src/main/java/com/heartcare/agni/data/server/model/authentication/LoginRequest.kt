package com.heartcare.agni.data.server.model.authentication

import androidx.annotation.Keep

@Keep
data class LoginRequest(
    val userId: String,
    val password: String
)
