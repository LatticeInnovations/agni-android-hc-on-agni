package com.heartcare.agni.ui.historyandtests.family

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.server.model.patient.PatientResponse

class AddFamilyHistoryViewModel: BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var selectedFamilyHistory by mutableStateOf(listOf<String>())

    var showAgeQuestionCard by mutableStateOf(false)
    var isAgeQuestionExpanded by mutableStateOf(false)
    var ageAnswer by mutableStateOf("")
}