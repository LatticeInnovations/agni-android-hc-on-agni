package com.heartcare.agni.ui.login.forgotpassword.otp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.server.repository.authentication.AuthenticationRepository
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.constants.ErrorConstants.EMAIL_NOT_REGISTERED_BACKEND
import com.heartcare.agni.utils.constants.ErrorConstants.EMAIL_NOT_REGISTERED_ERROR_UI
import com.heartcare.agni.utils.constants.ErrorConstants.FAILED_TO_SEND_EMAIL
import com.heartcare.agni.utils.converters.server.responsemapper.ApiEndResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticateOtpViewModel@Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var isLoading by mutableStateOf(false)
    var isResendLoading by mutableStateOf(false)
    var email by mutableStateOf("")
    var otp by mutableStateOf("")
    var isError by mutableStateOf(false)
    var errorMsg by mutableStateOf("")
    var twoMinuteTimer by mutableIntStateOf(120)
    var snackBarMsg by mutableStateOf("")

    fun isBtnEnabled() : Boolean {
        return otp.length == 6
    }

    fun validateOtp(
        navigate: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            authenticationRepository.validateCode(email, otp.toInt()).apply {
                isLoading = false
                when(this){
                    is ApiEndResponse -> {
                        navigate()
                    }
                    is ApiErrorResponse -> {
                        isError = true
                        errorMsg = errorMessage
                    }
                    else -> {
                        isError = true
                    }
                }
            }
        }
    }

    fun requestOtp(requested: () -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            authenticationRepository.requestOtp(email = email).apply {
                isResendLoading = false
                when(this){
                    is ApiEndResponse -> {
                        requested()
                    }
                    is ApiErrorResponse -> {
                        snackBarMsg =
                            if (errorMessage == EMAIL_NOT_REGISTERED_BACKEND) EMAIL_NOT_REGISTERED_ERROR_UI
                            else errorMessage
                    }
                    else -> {
                        snackBarMsg = FAILED_TO_SEND_EMAIL
                    }
                }
            }
        }
    }
}