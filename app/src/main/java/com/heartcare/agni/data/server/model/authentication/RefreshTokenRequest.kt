package com.heartcare.agni.data.server.model.authentication

import androidx.annotation.Keep

@Keep
data class RefreshTokenRequest(
    val refreshToken: String
)