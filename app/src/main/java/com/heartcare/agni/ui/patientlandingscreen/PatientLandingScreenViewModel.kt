package com.heartcare.agni.ui.patientlandingscreen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.heartcare.agni.FhirApp
import com.heartcare.agni.base.viewmodel.BaseAndroidViewModel
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.cvd.records.CVDAssessmentRepository
import com.heartcare.agni.data.local.repository.patient.PatientRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.prescription.PrescriptionRepository
import com.heartcare.agni.data.local.repository.vaccination.ImmunizationRecommendationRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.service.workmanager.utils.Sync
import com.heartcare.agni.service.workmanager.workers.trigger.TriggerWorkerPeriodicImpl
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.heartcare.agni.utils.network.CheckNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.internal.filterList
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PatientLandingScreenViewModel @Inject constructor(
    application: Application,
    private val patientRepository: PatientRepository,
    private val appointmentRepository: AppointmentRepository,
    private val prescriptionRepository: PrescriptionRepository,
    private val cvdAssessmentRepository: CVDAssessmentRepository,
    private val immunizationRecommendationRepository: ImmunizationRecommendationRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    preferenceRepository: PreferenceRepository
) : BaseAndroidViewModel(application) {

    private val syncService by lazy { getApplication<FhirApp>().syncService }

    var isLaunched by mutableStateOf(false)
    val user = preferenceRepository.getUserDetails()!!
    var patient by mutableStateOf<PatientResponse?>(null)

    var appointmentsCount by mutableIntStateOf(0)
    var pastAppointmentsCount by mutableIntStateOf(0)
    var uploadsCount by mutableIntStateOf(0)
    var isFabSelected by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)

    var showIdStatusDialog by mutableStateOf(false)
    var isNationalIdVerified by mutableStateOf(false)

    var cvdRisk by mutableStateOf("")

    var selectedIndex by mutableIntStateOf(0)

    var upcomingVaccine by mutableIntStateOf(0)
    var missedVaccine by mutableIntStateOf(0)
    var takenVaccine by mutableIntStateOf(0)

    private suspend fun syncData() {
        Sync.getWorkerInfo<TriggerWorkerPeriodicImpl>(getApplication<FhirApp>().applicationContext)
            .collectLatest { workInfo ->
                if (workInfo != null && workInfo.state == WorkInfo.State.ENQUEUED) {
                    getApplication<FhirApp>().launchSyncing()
                }
            }
    }

    internal fun downloadPrescriptions(patientFhirId: String) {
        if (CheckNetwork.isInternetAvailable(getApplication<FhirApp>().applicationContext)) {
            viewModelScope.launch(ioDispatcher) {
                syncService.downloadFormPrescription(patientFhirId) { _, _ -> }
                syncData()
            }
        }
    }

    internal suspend fun getPatientData(id: String): PatientResponse {
        return patientRepository.getPatientById(id)[0]
    }

    internal fun getScheduledAppointmentsCount(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            appointmentsCount = appointmentRepository.getAppointmentsOfPatientByStatus(
                patientId,
                AppointmentStatusEnum.SCHEDULED.value
            ).filter { appointmentResponseLocal ->
                appointmentResponseLocal.hospitalCode == user.hospitalCode &&
                appointmentResponseLocal.slot.start.time > Date().toTodayStartDate()
            }.size
            pastAppointmentsCount = appointmentRepository.getAppointmentsOfPatient(patientId)
                .filter { appointmentResponseLocal ->
                    appointmentResponseLocal.hospitalCode == user.hospitalCode &&
                    appointmentResponseLocal.slot.start.time < Date().toEndOfDay() && appointmentResponseLocal.status != AppointmentStatusEnum.SCHEDULED.value
                }.size
        }
    }

    internal fun getUploadsCount(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            uploadsCount = prescriptionRepository.getLastPhotoPrescription(patientId).size
        }
    }

    internal fun getLastCVDRisk(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            cvdRisk = (cvdAssessmentRepository.getCVDRecord(patientId).firstOrNull()?.risk ?: "").toString()
        }
    }

    internal fun getImmunizationRecommendationList(
        patientId: String
    ) {
        viewModelScope.launch(ioDispatcher) {
            val immunizationRecommendationList = immunizationRecommendationRepository.getImmunizationRecommendation(patientId)
            missedVaccine = immunizationRecommendationList.filterList { vaccineStartDate < Date(Date().toTodayStartDate()) && takenOn == null }.sortedBy { it.vaccineStartDate }.size
            takenVaccine = immunizationRecommendationList.filterList { takenOn != null }.sortedByDescending { it.takenOn }.size
            upcomingVaccine = immunizationRecommendationList.size - missedVaccine - takenVaccine
        }
    }
}