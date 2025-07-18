package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.model.authentication.ChangePassword
import com.heartcare.agni.data.server.model.authentication.ForgotPasswordRequest
import com.heartcare.agni.data.server.model.authentication.Login
import com.heartcare.agni.data.server.model.authentication.LoginResponse
import com.heartcare.agni.data.server.model.authentication.LoginRequest
import com.heartcare.agni.data.server.model.authentication.Otp
import com.heartcare.agni.data.server.model.authentication.TokenResponse
import com.heartcare.agni.data.server.model.user.UserResponse
import com.heartcare.agni.utils.constants.AuthenticationConstants.TEMP_TOKEN
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthenticationApiService {

    @POST("auth/login")
    suspend fun login(@Body login: Login): Response<BaseResponse<String?>>

    @POST("login")
    suspend fun login(@Body login: LoginRequest): Response<LoginResponse>

    @PUT("user/change-password")
    suspend fun changePassword(@Body changePassword: ChangePassword): Response<Unit>

    @POST("send-otp")
    suspend fun requestOtp(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<Unit>

    @POST("validate-otp")
    suspend fun validateCode(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<Unit>

    @POST("forgot-password")
    suspend fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<Unit>

    @POST("auth/otp")
    suspend fun validateOtp(@Body otp: Otp): Response<BaseResponse<TokenResponse>>

    @GET("user")
    suspend fun getUserDetails(): Response<BaseResponse<UserResponse>>

    @DELETE("user")
    suspend fun deleteUserDetails(@Header(TEMP_TOKEN) tempToken: String): Response<BaseResponse<String?>>
}