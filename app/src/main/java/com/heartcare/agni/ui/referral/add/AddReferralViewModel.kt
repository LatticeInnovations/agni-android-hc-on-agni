package com.heartcare.agni.ui.referral.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.healthfacility.HealthFacilityRepository
import com.heartcare.agni.data.local.repository.levels.LevelRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.referral.ReferralRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.referral.ReferralResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.heartcare.agni.utils.common.Queries.getInProgressCompletedAppointmentIds
import com.heartcare.agni.utils.common.Queries.updatePatientLastUpdated
import com.heartcare.agni.utils.converters.responseconverter.NameConverter.getFullName
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isToday
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddReferralViewModel @Inject constructor(
    private val referralRepository: ReferralRepository,
    private val healthFacilityRepository: HealthFacilityRepository,
    private val levelRepository: LevelRepository,
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

    var province: LevelResponse? by mutableStateOf(null)
    var provinceList: List<LevelResponse> by mutableStateOf(emptyList())

    var areaCouncil: LevelResponse? by mutableStateOf(null)
    var areaCouncilList: List<LevelResponse> by mutableStateOf(emptyList())
    private var masterAreaCouncilList: List<LevelResponse> by mutableStateOf(emptyList())

    var island: LevelResponse? by mutableStateOf(null)
    var islandList: List<LevelResponse> by mutableStateOf(emptyList())
    private var masterIslandList: List<LevelResponse> by mutableStateOf(emptyList())

    var heathFacility: LevelResponse? by mutableStateOf(null)
    var heathFacilityList: List<LevelResponse> by mutableStateOf(emptyList())
    private var masterHealthFacilityList: List<LevelResponse> by mutableStateOf(emptyList())

    var note by mutableStateOf("")

    var todayReferral by mutableStateOf<ReferralResponse?>(null)

    fun getListAndRecords(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            masterHealthFacilityList = healthFacilityRepository.getHealthFacilityInLevelResponse()
            masterIslandList =
                levelRepository.getLevelListByFhirIds(*masterHealthFacilityList.map { it.precedingLevelId!! }
                    .toTypedArray())
            masterAreaCouncilList =
                levelRepository.getLevelListByFhirIds(*masterIslandList.map { it.precedingLevelId!! }
                    .toTypedArray())
            provinceList =
                levelRepository.getLevelListByFhirIds(*masterAreaCouncilList.map { it.precedingLevelId!! }
                    .toTypedArray())
            val appointmentIds =
                getInProgressCompletedAppointmentIds(patientId, appointmentRepository)
            todayReferral = referralRepository.getReferralRecordsByAppointmentIds(*appointmentIds.toTypedArray()).firstOrNull {
                isToday(it.appUpdatedDate)
            }
            todayReferral?.let { referral ->
                heathFacility =
                    masterHealthFacilityList.first { it.fhirId == referral.healthFacilityId }
                island = masterIslandList.first { it.fhirId == heathFacility!!.precedingLevelId }
                areaCouncil = masterAreaCouncilList.first { it.fhirId == island!!.precedingLevelId }
                province = provinceList.first { it.fhirId == areaCouncil!!.precedingLevelId }
                note = referral.note ?: ""
                getAreaCouncilList()
                getIslandList()
                getHealthFacilityList()
            }
        }
    }

    fun getAreaCouncilList() {
        areaCouncilList =
            masterAreaCouncilList.filter { it.precedingLevelId == province!!.fhirId }
    }

    fun getIslandList() {
        islandList = masterIslandList.filter { it.precedingLevelId == areaCouncil!!.fhirId }
    }

    fun getHealthFacilityList() {
        heathFacilityList =
            masterHealthFacilityList.filter { it.precedingLevelId == island!!.fhirId }
    }

    fun isValid(): Boolean {
        return province != null &&
                areaCouncil != null &&
                island != null &&
                heathFacility != null
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

    private fun getReferralResponse(
        referralUuid: String = UUIDBuilder.generateUUID(),
        referralFhirId: String? = null
    ): ReferralResponse {
        return ReferralResponse(
            uuid = referralUuid,
            fhirId = referralFhirId,
            appointmentId = appointmentResponseLocal!!.appointmentId
                ?: appointmentResponseLocal!!.uuid,
            patientId = patient!!.fhirId ?: patient!!.id,
            practitionerId = null,
            practitionerName = null,
            appUpdatedDate = Date(),
            healthFacilityId = heathFacility!!.fhirId,
            note = note.trim().ifBlank { null },
            sourceHealthFacilityId = null,
            sourceIslandId = null
        )
    }

    fun addReferral(
        added: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            getAppointment()
            var uuid = UUIDBuilder.generateUUID()
            var fhirId: String? = null
            todayReferral?.let {
                uuid = it.uuid
                fhirId = it.fhirId
            }
            val referralResponse = getReferralResponse(uuid)
            val sourceHealthFacility = healthFacilityRepository.getHealthFacilityList().first { it.heartcareId == user.hospitalId.toString() }
            referralRepository.insertReferral(
                referralResponse.copy(
                    appointmentId = appointmentResponseLocal!!.uuid,
                    patientId = patient!!.id,
                    practitionerName = getFullName(
                        user.firstName,
                        user.lastName
                    ),
                    practitionerId = user.fhirId,
                    fhirId = fhirId,
                    sourceHealthFacilityId = sourceHealthFacility.healthFacilityId,
                    sourceIslandId = sourceHealthFacility.islandId
                )
            )
            genericRepository.insertReferralRecord(referralResponse)
            checkAndUpdateAppointmentStatusToInProgress(
                inProgressTime = referralResponse.appUpdatedDate,
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