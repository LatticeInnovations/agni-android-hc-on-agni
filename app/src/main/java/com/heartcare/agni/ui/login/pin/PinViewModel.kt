package com.heartcare.agni.ui.login.pin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : BaseViewModel() {
    val pinLength = 4

    var isLaunched by mutableStateOf(false)
    var screenFlag by mutableIntStateOf(0)

    var isLoading by mutableStateOf(false)
    var isPinInvalid by mutableStateOf(false)

    val focusRequesters = List(4) { FocusRequester() }
    val pinValues = List(4) { mutableStateOf("") }

    fun getPin(): String {
        return preferenceRepository.getPin()
    }

    fun savePin(
        pin: String,
        navigate: () -> Unit
    ) {
        preferenceRepository.setPin(pin)
        navigate()
    }
}