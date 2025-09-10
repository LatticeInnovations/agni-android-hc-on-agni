package com.heartcare.agni.ui.historyandtests

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.allergy.AllergyRepository
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.family.FamilyHistoryRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.historymedication.HistoryMedicationRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.priordx.PriorDxRepository
import com.heartcare.agni.data.local.repository.risk.RiskFactorRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.repository.tobacco.TobaccoCessationRepository
import com.heartcare.agni.data.server.model.allergy.AllergyResponse
import com.heartcare.agni.data.server.model.family.FamilyHistoryResponse
import com.heartcare.agni.data.server.model.historymedication.HistoryMedicationResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.priordx.PriorDxResponse
import com.heartcare.agni.data.server.model.risk.RiskFactorResponse
import com.heartcare.agni.data.server.model.tobacco.TobaccoCessationResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.common.Queries
import com.heartcare.agni.utils.common.Queries.getInProgressCompletedAppointmentIds
import com.heartcare.agni.utils.common.Queries.loadAppointmentInfo
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryTakingAndTestsViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    private val priorDxRepository: PriorDxRepository,
    private val historyMedicationRepository: HistoryMedicationRepository,
    private val familyHistoryRepository: FamilyHistoryRepository,
    private val allergyRepository: AllergyRepository,
    private val riskFactorRepository: RiskFactorRepository,
    private val tobaccoCessationRepository: TobaccoCessationRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var isLoading by mutableStateOf(true)

    val user = preferenceRepository.getUserDetails()!!
    var patient by mutableStateOf<PatientResponse?>(null)

    var priorDxList by mutableStateOf(listOf<PriorDxResponse>())
    var todayPriorDx by mutableStateOf<PriorDxResponse?>(null)

    var medicationList by mutableStateOf(listOf<HistoryMedicationResponse>())
    var todayHistoryMedication by mutableStateOf<HistoryMedicationResponse?>(null)

    var familyHistoryList by mutableStateOf(listOf<FamilyHistoryResponse>())
    var todayFamilyHistory by mutableStateOf<FamilyHistoryResponse?>(null)

    var allergyList by mutableStateOf(listOf<AllergyResponse>())
    var todayAllergy by mutableStateOf<AllergyResponse?>(null)

    var riskFactorsList by mutableStateOf(listOf<RiskFactorResponse>())
    var todayRiskFactor by mutableStateOf<RiskFactorResponse?>(null)

    var tobaccoCessationList by mutableStateOf(listOf<TobaccoCessationResponse>())
    var todayTobaccoCessation by mutableStateOf<TobaccoCessationResponse?>(null)

    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var canAddAssessment by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250
    var showAppointmentCompletedDialog by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)
    var existsInOtherHospital by mutableStateOf(false)

    internal fun getAppointmentInfo(
        callback: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            val info = loadAppointmentInfo(
                patientId = patient!!.id,
                hospitalCode = user.hospitalCode,
                maxNumberOfAppointmentsInADay = maxNumberOfAppointmentsInADay,
                appointmentRepository = appointmentRepository
            )
            appointment = info.appointment
            existsInOtherHospital = info.existsInOtherHospital
            canAddAssessment = info.canAddAssessment
            isAppointmentCompleted = info.isAppointmentCompleted
            ifAllSlotsBooked = info.ifAllSlotsBooked
            callback()
        }
    }

    internal fun addPatientToQueue(
        patient: PatientResponse,
        addedToQueue: (List<Long>) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            Queries.addPatientToQueue(
                patient,
                scheduleRepository,
                genericRepository,
                preferenceRepository,
                appointmentRepository,
                patientLastUpdatedRepository,
                addedToQueue
            )
        }
    }

    internal fun updateStatusToArrived(
        patient: PatientResponse,
        appointment: AppointmentResponseLocal,
        updated: (Int) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            Queries.updateStatusToArrived(
                patient,
                appointment,
                appointmentRepository,
                genericRepository,
                scheduleRepository,
                preferenceRepository,
                patientLastUpdatedRepository,
                updated
            )
        }
    }

    fun getPreviousRecords(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            val appointmentIds =
                getInProgressCompletedAppointmentIds(patientId, appointmentRepository)
            priorDxList = priorDxRepository.getPriorDxRecordsByAppointmentIds(*appointmentIds.toTypedArray()).also {
                todayPriorDx = it.firstOrNull { priorDx -> isToday(priorDx.createdOn!!) }
            }
            medicationList = historyMedicationRepository.getHistoryMedicationRecordsByAppointmentIds(*appointmentIds.toTypedArray()).also {
                todayHistoryMedication = it.firstOrNull { historyMedication -> isToday(historyMedication.appUpdatedDate) }
            }
            familyHistoryList = familyHistoryRepository.getFamilyHistoryRecordsByAppointmentIds(*appointmentIds.toTypedArray()).also {
                todayFamilyHistory = it.firstOrNull { familyHistory -> isToday(familyHistory.appUpdatedDate) }
            }
            allergyList = allergyRepository.getAllergyRecordsByAppointmentIds(*appointmentIds.toTypedArray()).also {
                todayAllergy = it.firstOrNull { allergy -> isToday(allergy.appUpdatedDate) }
            }
            riskFactorsList = riskFactorRepository.getRiskFactorRecordsByAppointmentIds(*appointmentIds.toTypedArray()).also {
                todayRiskFactor = it.firstOrNull { riskFactor -> isToday(riskFactor.appUpdatedDate) }
            }
            tobaccoCessationList = tobaccoCessationRepository.getTobaccoCessationRecordsByAppointmentIds(*appointmentIds.toTypedArray()).also {
                todayTobaccoCessation = it.firstOrNull { tobaccoCessation -> isToday(tobaccoCessation.appUpdatedDate) }
            }
            isLoading = false
        }
    }
}