package com.heartcare.agni.ui.examination.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.examination.ExaminationRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.repository.search.SearchRepository
import com.heartcare.agni.data.server.model.examination.ExaminationMasterResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddTestExaminationViewModel @Inject constructor(
    private val examinationRepository: ExaminationRepository,
    private val searchRepository: SearchRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    val user = preferenceRepository.getUserDetails()!!
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var appointmentResponseLocal by mutableStateOf<AppointmentResponseLocal?>(null)

    var testExaminationMasterList by mutableStateOf(listOf<ExaminationMasterResponse>())

    var selectedTestExaminationList by mutableStateOf(listOf<ExaminationMasterResponse>())

    var bottomNavExpanded by mutableStateOf(false)
    var clearAllConfirmDialog by mutableStateOf(false)

    var isSearching by mutableStateOf(false)
    var previousSearchList by mutableStateOf(listOf<String>())
    var searchQuery by mutableStateOf("")
    var tempSearchQuery by mutableStateOf("")
    var testExaminationSearchList by mutableStateOf(listOf<ExaminationMasterResponse>())
    var isSearchResult by mutableStateOf(false)

    init {
        viewModelScope.launch(ioDispatcher) {
            testExaminationMasterList = examinationRepository.getExaminationMaster()
        }
    }

    fun insertRecentSearch(query: String, date: Date = Date()) {
        viewModelScope.launch(ioDispatcher) {
            searchRepository.insertRecentTestExaminationSearch(query, date)
        }
    }

    fun getPreviousSearch() {
        viewModelScope.launch(ioDispatcher) {
            previousSearchList = searchRepository.getRecentTestExaminationSearches()
        }
    }

    fun getTestExaminationSearchList(query: String) {
        viewModelScope.launch(ioDispatcher) {
        }
    }
}