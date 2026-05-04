package com.heartcare.agni.ui.prescription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.model.prescription.MedicationLocal
import com.heartcare.agni.data.local.model.prescription.PrescriptionResponseLocal
import com.heartcare.agni.data.local.model.prescription.medication.MedicationResponseWithMedication
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.medication.MedicationRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.prescription.PrescriptionRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.repository.screeningsite.ScreeningSiteRepository
import com.heartcare.agni.data.local.repository.search.SearchRepository
import com.heartcare.agni.data.local.roomdb.entities.medication.MedicineTimingEntity
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.heartcare.agni.data.server.model.campaign.ScreeningSiteMasterResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.prescription.medication.MedicationResponse
import com.heartcare.agni.data.server.model.prescription.prescriptionresponse.Medication
import com.heartcare.agni.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.common.Queries
import com.heartcare.agni.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.heartcare.agni.utils.common.Queries.getAppointment
import com.heartcare.agni.utils.common.Queries.getInProgressCompletedAppointmentIds
import com.heartcare.agni.utils.common.Queries.loadAppointmentInfo
import com.heartcare.agni.utils.common.Queries.updatePatientLastUpdated
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isToday
import com.heartcare.agni.utils.converters.responseconverter.toMedicationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PrescriptionViewModel @Inject constructor(
    private val prescriptionRepository: PrescriptionRepository,
    private val medicationRepository: MedicationRepository,
    private val searchRepository: SearchRepository,
    private val genericRepository: GenericRepository,
    private val appointmentRepository: AppointmentRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    val preferenceRepository: PreferenceRepository,
    val screeningSiteRepository: ScreeningSiteRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    val user = preferenceRepository.getUserDetails()!!
    var isLaunched by mutableStateOf(false)
    var tabs = listOf("Previous prescription", "Quick select")

    var isSearching by mutableStateOf(false)
    var isSearchResult by mutableStateOf(false)

    var bottomNavExpanded by mutableStateOf(false)
    var clearAllConfirmDialog by mutableStateOf(false)

    var patient by mutableStateOf<PatientResponse?>(null)

    var medicationsList by mutableStateOf(listOf<MedicationResponse>())
    var selectedMedicationsList by mutableStateOf(listOf<MedicationResponse>())
    var checkedMedication by mutableStateOf<MedicationResponse?>(null)

    var medicationDirectionsList by mutableStateOf(listOf<MedicineTimingEntity>())
    var medicationsResponseWithMedicationList by mutableStateOf(listOf<MedicationResponseWithMedication>())
    var medicationToEdit by mutableStateOf<MedicationResponseWithMedication?>(null)

    var searchQuery by mutableStateOf("")
    var previousSearchList by mutableStateOf(listOf<String>())
    var medicationsSearchList by mutableStateOf(listOf<MedicationResponse>())

    var previousPrescriptionList by mutableStateOf(listOf<PrescriptionAndMedicineRelation>())
    var todayPrescription by mutableStateOf<PrescriptionAndMedicineRelation?>(null)

    var appointmentResponseLocal: AppointmentResponseLocal? = null

    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var canAddAssessment by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250
    var showAppointmentCompletedDialog by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)
    var existsInOtherHospital by mutableStateOf(false)
    var isScreeningSiteEnabled by mutableStateOf(false)
    var currentStep by mutableIntStateOf(0)
    var screeningSites by mutableStateOf<List<ScreeningSiteMasterResponse>>(listOf())
    var selectedCampaignId by mutableStateOf<String?>(null)
    var selectedType by mutableStateOf<RecordType?>(null)

    init {
        getScreeningSites()
    }

    fun getScreeningSites() {
        viewModelScope.launch(ioDispatcher) {
            val allSites = screeningSiteRepository.getActiveScreeningSites()
            val userFhirId = user.fhirId
            screeningSites = allSites.filter { site ->
                site.staff.any { it.id == userFhirId }
            }
            isScreeningSiteEnabled = screeningSites.isNotEmpty()
            currentStep = 0
        }
    }

    var isReprescribing by mutableStateOf(false)
    var represcribingPrescription by mutableStateOf<PrescriptionAndMedicineRelation?>(null)

    fun getPreviousPrescription(
        patientId: String
    ) {
        viewModelScope.launch(ioDispatcher) {
            val allSites = screeningSiteRepository.getScreeningSites()
            val siteMap = allSites.associateBy { it.id }
            val appointmentIds = getInProgressCompletedAppointmentIds(patientId, appointmentRepository)
            val screenSiteAppointmentIds = Queries.getScreenSiteAppointmentIds(patientId, appointmentRepository)
            val allCombinedIds = (screenSiteAppointmentIds + appointmentIds).toTypedArray()

            previousPrescriptionList = prescriptionRepository.getLastPrescriptionAndMedicineByAppointmentId(*allCombinedIds).map { relation ->
                relation.copy(
                    prescriptionEntity = relation.prescriptionEntity.copy(
                        screeningSiteName = relation.prescriptionEntity.campaignId?.let { siteMap[it]?.name }
                    )
                )
            }
            todayPrescription = if (selectedCampaignId ==null) {
                previousPrescriptionList.firstOrNull { isToday(it.prescriptionEntity.prescriptionDate) && it.prescriptionEntity.campaignId==null }
            }else {
                todayPrescription =null
                selectedMedicationsList = emptyList()
                medicationsResponseWithMedicationList = emptyList()
                previousPrescriptionList.firstOrNull { record -> record.prescriptionEntity.campaignId != null && isCampaignActive(record.prescriptionEntity.campaignId) }

            }
            setTodayData()
        }
    }

    private suspend fun isCampaignActive(campaignId: String): Boolean {
        return screeningSiteRepository.getScreeningSiteById(campaignId)?.status == "active"
    }
    fun setTodayData() {
        todayPrescription?.let { prescription ->
            selectedMedicationsList =
                prescription.prescriptionDirectionAndMedicineView.map { it.medicationEntity.toMedicationResponse() }
            medicationsResponseWithMedicationList =
                prescription.prescriptionDirectionAndMedicineView.map {
                    MedicationResponseWithMedication(
                        medicationResponse = it.medicationEntity.toMedicationResponse(),
                        medication = Medication(
                            medReqUuid = it.prescriptionDirectionsEntity.id,
                            medReqFhirId = it.prescriptionDirectionsEntity.medReqFhirId,
                            doseForm = it.prescriptionDirectionsEntity.doseForm,
                            duration = it.prescriptionDirectionsEntity.duration,
                            frequency = it.prescriptionDirectionsEntity.frequency,
                            medFhirId = it.prescriptionDirectionsEntity.medFhirId,
                            note = it.prescriptionDirectionsEntity.note ?: "",
                            qtyPerDose = it.prescriptionDirectionsEntity.qtyPerDose,
                            qtyPrescribed = it.prescriptionDirectionsEntity.qtyPrescribed,
                            timing = it.prescriptionDirectionsEntity.timing,
                            brandName = it.prescriptionDirectionsEntity.brandName ?: "",
                            doseFormCode = it.prescriptionDirectionsEntity.doseFormCode
                        )
                    )
                }
        }
    }

    private var timingList: Deferred<List<MedicineTimingEntity>> =
        viewModelScope.async(ioDispatcher) {
            medicationRepository.getAllMedicationDirections()
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

    fun getMedications(medicationsList: (List<MedicationResponse>) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            medicationsList(
                medicationRepository.getAllMedication().map { it.toMedicationResponse() }
            )
        }
    }

    fun getAllMedicationDirections(medicationDirectionsList: (List<MedicineTimingEntity>) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            medicationDirectionsList(
                timingList.await()
            )
        }
    }

    fun insertPrescription(
        date: Date = Date(),
        inserted: (Long) -> Unit
    ) {
        val medicationsList = mutableListOf<Medication>()
        medicationsResponseWithMedicationList.forEach { medicationResponseWithMedication ->
            medicationsList.add(
                medicationResponseWithMedication.medication
            )
        }
        viewModelScope.launch(ioDispatcher) {
            appointmentResponseLocal = getAppointment(
                patientId = patient!!.id,
                hospitalCode = user.hospitalCode,
                campaignId = selectedCampaignId,
                appointmentRepository = appointmentRepository
            )
            var uuid = UUIDBuilder.generateUUID()
            var fhirId: String? = null
            todayPrescription?.let {
                if (isToday(it.prescriptionEntity.prescriptionDate)) {
                    uuid = it.prescriptionEntity.id
                    fhirId = it.prescriptionEntity.prescriptionFhirId
                }
                it.prescriptionDirectionAndMedicineView.forEach { directionsView ->
                    prescriptionRepository.deletePrescriptionDirectionEntity(directionsView.prescriptionDirectionsEntity)
                }
            }
            inserted(withContext(ioDispatcher) {
                insertPrescriptionInDB(
                    date = date,
                    prescriptionUuid = uuid,
                    prescriptionFhirId = fhirId,
                    medicationsList = medicationsList
                ).also {
                    insertGenericEntityInDB(
                        date = date,
                        prescriptionUuid = uuid,
                        prescriptionFhirId = fhirId,
                        medicationsList = medicationsList
                    )
                    if (selectedCampaignId==null) {
                        checkAndUpdateAppointmentStatusToInProgress(
                            inProgressTime = date,
                            patient = patient!!,
                            appointmentResponseLocal = appointmentResponseLocal!!,
                            appointmentRepository = appointmentRepository,
                            scheduleRepository = scheduleRepository,
                            genericRepository = genericRepository,
                            preferenceRepository = preferenceRepository
                        )
                    }
                    updatePatientLastUpdated(
                        patient!!.id,
                        patientLastUpdatedRepository,
                        genericRepository
                    )
                }
            })
        }
    }

    fun getPreviousSearch(previousSearches: (List<String>) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            previousSearches(
                searchRepository.getRecentActiveIngredientSearches()
            )
        }
    }

    fun insertRecentSearch(query: String, date: Date = Date(), inserted: (Long) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            inserted(
                searchRepository.insertRecentActiveIngredientSearch(query, date)
            )
        }
    }

    fun getActiveIngredientSearchList(
        activeIngredient: String,
        searchList: (List<MedicationResponse>) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            searchList(
                searchRepository.searchMedication(activeIngredient)
            )
        }
    }

    private suspend fun insertPrescriptionInDB(
        date: Date,
        prescriptionUuid: String,
        prescriptionFhirId: String?,
        medicationsList: List<Medication>
    ): Long {
        return prescriptionRepository.insertPrescription(
            PrescriptionResponseLocal(
                patientId = patient!!.id,
                patientFhirId = patient?.fhirId,
                generatedOn = date,
                prescriptionId = prescriptionUuid,
                prescriptionFhirId = prescriptionFhirId,
                prescription = medicationsList.map {
                    MedicationLocal(
                        medUnit = "",
                        medName = "",
                        doseForm = it.doseForm,
                        qtyPrescribed = it.qtyPrescribed,
                        frequency = it.frequency,
                        duration = it.duration,
                        medFhirId = it.medFhirId,
                        medReqUuid = it.medReqUuid,
                        medReqFhirId = it.medReqFhirId,
                        note = it.note,
                        qtyPerDose = it.qtyPerDose,
                        timing = it.timing,
                        brandName = it.brandName,
                        doseFormCode = it.doseFormCode
                    )
                },
                appointmentId = appointmentResponseLocal!!.uuid,
                campaignId = selectedCampaignId,
                screeningSiteName = selectedCampaignId?.let { id -> screeningSites.find { it.id == id }?.name }
            )
        )
    }

    private suspend fun insertGenericEntityInDB(
        date: Date,
        prescriptionUuid: String,
        prescriptionFhirId: String?,
        medicationsList: List<Medication>
    ): Long {
        val prescriptionResponse = PrescriptionResponse(
            patientFhirId = patient!!.fhirId ?: patient!!.id,
            generatedOn = date,
            prescriptionId = prescriptionUuid,
            prescription = medicationsList.map { medication ->
                medication.copy(
                    timing = timingList.await()
                        .find { timing -> timing.medicalDosage == medication.timing }?.medicalDosageId,
                    medReqFhirId = null,
                    doseFormCode = null,
                    brandName = medication.brandName?.ifBlank { null },
                    note = medication.note?.ifBlank { null }
                )
            },
            prescriptionFhirId = prescriptionFhirId,
            appointmentUuid = null,
            appointmentId = appointmentResponseLocal!!.appointmentId
                ?: appointmentResponseLocal!!.uuid,
            appUpdatedOn = Date(),
            campaignId = selectedCampaignId
        )
        return if (prescriptionFhirId == null) {
            genericRepository.insertPrescription(prescriptionResponse)
        } else {
            genericRepository.insertOrUpdatePrescriptionPut(
                prescriptionFhirId = prescriptionFhirId,
                prescriptionResponse = prescriptionResponse.copy(
                    prescriptionId = null
                )
            )
        }
    }

    fun addPatientToQueue(
        patient: PatientResponse,
        campaignId: String? = null,
        addedToQueue: (List<Long>) -> Unit,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            if (campaignId != null) {
                Queries.addPatientToCampaignQueue(
                    patient,
                    campaignId,
                    scheduleRepository,
                    genericRepository,
                    appointmentRepository,
                    addedToQueue
                )
            } else {
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
    }

    fun updateStatusToArrived(
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
}