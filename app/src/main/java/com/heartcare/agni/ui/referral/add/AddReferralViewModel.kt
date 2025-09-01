package com.heartcare.agni.ui.referral.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.LevelsEnum
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.levels.LevelRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddReferralViewModel @Inject constructor(
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

    var island: LevelResponse? by mutableStateOf(null)
    var islandList: List<LevelResponse> by mutableStateOf(emptyList())

    var heathFacility by mutableStateOf("")
    var heathFacilityList: List<String> by mutableStateOf(emptyList())

    var note by mutableStateOf("")

    init {
        viewModelScope.launch(ioDispatcher) {
            provinceList =
                levelRepository.getLevels(levelType = LevelsEnum.PROVINCE.levelType)
        }
    }

    fun getAreaCouncilList() {
        viewModelScope.launch(ioDispatcher) {
            areaCouncilList = levelRepository.getLevels(
                levelType = LevelsEnum.AREA_COUNCIL.levelType,
                precedingId = province!!.fhirId
            )
        }
    }

    fun getIslandList() {
        viewModelScope.launch(ioDispatcher) {
            islandList = levelRepository.getLevels(
                levelType = LevelsEnum.ISLAND.levelType,
                precedingId = areaCouncil!!.fhirId
            )
        }
    }

    fun getHealthFacilityList() {
        heathFacilityList = listOf("abc", "efg", "hij")
    }

    fun isValid(): Boolean {
        return province != null &&
                areaCouncil != null &&
                island != null &&
                heathFacility.isNotBlank()
    }
}