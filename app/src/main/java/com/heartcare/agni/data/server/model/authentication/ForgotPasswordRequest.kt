package com.heartcare.agni.data.server.model.authentication

import androidx.annotation.Keep

@Keep
data class ForgotPasswordRequest(
    val context: String,
    val isEmailId: Boolean = true,
    val oneTimePassword: Int? = null,
    val password: String? = null
)