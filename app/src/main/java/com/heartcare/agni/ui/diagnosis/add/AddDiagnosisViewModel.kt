package com.heartcare.agni.ui.diagnosis.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.server.model.patient.PatientResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddDiagnosisViewModel: BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var searchQuery by mutableStateOf("")
    var frequentlyDiagnosedList by mutableStateOf(listOf(
        "A0109, Typhoid fever with other complications",
        "A1810, Tuberculosis of genitourinary system, unspecified",
        "Cholera due to Vibrio cholerae 01, biovar cholerae",
        "A0221, Salmonella meningitis",
        "A0102, Typhoid fever with heart involvement"
    ))

    var selectedDiagnosis by mutableStateOf(listOf<String>())
    var isSearching by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    var searchResults by mutableStateOf(listOf<String>())

    var bottomNavExpanded by mutableStateOf(false)
    var clearAllConfirmDialog by mutableStateOf(false)

    fun searchDiagnosis() {
        viewModelScope.launch {
            isSearching = true
            isLoading = true
            delay(2000)
            searchResults = listOf(
                "A0100, Typhoid fever, unspecified",
                "A0101, Typhoid meningitis",
                "A0102, Typhoid fever with heart involvement",
                "A0103, Typhoid pneumonia",
                "A0104, Typhoid arthritis",
                "A0105, Typhoid osteomyelitis",
                "A0109, Typhoid fever with other complications"
            )
            isLoading = false
        }
    }
}