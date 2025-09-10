package com.heartcare.agni.ui.patientregistration.preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.identifier.IdentifierRepository
import com.heartcare.agni.data.local.repository.patient.PatientRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.data.server.model.patient.PatientIdentifier
import com.heartcare.agni.data.server.model.patient.PatientLastUpdatedResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.builders.UUIDBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PatientRegistrationPreviewViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val genericRepository: GenericRepository,
    private val identifierRepository: IdentifierRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patientResponse by mutableStateOf<PatientResponse?>(null)

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var email by mutableStateOf("")
    var dob by mutableStateOf("")
    var dobDay by mutableStateOf("")
    var dobMonth by mutableStateOf("")
    var dobYear by mutableStateOf("")
    var years by mutableStateOf("")
    var months by mutableStateOf("")
    var days by mutableStateOf("")
    var gender by mutableStateOf("")
    var isPersonDeceased by mutableIntStateOf(0)
    var selectedDeceasedReason by mutableStateOf("")

    var motherName by mutableStateOf("")
    var fatherName by mutableStateOf("")
    var spouseName by mutableStateOf("")

    var hospitalId by mutableStateOf("")
    var nationalId by mutableStateOf("")
    var isNationalIdVerified by mutableStateOf(false)

    var openDialog by mutableStateOf(false)
    val identifierList = mutableListOf<PatientIdentifier>()

    var relativeId by mutableStateOf(UUIDBuilder.generateUUID())

    var province: LevelResponse? by mutableStateOf(null)
    var areaCouncil: LevelResponse? by mutableStateOf(null)
    var island: LevelResponse? by mutableStateOf(null)
    var village: LevelResponse? by mutableStateOf(null)
    var postalCode by mutableStateOf("")
    var otherVillage by mutableStateOf("")

    fun addPatient(patientResponse: PatientResponse) {
        viewModelScope.launch(ioDispatcher) {
            patientRepository.addPatient(patientResponse)
            genericRepository.insertPatient(
                patientResponse
            )
            identifierRepository.insertIdentifierList(patientResponse)

            val patientLastUpdatedResponse = PatientLastUpdatedResponse(
                uuid = patientResponse.id,
                timestamp = Date()
            )
            patientLastUpdatedRepository.insertPatientLastUpdatedData(patientLastUpdatedResponse)
            genericRepository.insertPatientLastUpdated(patientLastUpdatedResponse)
        }
    }
}