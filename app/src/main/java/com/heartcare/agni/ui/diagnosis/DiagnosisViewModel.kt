package com.heartcare.agni.ui.diagnosis

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.diagnosis.DiagnosisRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.repository.screeningsite.ScreeningSiteRepository
import com.heartcare.agni.data.local.roomdb.entities.diagnosis.DiagnosisLocal
import com.heartcare.agni.data.server.model.campaign.ScreeningSiteMasterResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.common.Queries
import com.heartcare.agni.utils.common.Queries.getCampaignAppointmentInfo
import com.heartcare.agni.utils.common.Queries.getInProgressCompletedAppointmentIds
import com.heartcare.agni.utils.common.Queries.isCampaignActive
import com.heartcare.agni.utils.common.Queries.loadAppointmentInfo
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosisViewModel @Inject constructor(
    private val diagnosisRepository: DiagnosisRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    private val screeningSiteRepository: ScreeningSiteRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    val user = preferenceRepository.getUserDetails()!!

    var diagnosisList by mutableStateOf(listOf<DiagnosisLocal>())
    var todayDiagnosis by mutableStateOf<DiagnosisLocal?>(null)

    var selectedCampaignId by mutableStateOf<String?>(null)
    var screeningSites by mutableStateOf(listOf<ScreeningSiteMasterResponse>())
    var isScreeningSiteEnabled by mutableStateOf(false)

    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var canAddAssessment by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250
    var showAppointmentCompletedDialog by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)
    var existsInOtherHospital by mutableStateOf(false)
    var currentStep by  mutableIntStateOf(0)

    init {
        loadActiveScreeningSites()
    }

    internal fun loadActiveScreeningSites() {
        viewModelScope.launch(ioDispatcher) {
            val allSites = screeningSiteRepository.getActiveScreeningSites()
            val userFhirId = user.fhirId
            screeningSites = allSites.filter { site ->
                site.staff.any { it.id == userFhirId }
            }
            isScreeningSiteEnabled = screeningSites.isNotEmpty()
        }
    }

    fun getPreviousDiagnosis(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            val allSites = screeningSiteRepository.getScreeningSites()
            val siteMap = allSites.associateBy { it.id }
            val appointmentIds = getInProgressCompletedAppointmentIds(patientId, appointmentRepository)
            val screenSiteAppointmentIds = Queries.getScreenSiteAppointmentIds(patientId, appointmentRepository)
            val allCombinedIds = (screenSiteAppointmentIds + appointmentIds).toTypedArray()

            diagnosisList = diagnosisRepository.getPastDiagnosisByAppointmentId(*allCombinedIds).map { dx ->
                dx.copy(screeningSiteName = dx.campaignId?.let { siteMap[it]?.name })
            }.also {
                todayDiagnosis = it.firstOrNull { dx -> isToday(dx.createdOn) }?:
                it.firstOrNull { record -> record.campaignId != null && isCampaignActive(screeningSiteRepository,record.campaignId) }

            }
        }
    }


    internal fun getAppointmentInfo(
        callback: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            val campaignId = selectedCampaignId
            val info = if (campaignId != null) {
                getCampaignAppointmentInfo(
                    patientId = patient!!.id,
                    campaignId = campaignId,
                    appointmentRepository = appointmentRepository
                )

            } else {
                loadAppointmentInfo(
                    patientId = patient!!.id,
                    hospitalCode = user.hospitalCode,
                    maxNumberOfAppointmentsInADay = maxNumberOfAppointmentsInADay,
                    appointmentRepository = appointmentRepository
                )

            }
            appointment = info.appointment
            existsInOtherHospital = info.existsInOtherHospital
            canAddAssessment = info.canAddAssessment
            isAppointmentCompleted = info.isAppointmentCompleted
            ifAllSlotsBooked = info.isAppointmentCompleted
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

    internal fun addPatientToCampaignQueue(
        patient: PatientResponse,
        campaignId: String,
        addedToQueue: (List<Long>) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            Queries.addPatientToCampaignQueue(
                patient = patient,
                campaignId = campaignId,
                scheduleRepository = scheduleRepository,
                genericRepository = genericRepository,
                appointmentRepository = appointmentRepository,
                addedToQueue = addedToQueue
            )
        }
    }

}