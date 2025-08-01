package com.heartcare.agni.ui.historyandtests.medication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.enums.MedicationAdherence.Companion.getAdherenceCode
import com.heartcare.agni.data.local.enums.MedicationAdherence.Companion.getAdherenceDisplay
import com.heartcare.agni.data.local.enums.MedicationEnum
import com.heartcare.agni.data.local.enums.MedicationEnum.Companion.getCodeFromMedication
import com.heartcare.agni.data.local.enums.MedicationEnum.Companion.getMedicationFromCode
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.historymedication.HistoryMedicationRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.server.model.historymedication.HistoryMedicationResponse
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
class AddMedicationViewModel @Inject constructor(
    private val historyMedicationRepository: HistoryMedicationRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    val maxOtherFieldLength = 200
    var isLaunched by mutableStateOf(false)
    val user = preferenceRepository.getUserDetails()!!
    var patient by mutableStateOf<PatientResponse?>(null)
    var appointmentResponseLocal by mutableStateOf<AppointmentResponseLocal?>(null)

    var lastHistoryMedication by mutableStateOf<HistoryMedicationResponse?>(null)
    var selectedMedication by mutableStateOf(listOf<String>())
    var sideEffectsField by mutableStateOf("")
    var isSideEffectsFieldError by mutableStateOf(false)
    var otherField by mutableStateOf("")
    var isOtherFieldError by mutableStateOf(false)

    var showAdherenceCard by mutableStateOf(false)
    var isAdherenceExpanded by mutableStateOf(false)
    var adherence by mutableStateOf("")

    fun isValid(): Boolean {
        return when {
            MedicationEnum.OTHERS.display in selectedMedication && otherField.isBlank() -> false
            MedicationEnum.SIDE_EFFECTS.display in selectedMedication && sideEffectsField.isBlank() -> false
            else -> selectedMedication.isNotEmpty()
        }
    }

    fun getLastHistoryMedication(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            lastHistoryMedication =
                historyMedicationRepository.getHistoryMedicationRecords(patientId).firstOrNull()
            lastHistoryMedication?.let { historyMedication ->
                selectedMedication = mutableListOf<String>().apply {
                    addAll(historyMedication.medicinePrescribed.map { getMedicationFromCode(it) })
                }.apply {
                    if (historyMedication.hasSideEffect) {
                        sideEffectsField = historyMedication.sideEffects!!
                        add(MedicationEnum.SIDE_EFFECTS.display)
                    }
                }
                adherence = historyMedication.adherence?.let {
                    showAdherenceCard = true
                    getAdherenceDisplay(it)
                } ?: ""
                otherField = historyMedication.medicinePrescribedOthers ?: ""
            }
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

    private fun getHistoryMedicationResponse(
        uuid: String = UUIDBuilder.generateUUID(),
        fhirId: String? = null,
        appUpdatedDate: Date = Date()
    ): HistoryMedicationResponse {
        return HistoryMedicationResponse(
            uuid = uuid,
            fhirId = fhirId,
            appointmentId = appointmentResponseLocal!!.appointmentId
                ?: appointmentResponseLocal!!.uuid,
            patientId = patient!!.fhirId ?: patient!!.id,
            practitionerId = null,
            practitionerName = null,
            appUpdatedDate = appUpdatedDate,
            adherence = getAdherenceCode(adherence),
            hasSideEffect = selectedMedication.contains(MedicationEnum.SIDE_EFFECTS.display),
            medicinePrescribed = selectedMedication.filter { it != MedicationEnum.SIDE_EFFECTS.display }
                .map { getCodeFromMedication(it) },
            medicinePrescribedOthers = otherField.ifBlank { null },
            sideEffects = sideEffectsField.ifBlank { null }
        )
    }

    fun addHistoryMedication(
        added: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            getAppointment()
            var uuid = UUIDBuilder.generateUUID()
            var fhirId: String? = null
            lastHistoryMedication?.let {
                if (isToday(it.appUpdatedDate)) {
                    uuid = it.uuid
                    fhirId = it.fhirId
                }
            }
            val historyMedicationResponse = getHistoryMedicationResponse(uuid)
            historyMedicationRepository.insertHistoryMedication(
                historyMedicationResponse.copy(
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
            genericRepository.insertHistoryMedicationRecord(historyMedicationResponse)
            checkAndUpdateAppointmentStatusToInProgress(
                inProgressTime = historyMedicationResponse.appUpdatedDate,
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