package com.heartcare.agni.ui.screeningreportdownload

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.Date

@HiltViewModel
class ScreeningReportDownloadViewModel @Inject constructor(
    preferenceRepository: PreferenceRepository,
    private val appointmentRepository: AppointmentRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
): BaseViewModel() {
    val user = preferenceRepository.getUserDetails()!!
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var appointmentList by mutableStateOf(listOf<AppointmentResponseLocal>())

    fun getAppointmentsList(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            appointmentList = appointmentRepository.getAppointmentsOfPatient(patientId)
                .filter { appointmentResponseLocal ->
                    appointmentResponseLocal.hospitalCode == user.hospitalCode &&
                            appointmentResponseLocal.slot.start.time < Date().toEndOfDay()
                            && (
                            appointmentResponseLocal.status == AppointmentStatusEnum.COMPLETED.value
                                    || appointmentResponseLocal.status == AppointmentStatusEnum.IN_PROGRESS.value
                            )
                }
        }
    }
}