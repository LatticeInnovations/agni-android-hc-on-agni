package com.heartcare.agni.ui.historyandtests.medication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.MedicationEnum
import com.heartcare.agni.data.server.model.patient.PatientResponse

class AddMedicationViewModel: BaseViewModel() {
    val maxOtherFieldLength = 200
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var selectedMedication by mutableStateOf(listOf<String>())
    var sideEffectsField by mutableStateOf("")
    var isSideEffectsFieldError by mutableStateOf(false)
    var otherField by mutableStateOf("")
    var isOtherFieldError by mutableStateOf(false)

    var showAdherenceCard by mutableStateOf(false)
    var isAdherenceExpanded by mutableStateOf(false)
    var adherence by mutableStateOf("")

    fun isValid(): Boolean {
        return when {
            MedicationEnum.OTHERS.display in selectedMedication && otherField.isBlank() -> false
            MedicationEnum.SIDE_EFFECTS.display in selectedMedication && sideEffectsField.isBlank() -> false
            else -> selectedMedication.isNotEmpty()
        }
    }
}