package com.heartcare.agni.ui.vitalsscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.cvd.records.CVDAssessmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.repository.vital.VitalRepository
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.common.Queries
import com.heartcare.agni.utils.constants.VitalConstants.ALL
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isToday
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class VitalsViewModel @Inject constructor(
    private val vitalRepository: VitalRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    private val cvdAssessmentRepository: CVDAssessmentRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    val user = preferenceRepository.getUserDetails()!!
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var isVitalExist by mutableStateOf(false)
    private var vitals = MutableStateFlow<List<VitalResponse>>(emptyList())
    var _vitals: StateFlow<List<VitalResponse>> = vitals
    var todayVital by mutableStateOf<VitalResponse?>(null)

    var isWeightSelected by mutableStateOf(true)
    var isGlucoseSelected by mutableStateOf(false)
    var isBPSelected by mutableStateOf(false)

    var msg by mutableStateOf("")
    var isFirstLaunch by mutableStateOf(false)

    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var canAddAssessment by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250
    var showAppointmentCompletedDialog by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)
    var existsInOtherHospital by mutableStateOf(false)

    var selectedOption by mutableStateOf(ALL)

    var previousRecords by mutableStateOf(listOf<CVDResponse>())

    internal fun getAppointmentInfo(
        callback: () -> Unit
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

    internal fun getVitalsAndCVDRecords() {
        viewModelScope.launch(ioDispatcher) {
            vitals.value = vitalRepository.getLastVital(patient!!.id).also {
                todayVital = it.firstOrNull { vital -> isToday(vital.appUpdatedDate) }
            }
            isVitalExist = vitals.value.isNotEmpty()
            previousRecords = cvdAssessmentRepository.getCVDRecord(patient!!.id)
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
}

data class CombineVitalAndCVDRecord(val type: String, val date: Date, val content: Any)