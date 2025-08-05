package com.heartcare.agni.ui.historyandtests

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.allergy.AllergyRepository
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.family.FamilyHistoryRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.historymedication.HistoryMedicationRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.priordx.PriorDxRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.server.model.allergy.AllergyResponse
import com.heartcare.agni.data.server.model.family.FamilyHistoryResponse
import com.heartcare.agni.data.server.model.historymedication.HistoryMedicationResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.priordx.PriorDxResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.common.Queries
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isToday
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
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
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
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
        callback: () -> Unit,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            val todayStart = Date().toTodayStartDate()
            val todayEnd = Date().toEndOfDay()
            val patientId = patient?.id ?: return@launch callback()

            val appointmentsToday = appointmentRepository.getAppointmentsOfPatientByDate(
                patientId, todayStart, todayEnd
            )

            // Determine if assessment can be added
            appointmentsToday?.let {
                existsInOtherHospital = it.hospitalCode != user.hospitalCode
                val status = it.status
                canAddAssessment = (status == AppointmentStatusEnum.ARRIVED.value ||
                        status == AppointmentStatusEnum.WALK_IN.value ||
                        status == AppointmentStatusEnum.IN_PROGRESS.value)
                        && it.hospitalCode == user.hospitalCode

                isAppointmentCompleted = status == AppointmentStatusEnum.COMPLETED.value
                        && it.hospitalCode == user.hospitalCode
            }

            // Get the appointment matching today's time window and scheduled status
            appointment = appointmentRepository
                .getAppointmentsOfPatientByStatus(patientId, AppointmentStatusEnum.SCHEDULED.value)
                .firstOrNull {
                    it.slot.start.time in todayStart..todayEnd
                            && it.hospitalCode == user.hospitalCode
                }

            // Check if all slots are booked
            val bookedAppointments =
                appointmentRepository.getAppointmentListByDate(todayStart, todayEnd)
                    .count {
                        it.status != AppointmentStatusEnum.CANCELLED.value
                                && it.hospitalCode == user.hospitalCode
                    }

            ifAllSlotsBooked = bookedAppointments >= maxNumberOfAppointmentsInADay

            callback()
        }
    }


    internal fun addPatientToQueue(
        patient: PatientResponse,
        addedToQueue: (List<Long>) -> Unit,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
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
        updated: (Int) -> Unit,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
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
            priorDxList = priorDxRepository.getPriorDxRecords(patientId).also {
                todayPriorDx = it.firstOrNull { priorDx -> isToday(priorDx.createdOn!!) }
            }
            medicationList = historyMedicationRepository.getHistoryMedicationRecords(patientId).also {
                todayHistoryMedication = it.firstOrNull { historyMedication -> isToday(historyMedication.appUpdatedDate) }
            }
            familyHistoryList = familyHistoryRepository.getFamilyHistoryRecords(patientId).also {
                todayFamilyHistory = it.firstOrNull { familyHistory -> isToday(familyHistory.appUpdatedDate) }
            }
            allergyList = allergyRepository.getAllergyRecords(patientId).also {
                todayAllergy = it.firstOrNull { allergy -> isToday(allergy.appUpdatedDate) }
            }
            isLoading = false
        }
    }
}