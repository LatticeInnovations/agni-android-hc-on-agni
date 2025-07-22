package com.heartcare.agni.data.server.model.authentication

import androidx.annotation.Keep

@Keep
data class LoginResponse(
    val accountGroupId: Int,
    val contactNumber: String?,
    val countryCode: String = "678",
    val createdAt: String,
    val email: String?,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val hospitalId: Int,
    val hospitalName: String,
    val isActive: Boolean,
    val levelThreeName: String,
    val systemPasswordChanged: Boolean,
    val userId: String,
    val userTypeId: Int
)