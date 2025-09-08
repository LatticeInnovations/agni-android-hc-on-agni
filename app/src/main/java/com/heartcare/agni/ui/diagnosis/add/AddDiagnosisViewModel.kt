package com.heartcare.agni.ui.diagnosis.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.SearchTypeEnum
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.diagnosis.DiagnosisRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.repository.search.SearchRepository
import com.heartcare.agni.data.local.roomdb.entities.diagnosis.DiagnosisLocal
import com.heartcare.agni.data.server.model.diagnosis.DiagnosisItem
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.heartcare.agni.utils.common.Queries.getAppointment
import com.heartcare.agni.utils.common.Queries.getInProgressCompletedAppointmentIds
import com.heartcare.agni.utils.common.Queries.updatePatientLastUpdated
import com.heartcare.agni.utils.converters.responseconverter.NameConverter.getFullName
import com.heartcare.agni.utils.converters.responseconverter.SymDiagConverter.splitString
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isToday
import com.heartcare.agni.utils.converters.responseconverter.toDiagnosisData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddDiagnosisViewModel @Inject constructor(
    private val diagnosisRepository: DiagnosisRepository,
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

    var searchQuery by mutableStateOf("")
    var frequentlyDiagnosedList by mutableStateOf(listOf<String>())

    var selectedDiagnosis by mutableStateOf(listOf<String>())
    var isSearching by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    var searchResults by mutableStateOf(listOf<String>())

    var bottomNavExpanded by mutableStateOf(false)
    var clearAllConfirmDialog by mutableStateOf(false)

    var lastDiagnosis by mutableStateOf<DiagnosisLocal?>(null)
    var isTodayDiagnosis by mutableStateOf(false)

    init {
        viewModelScope.launch(ioDispatcher) {
            frequentlyDiagnosedList =
                searchRepository.getRecentDiagnosisSearches(searchTypeEnum = SearchTypeEnum.DIAGNOSIS)
        }
    }

    fun getLastDiagnosis(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            val appointmentIds =
                getInProgressCompletedAppointmentIds(patientId, appointmentRepository)
            lastDiagnosis = diagnosisRepository.getPastDiagnosisByAppointmentId(*appointmentIds.toTypedArray()).firstOrNull()
            lastDiagnosis?.let { dx ->
                if (isToday(dx.createdOn)) {
                    isTodayDiagnosis = true
                    selectedDiagnosis = dx.diagnosis.map { "${it.code}, ${it.display}" }
                }
            }
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

    private fun getDiagnosisResponse(
        diagnosisUuid: String = UUIDBuilder.generateUUID(),
        fhirId: String? = null,
        createdOn: Date = Date()
    ): DiagnosisLocal {
        return DiagnosisLocal(
            diagnosisUuid = diagnosisUuid,
            appointmentId = appointmentResponseLocal!!.uuid,
            diagnosisFhirId = fhirId,
            createdOn = createdOn,
            diagnosis = selectedDiagnosis.map {
                DiagnosisItem(
                    code = it.splitString().first,
                    display = it.splitString().second
                )
            },
            symptoms = listOf(),
            practitionerName = getFullName(
                user.firstName,
                user.lastName
            ),
            patientId = patient!!.id,
            progressNote = null
        )
    }

    fun addDiagnosis(
        added: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            appointmentResponseLocal = getAppointment(
                patientId = patient!!.id,
                hospitalCode = user.hospitalCode,
                appointmentRepository = appointmentRepository
            )
            var uuid = UUIDBuilder.generateUUID()
            var fhirId: String? = null
            lastDiagnosis?.let {
                if (isToday(it.createdOn)) {
                    uuid = it.diagnosisUuid
                    fhirId = it.diagnosisFhirId
                }
            }
            val diagnosisResponseLocal = getDiagnosisResponse(diagnosisUuid = uuid, fhirId = fhirId)
            diagnosisRepository.insertDiagnosis(
                diagnosisResponseLocal
            )
            genericRepository.insertSymDiag(
                diagnosisResponseLocal.copy(
                    appointmentId = appointmentResponseLocal!!.appointmentId
                        ?: appointmentResponseLocal!!.uuid,
                    patientId = patient!!.fhirId ?: patient!!.id
                ).toDiagnosisData()
            )
            checkAndUpdateAppointmentStatusToInProgress(
                inProgressTime = diagnosisResponseLocal.createdOn,
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
            insertRecentSearch()
            added()
        }
    }

    private suspend fun insertRecentSearch() {
        selectedDiagnosis.forEach { diagnosis ->
            searchRepository.insertRecentDiagnosisSearch(
                searchQuery = diagnosis,
                searchTypeEnum = SearchTypeEnum.DIAGNOSIS,
                size = 5
            )
        }
    }

    fun retainDiagnosis() {
        selectedDiagnosis = lastDiagnosis!!.diagnosis.map { "${it.code}, ${it.display}" }
    }
}