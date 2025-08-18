package com.heartcare.agni.ui.diagnosis.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.repository.search.SearchRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDiagnosisViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var searchQuery by mutableStateOf("")
    var frequentlyDiagnosedList by mutableStateOf(
        listOf(
            "A0109, Typhoid fever with other complications",
            "A1810, Tuberculosis of genitourinary system, unspecified",
            "Cholera due to Vibrio cholerae 01, biovar cholerae",
            "A0221, Salmonella meningitis",
            "A0102, Typhoid fever with heart involvement"
        )
    )

    var selectedDiagnosis by mutableStateOf(listOf<String>())
    var isSearching by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    var searchResults by mutableStateOf(listOf<String>())

    var bottomNavExpanded by mutableStateOf(false)
    var clearAllConfirmDialog by mutableStateOf(false)

    var lastDiagnosis by mutableStateOf<String?>(null)

    fun searchDiagnosis() {
        viewModelScope.launch(ioDispatcher) {
            isSearching = true
            isLoading = true
            searchResults = searchRepository.searchDiagnosis(
                searchQuery.trim()
            )
            isLoading = false
        }
    }
}