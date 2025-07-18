package com.heartcare.agni.data.server.repository.authentication

import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.server.api.AuthenticationApiService
import com.heartcare.agni.data.server.model.authentication.ChangePassword
import com.heartcare.agni.data.server.model.authentication.ForgotPasswordRequest
import com.heartcare.agni.data.server.model.authentication.Login
import com.heartcare.agni.data.server.model.authentication.LoginResponse
import com.heartcare.agni.data.server.model.authentication.LoginRequest
import com.heartcare.agni.data.server.model.authentication.Otp
import com.heartcare.agni.data.server.model.authentication.TokenResponse
import com.heartcare.agni.data.server.model.user.UserResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiEmptyResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiEndResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiResponseConverter
import com.heartcare.agni.utils.converters.server.responsemapper.ResponseMapper
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val authenticationApiService: AuthenticationApiService,
    private val preferenceRepository: PreferenceRepository
) : AuthenticationRepository {

    override suspend fun login(userContact: String): ResponseMapper<String?> {
        return ApiResponseConverter.convert(
            authenticationApiService.login(
                Login(
                    userContact = userContact
                )
            )
        )
    }

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
            authenticationApiService.changePassword(
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

    override suspend fun validateOtp(userContact: String, otp: Int): ResponseMapper<TokenResponse> {
        return ApiResponseConverter.convert(
            authenticationApiService.validateOtp(
                Otp(
                    userContact = userContact,
                    otp = otp
                )
            )
        ).apply {
            if (this is ApiEndResponse) {
                preferenceRepository.setAuthenticationToken(body.token)
                getUserDetails()
            }
        }
    }

    override suspend fun getUserDetails(): ResponseMapper<UserResponse> {
        return ApiResponseConverter.convert(
            authenticationApiService.getUserDetails()
        ).apply {
            if (this is ApiEndResponse) {
                body.apply {
                    preferenceRepository.setUserFhirId(userId)
                    preferenceRepository.setUserName(userName)
                    preferenceRepository.setUserRoleId(role[0].roleId)
                    preferenceRepository.setUserRole(role[0].role)
                    preferenceRepository.setOrganizationFhirId(role[0].orgId)
                    preferenceRepository.setOrganization(role[0].orgName)
                    userEmail?.let { email -> preferenceRepository.setUserEmail(email) }
                    mobileNumber?.let { mobileNumber ->
                        preferenceRepository.setUserMobile(
                            mobileNumber
                        )
                    }
                }
            }
        }
    }

    override suspend fun deleteAccount(tempToken: String): ResponseMapper<String?> {
        val deleteUserResponse = authenticationApiService.deleteUserDetails(tempToken)
        return ApiResponseConverter.convert(
            deleteUserResponse
        ).run {
            if (this is ApiEmptyResponse) {
                return ApiEndResponse(body = deleteUserResponse.body()?.message!!)
            } else {
                this
            }
        }
    }
}