package com.heartcare.agni.ui.referral

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.referral.ReferralRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.referral.ReferralResponse
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
class ReferralViewModel @Inject constructor(
    private val referralRepository: ReferralRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    val user = preferenceRepository.getUserDetails()!!

    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var canAddAssessment by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250
    var showAppointmentCompletedDialog by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)
    var existsInOtherHospital by mutableStateOf(false)

    var referralList by mutableStateOf(listOf<ReferralResponse>())
    var todayReferral by mutableStateOf<ReferralResponse?>(null)

    fun getAppointmentInfo(
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

    fun getReferralRecords(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            val appointmentIds =
                getInProgressCompletedAppointmentIds(patientId, appointmentRepository)
            referralList = referralRepository.getReferralRecordsByAppointmentIds(*appointmentIds.toTypedArray()).also {
                todayReferral = it.firstOrNull { referral -> isToday(referral.appUpdatedDate) }
            }
        }
    }
}