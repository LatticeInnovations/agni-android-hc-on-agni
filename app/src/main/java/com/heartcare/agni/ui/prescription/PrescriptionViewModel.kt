package com.heartcare.agni.ui.prescription

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.enums.DispenseStatusEnum
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.model.prescription.MedicationLocal
import com.heartcare.agni.data.local.model.prescription.PrescriptionResponseLocal
import com.heartcare.agni.data.local.model.prescription.medication.MedicationResponseWithMedication
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.dispense.DispenseRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.medication.MedicationRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.prescription.PrescriptionRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.repository.search.SearchRepository
import com.heartcare.agni.data.local.roomdb.entities.dispense.DispensePrescriptionEntity
import com.heartcare.agni.data.local.roomdb.entities.medication.MedicineTimingEntity
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.prescription.medication.MedicationResponse
import com.heartcare.agni.data.server.model.prescription.prescriptionresponse.Medication
import com.heartcare.agni.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.common.Queries
import com.heartcare.agni.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.heartcare.agni.utils.common.Queries.updatePatientLastUpdated
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isToday
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
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
    private val dispenseRepository: DispenseRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    private val preferenceRepository: PreferenceRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
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

    internal var appointmentResponseLocal: AppointmentResponseLocal? = null

    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var canAddAssessment by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250
    var showAppointmentCompletedDialog by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)
    var existsInOtherHospital by mutableStateOf(false)

    var isReprescribing by mutableStateOf(false)
    var represcribingPrescription by mutableStateOf<PrescriptionAndMedicineRelation?>(null)

    internal fun getPreviousPrescription(
        patientId: String
    ) {
        viewModelScope.launch(ioDispatcher) {
            previousPrescriptionList =
                prescriptionRepository.getLastPrescriptionAndMedicine(patientId)
            todayPrescription =
                previousPrescriptionList.firstOrNull { isToday(it.prescriptionEntity.prescriptionDate) }
            todayPrescription?.let { prescription ->
                selectedMedicationsList =
                    prescription.prescriptionDirectionAndMedicineView.map { it.medicationEntity.toMedicationResponse() }
                medicationsResponseWithMedicationList =
                    prescription.prescriptionDirectionAndMedicineView.map {
                        MedicationResponseWithMedication(
                            activeIngredient = it.medicationEntity.activeIngredient,
                            medName = it.medicationEntity.medName,
                            medUnit = it.medicationEntity.medUnit,
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
    }

    private var timingList: Deferred<List<MedicineTimingEntity>> =
        viewModelScope.async(ioDispatcher) {
            medicationRepository.getAllMedicationDirections()
        }

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

    internal fun getMedications(medicationsList: (List<MedicationResponse>) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            medicationsList(
                medicationRepository.getAllMedication().map { it.toMedicationResponse() }
            )
        }
    }

    internal fun getAllMedicationDirections(medicationDirectionsList: (List<MedicineTimingEntity>) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            medicationDirectionsList(
                timingList.await()
            )
        }
    }

    private suspend fun getAppointment() {
        appointmentResponseLocal =
            appointmentRepository.getAppointmentListByDate(
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).firstOrNull { appointmentEntity ->
                appointmentEntity.patientId == patient!!.id && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
                        && user.hospitalCode == appointmentEntity.hospitalCode
            }
    }

    internal fun insertPrescription(
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
            getAppointment()
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
                insertPrescriptionInDB(date, uuid, fhirId, medicationsList).also {
                    insertGenericEntityInDB(date, uuid, fhirId,  medicationsList)
                    dispenseRepository.insertPrescriptionDispenseData(
                        DispensePrescriptionEntity(
                            patientId = patient!!.id,
                            prescriptionId = uuid,
                            status = DispenseStatusEnum.NOT_DISPENSED.code
                        )
                    )
                    checkAndUpdateAppointmentStatusToInProgress(
                        inProgressTime = date,
                        patient = patient!!,
                        appointmentResponseLocal = appointmentResponseLocal!!,
                        appointmentRepository = appointmentRepository,
                        scheduleRepository = scheduleRepository,
                        genericRepository = genericRepository,
                        preferenceRepository = preferenceRepository
                    )
                    updatePatientLastUpdated(
                        patient!!.id,
                        patientLastUpdatedRepository,
                        genericRepository
                    )
                }
            })
        }
    }

    internal fun getPreviousSearch(previousSearches: (List<String>) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            previousSearches(
                searchRepository.getRecentActiveIngredientSearches()
            )
        }
    }

    internal fun insertRecentSearch(query: String, date: Date = Date(), inserted: (Long) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            inserted(
                searchRepository.insertRecentActiveIngredientSearch(query, date)
            )
        }
    }

    internal fun getActiveIngredientSearchList(
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
        prescriptionFhirId: String? = null,
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
                appointmentId = appointmentResponseLocal!!.uuid
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
            appUpdatedOn = Date()
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
}