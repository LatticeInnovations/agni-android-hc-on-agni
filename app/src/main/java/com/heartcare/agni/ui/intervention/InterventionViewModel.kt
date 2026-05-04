package com.heartcare.agni.ui.intervention

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.model.InterventionResponseLocal
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.intervention.InterventionRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.local.repository.screeningsite.ScreeningSiteRepository
import com.heartcare.agni.data.server.model.campaign.ScreeningSiteMasterResponse
import com.heartcare.agni.data.local.enums.RecordType
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
class InterventionViewModel @Inject constructor(
    private val interventionRepository: InterventionRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    val screeningSiteRepository: ScreeningSiteRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    val user = preferenceRepository.getUserDetails()!!
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var interventionLists by mutableStateOf(listOf<InterventionResponseLocal>())
    var todayIntervention by mutableStateOf<InterventionResponseLocal?>(null)

    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var canAddAssessment by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250
    var showAppointmentCompletedDialog by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)
    var existsInOtherHospital by mutableStateOf(false)

    var screeningSites by mutableStateOf(listOf<ScreeningSiteMasterResponse>())
    var isScreeningSiteEnabled by mutableStateOf(false)
    var selectedCampaignId by mutableStateOf<String?>(null)
    var selectedType by mutableStateOf<RecordType?>(null)
    var currentStep by mutableIntStateOf(0)

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
    
    fun getAppointmentInfo(
        callback: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            val campaignId = selectedCampaignId
            val info = if (campaignId != null) {
                Queries.getCampaignAppointmentInfo(
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
            ifAllSlotsBooked = info.ifAllSlotsBooked
            callback()
        }
    }

    fun addPatientToQueue(
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

    fun addPatientToCampaignQueue(
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

    fun updateStatusToArrived(
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

    fun getInterventionRecords(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            val allSites = screeningSiteRepository.getScreeningSites()
            val siteMap = allSites.associateBy { it.id }
            val appointmentIds =
                getInProgressCompletedAppointmentIds(patientId, appointmentRepository)
            val screenSiteAppointmentIds = Queries.getScreenSiteAppointmentIds(patientId, appointmentRepository)
            val allCombinedIds = (screenSiteAppointmentIds + appointmentIds).toTypedArray()

            interventionLists = interventionRepository.getInterventionListByAppointmentId(*allCombinedIds).map {record->
                record.copy(screeningSiteName = record.campaignId?.let { siteMap[it]?.name })

            }.also {
                todayIntervention = it.firstOrNull { intervention -> isToday(intervention.appUpdatedDate) }?:
                        it.firstOrNull { record -> record.campaignId != null && isCampaignActive(record.campaignId) }




            }
        }
    }
    private suspend fun isCampaignActive(campaignId: String): Boolean {
        return screeningSiteRepository.getScreeningSiteById(campaignId)?.status == "active"
    }

}