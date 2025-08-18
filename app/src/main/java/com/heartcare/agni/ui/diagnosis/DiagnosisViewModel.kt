package com.heartcare.agni.ui.diagnosis

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.server.model.patient.PatientResponse

class DiagnosisViewModel: BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var diagnosisList by mutableStateOf(listOf<String>("", ""))
}