package com.heartcare.agni.data.server.repository.authentication

import com.heartcare.agni.data.server.model.authentication.LoginResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ResponseMapper

interface AuthenticationRepository {

    suspend fun login(userId: String, password: String): ResponseMapper<LoginResponse>
    suspend fun changePassword(oldPassword: String, newPassword: String): ResponseMapper<Unit>
    suspend fun requestOtp(email: String): ResponseMapper<Unit>
    suspend fun validateCode(email: String, otp: Int): ResponseMapper<Unit>
    suspend fun forgotPassword(email: String, password: String): ResponseMapper<Unit>
}