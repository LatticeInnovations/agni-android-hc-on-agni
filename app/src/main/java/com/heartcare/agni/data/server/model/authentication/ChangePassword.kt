package com.heartcare.agni.data.server.model.authentication

import androidx.annotation.Keep

@Keep
data class ChangePassword(
    val newPassword: String,
    val oldPassword: String
)