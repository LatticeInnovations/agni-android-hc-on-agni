package com.heartcare.agni.data.server.api

import com.heartcare.agni.data.server.model.authentication.ChangePassword
import com.heartcare.agni.data.server.model.authentication.ForgotPasswordRequest
import com.heartcare.agni.data.server.model.authentication.LoginRequest
import com.heartcare.agni.data.server.model.authentication.LoginResponse
import com.heartcare.agni.data.server.model.authentication.RefreshTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthenticationApiService {

    @POST("login")
    suspend fun login(@Body login: LoginRequest): Response<LoginResponse>

    @POST("send-otp")
    suspend fun requestOtp(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<Unit>

    @POST("validate-otp")
    suspend fun validateCode(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<Unit>

    @POST("forgot-password")
    suspend fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<Unit>

    @PUT("refresh-token")
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Response<Unit>
}

interface AuthenticationApiServiceWithToken {

    @PUT("user/change-password")
    suspend fun changePassword(@Body changePassword: ChangePassword): Response<Unit>
}