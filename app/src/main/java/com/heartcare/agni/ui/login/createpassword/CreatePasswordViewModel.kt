package com.heartcare.agni.ui.login.createpassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.server.repository.authentication.AuthenticationRepository
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.constants.ErrorConstants.SOMETHING_WENT_WRONG
import com.heartcare.agni.utils.converters.server.responsemapper.ApiEndResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePasswordViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    val maxPasswordLength = 15

    var isLaunched by mutableStateOf(false)
    var screenFlag by mutableIntStateOf(0)

    var oldPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var isNewPasswordError by mutableStateOf(false)
    var isNewPasswordVisible by mutableStateOf(false)

    var confirmPassword by mutableStateOf("")
    var isConfirmPasswordError by mutableStateOf(false)
    var confirmPasswordError by mutableStateOf("")
    var isConfirmPasswordVisible by mutableStateOf(false)
    var hasInteractedWithConfirmPassword by mutableStateOf(false)

    var snackBarError by mutableStateOf("")

    fun validation(): Boolean {
        return newPassword.isNotBlank() && confirmPassword.isNotBlank() && !isNewPasswordError && !isConfirmPasswordError
    }

    fun savePassword(
        navigate: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            authenticationRepository.changePassword(oldPassword, newPassword).apply {
                when (this) {
                    is ApiEndResponse -> {
                        navigate()
                    }

                    is ApiErrorResponse -> {
                        snackBarError = errorMessage
                    }

                    else -> {
                        snackBarError = SOMETHING_WENT_WRONG
                    }
                }
            }
        }
    }
}