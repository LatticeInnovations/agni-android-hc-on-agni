package com.heartcare.agni.ui.intervention.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.repository.intervention.InterventionRepository
import com.heartcare.agni.data.local.repository.search.SearchRepository
import com.heartcare.agni.data.server.model.intervention.InterventionMasterResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddInterventionViewModel@Inject constructor(
    private val interventionRepository: InterventionRepository,
    private val searchRepository: SearchRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var interventionsMasterList by mutableStateOf(listOf<InterventionMasterResponse>())

    var selectedInterventionList by mutableStateOf(listOf<InterventionMasterResponse>())

    var bottomNavExpanded by mutableStateOf(false)
    var clearAllConfirmDialog by mutableStateOf(false)

    var isSearching by mutableStateOf(false)
    var previousSearchList by mutableStateOf(listOf<String>())
    var searchQuery by mutableStateOf("")
    var tempSearchQuery by mutableStateOf("")
    var interventionsSearchList by mutableStateOf(listOf<InterventionMasterResponse>())
    var isSearchResult by mutableStateOf(false)

    init {
        viewModelScope.launch(ioDispatcher) {
            interventionsMasterList = interventionRepository.getInterventionMasterList()
        }
    }

    fun insertRecentSearch(query: String, date: Date = Date()) {
        viewModelScope.launch(ioDispatcher) {
            searchRepository.insertRecentInterventionSearch(query, date)
        }
    }

    fun getPreviousSearch() {
        viewModelScope.launch(ioDispatcher) {
            previousSearchList = searchRepository.getRecentInterventionSearches()
        }
    }

    fun getInterventionsSearchList(query: String) {
        viewModelScope.launch(ioDispatcher) {
            interventionsSearchList = searchRepository.searchIntervention(query.trim())
        }
    }
}