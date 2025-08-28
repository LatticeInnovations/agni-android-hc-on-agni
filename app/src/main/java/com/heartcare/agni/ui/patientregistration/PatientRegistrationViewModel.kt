package com.heartcare.agni.ui.patientregistration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.heartcare.agni.base.viewmodel.BaseViewModel

class PatientRegistrationViewModel : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)

    var currentStep by mutableIntStateOf(1)
    var totalSteps by mutableIntStateOf(3)
    var isEditing by mutableStateOf(false)
    var openDialog by mutableStateOf(false)
}