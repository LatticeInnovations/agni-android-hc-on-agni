package com.heartcare.agni.ui.patienteditscreen.address

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.heartcare.agni.data.local.enums.LevelsEnum
import com.heartcare.agni.data.local.enums.LocationStateEnum
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.levels.LevelRepository
import com.heartcare.agni.data.local.repository.patient.PatientRepository
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditPatientAddressViewModel @Inject constructor(
    private val application: Application,
    val patientRepository: PatientRepository,
    val genericRepository: GenericRepository,
    private val levelRepository: LevelRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AndroidViewModel(application), DefaultLifecycleObserver {
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

    var latitude: Double? by mutableStateOf(null)
    var longitude: Double? by mutableStateOf(null)

    var provinceTemp: LevelResponse? by mutableStateOf(null)
    var areaCouncilTemp: LevelResponse? by mutableStateOf(null)
    var islandTemp: LevelResponse? by mutableStateOf(null)
    var villageTemp: LevelResponse? by mutableStateOf(null)
    var otherVillageTemp by mutableStateOf("")
    var postalCodeTemp by mutableStateOf("")
    var latitudeTemp: Double? by mutableStateOf(null)
    var longitudeTemp: Double? by mutableStateOf(null)

    var stepState by mutableStateOf(LocationStateEnum.TODO)
    var openPermissionDialog by mutableStateOf(false)

    fun getLists() {
        viewModelScope.launch(ioDispatcher) {
            provinceList =
                levelRepository.getLevels(levelType = LevelsEnum.PROVINCE.levelType)
            getAreaCouncilList()
            getIslandList()
            getVillageList()
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

    fun getVillageList() {
        viewModelScope.launch(ioDispatcher) {
            villageList = levelRepository.getLevels(
                levelType = LevelsEnum.VILLAGE.levelType
            ).filter { it.precedingLevelId == island?.fhirId || it.precedingLevelId == null }
        }
    }

    suspend fun getLevelByFhirId(fhirId: String): LevelResponse {
        return levelRepository.getLevelByFhirId(fhirId)
    }

    fun addressInfoValidation(): Boolean {
        if (province == null || areaCouncil == null || island == null) return false

        if (village?.name == otherName) {
            return otherVillage.isNotBlank()
        }

        return true
    }

    fun checkIsEdit(): Boolean {
        return postalCode != postalCodeTemp ||
                province != provinceTemp ||
                areaCouncil != areaCouncilTemp ||
                island != islandTemp ||
                village != villageTemp ||
                otherVillage != otherVillageTemp ||
                latitude != latitudeTemp ||
                longitude != longitudeTemp
    }


    fun revertChanges(): Boolean {
        postalCode = postalCodeTemp
        province = provinceTemp
        areaCouncil = areaCouncilTemp
        island = islandTemp
        village = villageTemp
        otherVillage = otherVillageTemp
        otherVillageError = false
        isVillageOtherSelected = otherVillage.isNotBlank()
        latitude = latitudeTemp
        longitude = longitudeTemp
        if (longitude != null && latitude != null) {
            stepState = LocationStateEnum.SAVED
        } else {
            stepState = LocationStateEnum.TODO
        }
        return true
    }

    fun updateAddressInfo(patientResponse: PatientResponse) {
        viewModelScope.launch(ioDispatcher) {
            val response = patientRepository.updatePatientData(patientResponse = patientResponse)
            if (checkIsEdit() && response > 0) {
                if (patientResponse.fhirId != null) {
                    genericRepository.insertOrUpdatePatientPatchEntity(
                        patientFhirId = patientResponse.fhirId,
                        patientResponse = patientResponse
                    )
                } else {
                    genericRepository.insertPatient(
                        patientResponse
                    )
                }
            }
        }
    }

    fun fetchLocation() {
        stepState = LocationStateEnum.LOADING

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

        if (ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                application,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            location?.let {
                latitude = it.latitude
                longitude = it.longitude
                Timber.d("Latitude: $latitude Longitude: $longitude")
                stepState = LocationStateEnum.SAVED
            }
        }.addOnFailureListener {
            Timber.e(it, "Failed to fetch location")
            stepState = LocationStateEnum.TODO
        }
    }
}

