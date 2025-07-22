package com.heartcare.agni.ui.login.forgotpassword

import androidx.compose.runtime.getValue
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
class ForgotPasswordViewModel@Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): BaseViewModel() {
    var isLoading by mutableStateOf(false)

    var inputValue by mutableStateOf("")
    var isError by mutableStateOf(false)
    var isServerError by mutableStateOf(false)
    var errorMsg by mutableStateOf("")

    fun isBtnEnabled(): Boolean {
        return inputValue.isNotBlank() && !isError
    }

    fun requestOtp(
        navigate: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            authenticationRepository.requestOtp(email = inputValue).apply {
                isLoading = false
                when(this){
                    is ApiEndResponse -> {
                        navigate()
                    }
                    is ApiErrorResponse -> {
                        isServerError = true
                        errorMsg =
                            if (errorMessage == EMAIL_NOT_REGISTERED_BACKEND) EMAIL_NOT_REGISTERED_ERROR_UI
                            else errorMessage
                    }
                    else -> {
                        isServerError = true
                        errorMsg = FAILED_TO_SEND_EMAIL
                    }
                }
            }
        }
    }
}