package com.heartcare.agni.ui.screeningreportdownload

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class ScreeningReportDownloadViewModel @Inject constructor(

): BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var appointmentList = listOf(
        "2 Jun, 2023 · 12:00 PM",
        "3 Jun, 2023 · 1:00 PM",
        "4 Jun, 2023 · 2:00 PM",
        "5 Jun, 2023 · 3:00 PM",
        "6 Jun, 2023 · 4:00 PM",
    )
}