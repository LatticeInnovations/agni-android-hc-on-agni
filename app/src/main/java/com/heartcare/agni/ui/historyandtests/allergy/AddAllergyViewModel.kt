package com.heartcare.agni.ui.historyandtests.allergy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.allergy.AllergyRepository
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.server.model.allergy.AllergyResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.heartcare.agni.utils.common.Queries.getAppointment
import com.heartcare.agni.utils.common.Queries.getInProgressCompletedAppointmentIds
import com.heartcare.agni.utils.common.Queries.updatePatientLastUpdated
import com.heartcare.agni.utils.converters.responseconverter.NameConverter.getFullName
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddAllergyViewModel @Inject constructor(
    private val allergyRepository: AllergyRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    val user = preferenceRepository.getUserDetails()!!
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var appointmentResponseLocal by mutableStateOf<AppointmentResponseLocal?>(null)

    val maxAllergiesLength = 500

    var allergy by mutableStateOf("")
    var todayAllergy by mutableStateOf<AllergyResponse?>(null)

    fun getTodayAllergy(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            val appointmentIds =
                getInProgressCompletedAppointmentIds(patientId, appointmentRepository)
            todayAllergy =
                allergyRepository.getAllergyRecordsByAppointmentIds(*appointmentIds.toTypedArray()).firstOrNull {
                    isToday(it.appUpdatedDate)
                }
            allergy = todayAllergy?.allergy ?: ""
        }
    }

    private fun getAllergyResponse(
        uuid: String = UUIDBuilder.generateUUID(),
        fhirId: String? = null,
        appUpdatedDate: Date = Date()
    ): AllergyResponse {
        return AllergyResponse(
            uuid = uuid,
            fhirId = fhirId,
            appointmentId = appointmentResponseLocal!!.appointmentId
                ?: appointmentResponseLocal!!.uuid,
            patientId = patient!!.fhirId ?: patient!!.id,
            practitionerId = null,
            practitionerName = null,
            appUpdatedDate = appUpdatedDate,
            allergy = allergy.trim().ifBlank { null },
        )
    }

    fun addAllergy(
        added: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            appointmentResponseLocal = getAppointment(
                patientId = patient!!.id,
                hospitalCode = user.hospitalCode,
                appointmentRepository = appointmentRepository
            )
            var uuid = UUIDBuilder.generateUUID()
            var fhirId: String? = null
            todayAllergy?.let {
                uuid = it.uuid
                fhirId = it.fhirId
            }
            val allergyResponse = getAllergyResponse(uuid)
            allergyRepository.insertAllergy(
                allergyResponse.copy(
                    appointmentId = appointmentResponseLocal!!.uuid,
                    patientId = patient!!.id,
                    practitionerName = getFullName(
                        user.firstName,
                        user.lastName
                    ),
                    practitionerId = user.fhirId,
                    fhirId = fhirId
                )
            )
            genericRepository.insertAllergyRecord(allergyResponse)
            checkAndUpdateAppointmentStatusToInProgress(
                inProgressTime = allergyResponse.appUpdatedDate,
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
            added()
        }
    }
}