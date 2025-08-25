package com.heartcare.agni.ui.intervention.add

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
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddInterventionViewModel@Inject constructor(
    private val searchRepository: SearchRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var listOfInterventions by mutableStateOf(listOf(
        "ABC0022 Education - Hypertension",
        "ABC002 Counselling - Diabetes diet",
        "ABC001 Education - Hypertension control",
        "EDU007 Education - High cardiovascular risk",
        "EDU006 Education - Hypercholesterolaemia control",
        "EDU005 Education - Diabetes control (insulin injection)",
        "EDU004 Education - Diabetes control (advanced)",
        "EDU003 Education - Diabetes control (basic)",
        "EDU002 Education - Hypertension control (advanced)",
        "EDU001 Education - Hypertension control (basic)",
        "CSL008 Counselling - weight control",
        "CSL007 Counselling - physical activity",
        "CSL006 Counselling - fruits and vegetables"
    ))

    var selectedInterventionList by mutableStateOf(listOf<String>())

    var bottomNavExpanded by mutableStateOf(false)
    var clearAllConfirmDialog by mutableStateOf(false)

    var isSearching by mutableStateOf(false)
    var previousSearchList by mutableStateOf(listOf<String>())
    var searchQuery by mutableStateOf("")
    var tempSearchQuery by mutableStateOf("")
    var interventionsSearchList by mutableStateOf(listOf<String>())
    var isSearchResult by mutableStateOf(false)

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
        interventionsSearchList = listOf(
            "ABC0022 Education - Hypertension",
            "ABC002 Counselling - Diabetes diet"
        )
    }
}