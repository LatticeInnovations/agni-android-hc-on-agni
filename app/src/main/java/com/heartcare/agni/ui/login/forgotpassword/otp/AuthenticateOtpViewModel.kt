package com.heartcare.agni.ui.login.forgotpassword.otp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel

class AuthenticateOtpViewModel: BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var email by mutableStateOf("")
    var otp by mutableStateOf("")
    var isError by mutableStateOf(false)
    var errorMsg by mutableStateOf("")
    var twoMinuteTimer by mutableIntStateOf(120)

    fun isBtnEnabled() : Boolean {
        return otp.length == 6
    }
}