package com.heartcare.agni.ui.vitalsscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.cvd.records.CVDAssessmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.repository.vital.VitalRepository
import com.heartcare.agni.data.local.roomdb.dao.ScreeningSiteDao
import com.heartcare.agni.data.local.roomdb.entities.campaign.ScreeningSiteMasterEntity
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.common.Queries
import com.heartcare.agni.utils.common.Queries.getAppointment
import com.heartcare.agni.utils.common.Queries.getInProgressCompletedAppointmentIds
import com.heartcare.agni.utils.common.Queries.loadAppointmentInfo
import com.heartcare.agni.utils.common.Queries.loadCampaignVitalInfo
import com.heartcare.agni.utils.constants.VitalConstants.ALL
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
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
    private val screeningSiteDao: ScreeningSiteDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
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
    val kg = "kg"

    var selectedCampaignId by mutableStateOf<String?>(null)
    var screeningSites by mutableStateOf(listOf<ScreeningSiteMasterEntity>())
    var isScreeningSiteEnabled by mutableStateOf(false)
    var existingCampaignVital by mutableStateOf<VitalResponse?>(null)
    var hasExistingCampaignVitalRecord by mutableStateOf(false)

    internal fun loadActiveScreeningSites() {
        viewModelScope.launch(ioDispatcher) {
            val allSites = Queries.getScreeningSites(screeningSiteDao)
            val userFhirId = user.fhirId
            Timber.d("USERID: $userFhirId")
            screeningSites = allSites.filter { site ->
                site.staff.any { it.id == userFhirId }
            }
            isScreeningSiteEnabled = screeningSites.isNotEmpty()

        }
    }

    internal fun getAppointmentInfo(
        callback: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            val campaignId = selectedCampaignId
            if (campaignId != null) {
                // Campaign path
                val info = loadCampaignVitalInfo(
                    patientId = patient!!.id,
                    campaignId = campaignId,
                    appointmentRepository = appointmentRepository,
                    vitalRepository = vitalRepository,
                    screeningSiteDao = screeningSiteDao
                )
                appointment = info.appointment
                existingCampaignVital = info.existingVital
                hasExistingCampaignVitalRecord = info.hasExistingRecord
                canAddAssessment = info.appointment != null && !info.hasExistingRecord
                existsInOtherHospital = false
                isAppointmentCompleted = false
                ifAllSlotsBooked = false
            } else {
                // Facility path
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
                existingCampaignVital = null
                hasExistingCampaignVitalRecord = false
            }
            callback()
        }
    }

    internal fun getVitalsAndCVDRecords() {
        viewModelScope.launch(ioDispatcher) {
            val appointmentIds =
                getInProgressCompletedAppointmentIds(patient!!.id, appointmentRepository)
            
            // Load all screening sites once for mapping names
            val allSites = screeningSiteDao.getScreeningSiteMaster()
            val siteMap = allSites.associateBy { it.id }

            vitals.value = vitalRepository.getLastVitalByAppointmentId(*appointmentIds.toTypedArray()).map { vital ->
                vital.copy(screeningSiteName = vital.campaignId?.let { siteMap[it]?.name })
            }.also {
                todayVital = it.firstOrNull { vital -> isToday(vital.appUpdatedDate) }
            }
            
            isVitalExist = vitals.value.isNotEmpty()
            
            previousRecords = cvdAssessmentRepository.getCVDRecordByAppointmentIds(*appointmentIds.toTypedArray()).map { cvd ->
                cvd.copy(screeningSiteName = cvd.campaignId?.let { siteMap[it]?.name })
            }
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
                addedToQueue = { ids ->
                    viewModelScope.launch(ioDispatcher) {
                        appointment = getAppointment(
                            patientId = patient.id,
                            hospitalCode = user.hospitalCode,
                            appointmentRepository = appointmentRepository
                        )
                        addedToQueue(ids)
                    }
                }
            )
        }
    }

    /** Enroll a patient in a campaign queue (creates new appointment + schedule) */
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
                addedToQueue = { ids ->
                    viewModelScope.launch(ioDispatcher) {
                        appointment = getAppointment(
                            patientId = patient.id,
                            hospitalCode = user.hospitalCode,
                            campaignId = campaignId,
                            appointmentRepository = appointmentRepository
                        )
                        addedToQueue(ids)
                    }
                }
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