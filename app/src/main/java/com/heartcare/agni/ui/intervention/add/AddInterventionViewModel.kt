package com.heartcare.agni.ui.intervention.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.model.InterventionResponseLocal
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.intervention.InterventionRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.repository.search.SearchRepository
import com.heartcare.agni.data.server.model.intervention.InterventionMasterResponse
import com.heartcare.agni.data.server.model.intervention.InterventionResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.heartcare.agni.utils.common.Queries.getAppointment
import com.heartcare.agni.utils.common.Queries.getInProgressCompletedAppointmentIds
import com.heartcare.agni.utils.common.Queries.updatePatientLastUpdated
import com.heartcare.agni.utils.converters.responseconverter.NameConverter.getFullName
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddInterventionViewModel @Inject constructor(
    private val interventionRepository: InterventionRepository,
    private val searchRepository: SearchRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    val user = preferenceRepository.getUserDetails()!!
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var appointmentResponseLocal by mutableStateOf<AppointmentResponseLocal?>(null)

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

    var todayIntervention by mutableStateOf<InterventionResponseLocal?>(null)

    init {
        viewModelScope.launch(ioDispatcher) {
            interventionsMasterList = interventionRepository.getInterventionMasterList()
        }
    }

    fun getTodayIntervention(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            val appointmentIds =
                getInProgressCompletedAppointmentIds(patientId, appointmentRepository)
            todayIntervention =
                interventionRepository.getInterventionListByAppointmentId(*appointmentIds.toTypedArray()).firstOrNull {
                    isToday(it.appUpdatedDate)
                }
            todayIntervention?.let {
                selectedInterventionList = it.interventions.map { intervention ->
                    interventionRepository.getInterventionMasterByFhirId(intervention.fhirId)
                }
            }
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

    fun saveIntervention(saved: () -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            appointmentResponseLocal = getAppointment(
                patientId = patient!!.id,
                hospitalCode = user.hospitalCode,
                appointmentRepository = appointmentRepository
            )

            var uuid = UUIDBuilder.generateUUID()
            var fhirId: String? = null

            todayIntervention?.let {
                uuid = it.uuid
                fhirId = it.fhirId
            }

            val interventionResponse = InterventionResponse(
                uuid = uuid,
                fhirId = fhirId,
                appUpdatedDate = Date(),
                appointmentId = appointmentResponseLocal!!.appointmentId
                    ?: appointmentResponseLocal!!.uuid,
                patientId = patient!!.fhirId ?: patient!!.id,
                practitionerId = null,
                practitionerName = null,
                interventions = selectedInterventionList.map { it.fhirId }
            )

            interventionRepository.insertIntervention(
                interventionResponse.copy(
                    patientId = patient!!.id,
                    appointmentId = appointmentResponseLocal!!.uuid,
                    practitionerId = user.fhirId,
                    practitionerName = getFullName(
                        user.firstName,
                        user.lastName
                    )
                )
            )

            if (fhirId == null) {
                genericRepository.insertInterventionRecord(interventionResponse)
            } else {
                genericRepository.insertOrUpdateInterventionPut(
                    interventionFhirId = fhirId,
                    interventionResponse = interventionResponse.copy(
                        uuid = null
                    )
                )
            }

            checkAndUpdateAppointmentStatusToInProgress(
                inProgressTime = interventionResponse.appUpdatedDate,
                patient = patient!!,
                appointmentResponseLocal = appointmentResponseLocal!!,
                appointmentRepository = appointmentRepository,
                scheduleRepository = scheduleRepository,
                genericRepository = genericRepository,
                preferenceRepository = preferenceRepository
            )

            updatePatientLastUpdated(
                patient!!.id,
                patientLastUpdatedRepository,
                genericRepository
            )

            saved()
        }
    }
}