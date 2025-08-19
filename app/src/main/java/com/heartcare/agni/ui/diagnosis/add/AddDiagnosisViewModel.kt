package com.heartcare.agni.ui.diagnosis.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.SearchTypeEnum
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
    var frequentlyDiagnosedList by mutableStateOf(listOf<String>())

    var selectedDiagnosis by mutableStateOf(listOf<String>())
    var isSearching by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    var searchResults by mutableStateOf(listOf<String>())

    var bottomNavExpanded by mutableStateOf(false)
    var clearAllConfirmDialog by mutableStateOf(false)

    var lastDiagnosis by mutableStateOf<String?>(null)

    init {
        viewModelScope.launch(ioDispatcher) {
            frequentlyDiagnosedList =
                searchRepository.getRecentSymptomAndDiagnosisSearches(searchTypeEnum = SearchTypeEnum.DIAGNOSIS)
        }
    }

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

    fun addDiagnosis(
        added: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            insertRecentSearch()
            added()
        }
    }

    private suspend fun insertRecentSearch() {
        selectedDiagnosis.forEach { diagnosis ->
            searchRepository.insertRecentSymptomAndDiagnosisSearch(
                searchQuery = diagnosis,
                searchTypeEnum = SearchTypeEnum.DIAGNOSIS,
                size = 5
            )
        }
    }
}