package com.heartcare.agni.ui.patientregistration.step3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.LevelsEnum
import com.heartcare.agni.data.local.repository.levels.LevelRepository
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientRegistrationStepThreeViewModel @Inject constructor(
    private val levelRepository: LevelRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)
    val otherName = "Others"
    val maxLength = 50
    val postalCodeLength = 10

    var province: LevelResponse? by mutableStateOf(null)
    var provinceList: List<LevelResponse> by mutableStateOf(emptyList())

    var areaCouncil: LevelResponse? by mutableStateOf(null)
    var areaCouncilList: List<LevelResponse> by mutableStateOf(emptyList())

    var island: LevelResponse? by mutableStateOf(null)
    var islandList: List<LevelResponse> by mutableStateOf(emptyList())

    var village: LevelResponse? by mutableStateOf(null)
    var villageList: List<LevelResponse> by mutableStateOf(emptyList())
    var isVillageOtherSelected by mutableStateOf(false)
    var otherVillage by mutableStateOf("")
    var otherVillageError by mutableStateOf(false)

    var postalCode by mutableStateOf("")

    init {
        viewModelScope.launch(ioDispatcher) {
            provinceList =
                levelRepository.getLevels(levelType = LevelsEnum.PROVINCE.levelType)
        }
    }

    fun getAreaCouncilList(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            areaCouncilList = levelRepository.getLevels(
                levelType = LevelsEnum.AREA_COUNCIL.levelType,
                precedingId = province!!.fhirId
            )
        }
    }

    fun getIslandList(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            islandList = levelRepository.getLevels(
                levelType = LevelsEnum.ISLAND.levelType,
                precedingId = areaCouncil!!.fhirId
            )
        }
    }

    fun getVillageList(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            villageList = levelRepository.getLevels(
                levelType = LevelsEnum.VILLAGE.levelType
            ).filter { it.precedingLevelId == island?.fhirId || it.precedingLevelId == null }
        }
    }

    fun addressInfoValidation(): Boolean {
        if (province == null || areaCouncil == null || island == null) return false

        if (village?.name == otherName) {
            return otherVillage.isNotBlank()
        }

        return true
    }
}

class Address {
    var pincode by mutableStateOf("")
    var state by mutableStateOf("")
    var addressLine1 by mutableStateOf("")
    var addressLine2 by mutableStateOf("")
    var city by mutableStateOf("")
    var district by mutableStateOf("")
    var isPostalCodeValid by mutableStateOf(false)
    var isAddressLine1Valid by mutableStateOf(false)
    var isCityValid by mutableStateOf(false)
    var isStateValid by mutableStateOf(false)
}