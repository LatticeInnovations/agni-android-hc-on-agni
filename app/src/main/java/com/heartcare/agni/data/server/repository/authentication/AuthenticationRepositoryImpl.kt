package com.heartcare.agni.data.server.repository.authentication

import com.heartcare.agni.data.server.api.AuthenticationApiService
import com.heartcare.agni.data.server.api.AuthenticationApiServiceWithToken
import com.heartcare.agni.data.server.model.authentication.ChangePassword
import com.heartcare.agni.data.server.model.authentication.ForgotPasswordRequest
import com.heartcare.agni.data.server.model.authentication.LoginRequest
import com.heartcare.agni.data.server.model.authentication.LoginResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiResponseConverter
import com.heartcare.agni.utils.converters.server.responsemapper.ResponseMapper
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val authenticationApiService: AuthenticationApiService,
    private val authenticationApiServiceWithToken: AuthenticationApiServiceWithToken
) : AuthenticationRepository {

    override suspend fun login(userId: String, password: String): ResponseMapper<LoginResponse> {
        return ApiResponseConverter.convert(
            authenticationApiService.login(
                LoginRequest(
                    userId = userId,
                    password = password
                )
            )
        )
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): ResponseMapper<Unit> {
        return ApiResponseConverter.convert(
            authenticationApiServiceWithToken.changePassword(
                ChangePassword(
                    oldPassword = oldPassword,
                    newPassword = newPassword
                )
            )
        )
    }

    override suspend fun requestOtp(email: String): ResponseMapper<Unit> {
        return ApiResponseConverter.convert(
            authenticationApiService.requestOtp(
                ForgotPasswordRequest(context = email)
            )
        )
    }

    override suspend fun validateCode(
        email: String,
        otp: Int
    ): ResponseMapper<Unit> {
        return ApiResponseConverter.convert(
            authenticationApiService.validateCode(
                ForgotPasswordRequest(context = email, oneTimePassword = otp)
            )
        )
    }

    override suspend fun forgotPassword(
        email: String,
        password: String
    ): ResponseMapper<Unit> {
        return ApiResponseConverter.convert(
            authenticationApiService.forgotPassword(
                ForgotPasswordRequest(context = email, password = password)
            )
        )
    }
}