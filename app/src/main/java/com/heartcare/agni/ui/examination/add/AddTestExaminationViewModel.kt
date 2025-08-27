package com.heartcare.agni.ui.examination.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.model.examination.ExaminationResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.examination.ExaminationRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.repository.search.SearchRepository
import com.heartcare.agni.data.server.model.examination.ExaminationMasterResponse
import com.heartcare.agni.data.server.model.examination.ExaminationResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.heartcare.agni.utils.common.Queries.updatePatientLastUpdated
import com.heartcare.agni.utils.converters.responseconverter.NameConverter.getFullName
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isToday
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
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
    
    var todayExamination by mutableStateOf<ExaminationResponseLocal?>(null)

    init {
        viewModelScope.launch(ioDispatcher) {
            testExaminationMasterList = examinationRepository.getExaminationMaster()
        }
    }

    fun getTodayExamination(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            todayExamination =
                examinationRepository.getExaminationList(patientId).firstOrNull {
                    isToday(it.appUpdatedDate)
                }
            todayExamination?.let {
                selectedTestExaminationList = it.examinations.map { examination ->
                    examinationRepository.getExaminationMasterByFhirId(examination.fhirId)
                }
            }
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
            testExaminationSearchList = searchRepository.searchExamination(query.trim())
        }
    }

    private suspend fun getAppointment() {
        appointmentResponseLocal =
            appointmentRepository.getAppointmentListByDate(
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).firstOrNull { appointmentEntity ->
                appointmentEntity.patientId == patient!!.id && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
                        && user.hospitalCode == appointmentEntity.hospitalCode
            }
    }

    fun saveExamination(saved: () -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            getAppointment()

            var uuid = UUIDBuilder.generateUUID()
            var fhirId: String? = null

            todayExamination?.let {
                uuid = it.uuid
                fhirId = it.fhirId
            }

            val examinationResponse = ExaminationResponse(
                uuid = uuid,
                fhirId = fhirId,
                appUpdatedDate = Date(),
                appointmentId = appointmentResponseLocal!!.appointmentId
                    ?: appointmentResponseLocal!!.uuid,
                patientId = patient!!.fhirId ?: patient!!.id,
                practitionerId = null,
                practitionerName = null,
                examinations = selectedTestExaminationList.map { it.fhirId }
            )

            examinationRepository.insertExamination(
                examinationResponse.copy(
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
                genericRepository.insertExaminationRecord(examinationResponse)
            } else {
                genericRepository.insertOrUpdateExaminationPut(
                    examinationFhirId = fhirId,
                    examinationResponse = examinationResponse.copy(
                        uuid = null
                    )
                )
            }

            checkAndUpdateAppointmentStatusToInProgress(
                inProgressTime = examinationResponse.appUpdatedDate,
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