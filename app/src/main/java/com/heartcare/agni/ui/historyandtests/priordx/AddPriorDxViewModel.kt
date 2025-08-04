package com.heartcare.agni.ui.historyandtests.priordx

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.enums.PriorDiagnosis
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.priordx.PriorDxRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.priordx.PriorDxResponse
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
class AddPriorDxViewModel @Inject constructor(
    private val priorDxRepository: PriorDxRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): BaseViewModel() {
    val maxCancerFieldLength = 200
    val maxOtherFieldLength = 200

    val user = preferenceRepository.getUserDetails()!!
    var patient by mutableStateOf<PatientResponse?>(null)
    var appointmentResponseLocal by mutableStateOf<AppointmentResponseLocal?>(null)
    var isLaunched by mutableStateOf(false)

    var lastPriorDx by mutableStateOf<PriorDxResponse?>(null)
    var selectedPriorDx by mutableStateOf(listOf<String>())
    var cancerField by mutableStateOf("")
    var isCancerFieldError by mutableStateOf(false)
    var otherField by mutableStateOf("")
    var isOtherFieldError by mutableStateOf(false)

    fun getLastPriorDx(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            lastPriorDx = priorDxRepository.getPriorDxRecords(patientId).firstOrNull()
            lastPriorDx?.let { priorDx ->
                selectedPriorDx = mutableListOf<String>().apply {
                    if (priorDx.hasHypertension) add(PriorDiagnosis.HYPERTENSION.display)
                    if (priorDx.hasHeartDiseases) add(PriorDiagnosis.HEART_DISEASE.display)
                    if (priorDx.hasTransientIschaemicAttack) add(PriorDiagnosis.TIA.display)
                    if (priorDx.hasDiabetes) add(PriorDiagnosis.DIABETES.display)
                    if (priorDx.hasHypercholesterolaemia) add(PriorDiagnosis.HYPERCHOLESTEROLAEMIA.display)
                    if (priorDx.hasCovid) add(PriorDiagnosis.COVID_19.display)
                    if (priorDx.hasCancer) {
                        cancerField = priorDx.cancer!!
                        add(PriorDiagnosis.CANCER.display)
                    }
                    if (priorDx.hasAsthma) add(PriorDiagnosis.ASTHMA.display)
                    if (priorDx.hasChronicObstructivePulmonaryDisease) add(PriorDiagnosis.COPD.display)
                    if (priorDx.hasChronicKidneyDiseases) add(PriorDiagnosis.CHRONIC_KIDNEY_DISEASE.display)
                    if (priorDx.hasTuberculosis) add(PriorDiagnosis.TUBERCULOSIS.display)
                    if (priorDx.hasAids) add(PriorDiagnosis.AIDS_OR_HIV.display)
                    if (priorDx.hasOthers) {
                        otherField = priorDx.others!!
                        add(PriorDiagnosis.OTHERS.display)
                    }
                }
            }
        }
    }

    fun isValid(): Boolean {
        return when {
            PriorDiagnosis.OTHERS.display in selectedPriorDx && otherField.isBlank() -> false
            PriorDiagnosis.CANCER.display in selectedPriorDx && cancerField.isBlank() -> false
            else -> true
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

    private fun getPriorDxResponse(
        priorDxUuid: String = UUIDBuilder.generateUUID(),
        priorDxFhirId: String? = null,
        createdOn: Date = Date()
    ): PriorDxResponse {
        return PriorDxResponse(
            priorDxUuid = priorDxUuid,
            priorDxFhirId = priorDxFhirId,
            appointmentId = appointmentResponseLocal!!.appointmentId
                ?: appointmentResponseLocal!!.uuid,
            cancer = cancerField.ifBlank { null },
            createdOn = createdOn,
            hasAids = selectedPriorDx.contains(PriorDiagnosis.AIDS_OR_HIV.display),
            hasAsthma = selectedPriorDx.contains(PriorDiagnosis.ASTHMA.display),
            hasCancer = selectedPriorDx.contains(PriorDiagnosis.CANCER.display),
            hasChronicKidneyDiseases = selectedPriorDx.contains(PriorDiagnosis.CHRONIC_KIDNEY_DISEASE.display),
            hasChronicObstructivePulmonaryDisease = selectedPriorDx.contains(PriorDiagnosis.COPD.display),
            hasCovid = selectedPriorDx.contains(PriorDiagnosis.COVID_19.display),
            hasDiabetes = selectedPriorDx.contains(PriorDiagnosis.DIABETES.display),
            hasHeartDiseases = selectedPriorDx.contains(PriorDiagnosis.HEART_DISEASE.display),
            hasHypercholesterolaemia = selectedPriorDx.contains(PriorDiagnosis.HYPERCHOLESTEROLAEMIA.display),
            hasHypertension = selectedPriorDx.contains(PriorDiagnosis.HYPERTENSION.display),
            hasOthers = selectedPriorDx.contains(PriorDiagnosis.OTHERS.display),
            hasTransientIschaemicAttack = selectedPriorDx.contains(PriorDiagnosis.TIA.display),
            hasTuberculosis = selectedPriorDx.contains(PriorDiagnosis.TUBERCULOSIS.display),
            others = otherField.ifBlank { null },
            patientId = patient!!.fhirId ?: patient!!.id,
            practitionerId = null,
            practitionerName = null,
            appUpdatedDate = Date()
        )
    }

    fun addPriorDx(
        added: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            getAppointment()
            var uuid = UUIDBuilder.generateUUID()
            var fhirId: String? = null
            lastPriorDx?.let {
                if (isToday(it.createdOn!!)) {
                    uuid = it.priorDxUuid
                    fhirId = it.priorDxFhirId
                }
            }
            val priorDxResponse = getPriorDxResponse(uuid)
            priorDxRepository.insertPriorDx(
                priorDxResponse.copy(
                    appointmentId = appointmentResponseLocal!!.uuid,
                    patientId = patient!!.id,
                    practitionerName = getFullName(
                        user.firstName,
                        user.lastName
                    ),
                    practitionerId = user.fhirId,
                    priorDxFhirId = fhirId
                )
            )
            genericRepository.insertPriorDxRecord(priorDxResponse.copy(createdOn = null))
            checkAndUpdateAppointmentStatusToInProgress(
                inProgressTime = priorDxResponse.createdOn!!,
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