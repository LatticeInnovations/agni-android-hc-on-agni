package com.heartcare.agni.ui.historyandtests.family

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.FamilyHistoryEnum.Companion.familyHistoryCodeFromDisplay
import com.heartcare.agni.data.local.enums.FamilyHistoryEnum.Companion.familyHistoryDisplayFromCode
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.family.FamilyHistoryRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.server.model.family.FamilyHistoryResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.heartcare.agni.utils.common.Queries.getAppointment
import com.heartcare.agni.utils.common.Queries.getInProgressCompletedAppointmentIds
import com.heartcare.agni.utils.common.Queries.updatePatientLastUpdated
import com.heartcare.agni.utils.converters.responseconverter.NameConverter.getFullName
import com.heartcare.agni.utils.converters.responseconverter.StringUtils.capitalizeFirst
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddFamilyHistoryViewModel@Inject constructor(
    private val familyHistoryRepository: FamilyHistoryRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
): BaseViewModel() {
    val user = preferenceRepository.getUserDetails()!!
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var appointmentResponseLocal by mutableStateOf<AppointmentResponseLocal?>(null)
    
    var selectedFamilyHistory by mutableStateOf(listOf<String>())
    var lastFamilyHistory by mutableStateOf<FamilyHistoryResponse?>(null)

    var showAgeQuestionCard by mutableStateOf(false)
    var isAgeQuestionExpanded by mutableStateOf(false)
    var ageAnswer by mutableStateOf("")
    
    fun getLastFamilyHistory(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            val appointmentIds =
                getInProgressCompletedAppointmentIds(patientId, appointmentRepository)
            lastFamilyHistory =
                familyHistoryRepository.getFamilyHistoryRecordsByAppointmentIds(*appointmentIds.toTypedArray()).firstOrNull()
            lastFamilyHistory?.let { familyHistory ->
                selectedFamilyHistory = mutableListOf<String>().apply {
                    addAll(familyHistory.familyDiseases.map { familyHistoryDisplayFromCode(it) })
                }
                ageAnswer = familyHistory.occurrenceAgeData?.capitalizeFirst() ?: ""
                showAgeQuestionCard = selectedFamilyHistory.isNotEmpty()
            }
        }
    }

    private fun getFamilyHistoryResponse(
        uuid: String = UUIDBuilder.generateUUID(),
        fhirId: String? = null,
        appUpdatedDate: Date = Date()
    ): FamilyHistoryResponse {
        return FamilyHistoryResponse(
            uuid = uuid,
            fhirId = fhirId,
            appointmentId = appointmentResponseLocal!!.appointmentId
                ?: appointmentResponseLocal!!.uuid,
            patientId = patient!!.fhirId ?: patient!!.id,
            practitionerId = null,
            practitionerName = null,
            appUpdatedDate = appUpdatedDate,
            familyDiseases = selectedFamilyHistory.map { familyHistoryCodeFromDisplay(it) },
            occurrenceAgeData = ageAnswer.lowercase().ifBlank { null },
        )
    }

    fun addFamilyHistory(
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
            lastFamilyHistory?.let {
                if (isToday(it.appUpdatedDate)) {
                    uuid = it.uuid
                    fhirId = it.fhirId
                }
            }
            val familyHistoryResponse = getFamilyHistoryResponse(uuid)
            familyHistoryRepository.insertFamilyHistory(
                familyHistoryResponse.copy(
                    appointmentId = appointmentResponseLocal!!.uuid,
                    patientId = patient!!.id,
                    practitionerName = getFullName(
                        user.firstName,
                        user.lastName
                    ),
                    practitionerId = user.fhirId,
                    fhirId = fhirId
                )
            )
            genericRepository.insertFamilyHistoryRecord(familyHistoryResponse)
            checkAndUpdateAppointmentStatusToInProgress(
                inProgressTime = familyHistoryResponse.appUpdatedDate,
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
            added()
        }
    }
}