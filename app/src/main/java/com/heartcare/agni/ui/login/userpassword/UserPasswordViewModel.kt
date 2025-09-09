package com.heartcare.agni.ui.login.userpassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.repository.crashlytics.CrashlyticsLogger
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.roomdb.FhirAppDatabase
import com.heartcare.agni.data.server.repository.authentication.AuthenticationRepository
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.constants.AuthenticationConstants.AUTHORIZATION
import com.heartcare.agni.utils.constants.AuthenticationConstants.REFRESH_TOKEN
import com.heartcare.agni.utils.constants.ErrorConstants.ERROR_FETCHING_USER_DETAILS
import com.heartcare.agni.utils.constants.ErrorConstants.SOMETHING_WENT_WRONG
import com.heartcare.agni.utils.constants.FirebaseKeyConstants.USER_ID
import com.heartcare.agni.utils.converters.server.responsemapper.ApiEmptyResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiEndResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserPasswordViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val preferenceRepository: PreferenceRepository,
    private val fhirAppDatabase: FhirAppDatabase,
    private val crashlyticsLogger: CrashlyticsLogger,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    val maxUserIdLength = 10
    val minUserIdLength = 3
    val maxPasswordLength = 15

    var isLaunched by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    var pinScreen by mutableIntStateOf(0)

    var userId by mutableStateOf("")
    var isUserIdError by mutableStateOf(false)
    var userIdError by mutableStateOf("")

    var password by mutableStateOf("")
    var isPasswordError by mutableStateOf(false)
    var passwordError by mutableStateOf("")
    var isPasswordVisible by mutableStateOf(false)

    var snackBarError by mutableStateOf("")
    var isPasswordCreated by mutableStateOf(false)

    var showDifferentUserLoginDialog by mutableStateOf(false)

    fun isValid(): Boolean {
        return userId.isNotBlank() && password.isNotBlank() && !isUserIdError && !isPasswordError
    }

    fun login(navigate: () -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            authenticationRepository.login(userId, password).apply {
                isLoading = false
                when (this) {
                    is ApiEndResponse -> {
                        // save user details and navigate
                        isPasswordCreated = body.systemPasswordChanged
                        preferenceRepository.setUserDetails(body)
                        try {
                            preferenceRepository.setAccessToken(headers!![AUTHORIZATION]!!)
                            preferenceRepository.setRefreshToken(headers[REFRESH_TOKEN]!!)
                            navigate()
                        } catch (e: Exception) {
                            Timber.e(e, e.localizedMessage)
                            snackBarError = SOMETHING_WENT_WRONG
                            crashlyticsLogger.logException(
                                e,
                                "Headers not provided.",
                                mapOf(Pair(USER_ID, body.userId))
                            )
                        }
                    }

                    is ApiEmptyResponse -> {
                        // show user details not found error
                        snackBarError = ERROR_FETCHING_USER_DETAILS
                    }

                    is ApiErrorResponse -> {
                        // show error
                        snackBarError = errorMessage
                    }

                    else -> {
                        // show default error
                        snackBarError = SOMETHING_WENT_WRONG
                    }
                }
            }
        }
    }

    fun isDifferentUserLogin(): Boolean {
        val userDetails = preferenceRepository.getUserDetails()
        return if (userDetails == null || userDetails.userId.isBlank()) {
            false
        } else userDetails.userId.lowercase() != userId.lowercase()
    }

    fun clearAllAppData(
        cleared: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            fhirAppDatabase.clearAllTables()
            preferenceRepository.clearPreferences()
            cleared()
        }
    }
}