package com.heartcare.agni.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferenceRepository: PreferenceRepository
) : BaseViewModel() {

    var mPinExists by mutableStateOf(false)

    init {
        mPinExists = preferenceRepository.getPin().isNotBlank()
    }
}