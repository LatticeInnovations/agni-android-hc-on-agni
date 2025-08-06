package com.heartcare.agni.ui.historyandtests.risk.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.server.model.patient.PatientResponse

class AddRiskFactorViewModel: BaseViewModel() {
    var patient by mutableStateOf<PatientResponse?>(null)
    var isLaunched by mutableStateOf(false)

    var useTobacco by mutableStateOf("")
    var tobaccoType by mutableStateOf("")
    var otherTobacco by mutableStateOf("")
    var otherTobaccoError by mutableStateOf(false)
    var tobaccoQuantity by mutableStateOf("")
    var tobaccoQuantityError by mutableStateOf(false)
    var quantityOptions = listOf("Sticks", "Times")
    var selectedQuantityOption by mutableIntStateOf(0)
    var startAge by mutableStateOf("")
    var startAgeError by mutableStateOf(false)
    var startAgeErrorMsg by mutableStateOf("")
    var willingToQuit by mutableStateOf("")

    fun resetTobaccoValues() {
        tobaccoType = ""
        otherTobacco = ""
        otherTobaccoError = false
        tobaccoQuantity = ""
        tobaccoQuantityError = false
        selectedQuantityOption = 0
        startAge = ""
        startAgeError = false
        startAgeErrorMsg = ""
        willingToQuit = ""
    }
}