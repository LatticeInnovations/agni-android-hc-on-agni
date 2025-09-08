package com.heartcare.agni.ui.vitalsscreen.addvitals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.repository.vital.VitalRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.vitals.UnitValue
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.ui.vitalsscreen.enums.BGEnum
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
class AddVitalsViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val vitalRepository: VitalRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val preferenceRepository: PreferenceRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    val user = preferenceRepository.getUserDetails()!!
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var appointmentResponseLocal by mutableStateOf<AppointmentResponseLocal?>(null)

    var todayVital by mutableStateOf<VitalResponse?>(null)

    var bgRandomChipSelected by mutableStateOf(true)
    var bgFastingChipSelected by mutableStateOf(false)
    var map = mapOf<String, Any>()
    var isButtonClicked by mutableStateOf(false)

    var bloodGlucose by mutableStateOf("")
    var bloodGlucoseError by mutableStateOf(false)
    val bloodGlucoseUnits = listOf("mg/dL", "mmol/L")
    var selectedBloodGlucoseUnit by mutableIntStateOf(0)

    var footExamination by mutableStateOf("")
    var eyeExamination by mutableStateOf("")

    var abdominalCircumference by mutableStateOf("")
    var abdominalCircumferenceError by mutableStateOf(false)
    val abdominalCircumferenceUnits = listOf("cm", "in")
    var selectedAbdominalCircumferenceUnit by mutableIntStateOf(0)

    var hipCircumference by mutableStateOf("")
    var hipCircumferenceError by mutableStateOf(false)
    val hipCircumferenceUnits = listOf("cm", "in")
    var selectedHipCircumferenceUnit by mutableIntStateOf(0)

    var hbA1c by mutableStateOf("")
    var hbA1cError by mutableStateOf(false)

    var serumCreatinine by mutableStateOf("")
    var serumCreatinineError by mutableStateOf(false)
    val serumCreatinineUnits = listOf("mg/dL", "µmol/L")
    var selectedSerumCreatinineUnit by mutableIntStateOf(0)

    var serumPotassium by mutableStateOf("")
    var serumPotassiumError by mutableStateOf(false)
    val serumPotassiumUnits = listOf("mEq/L", "µmol/L")
    var selectedSerumPotassiumUnit by mutableIntStateOf(0)

    var urineProtein by mutableStateOf("")
    var urineKetones by mutableStateOf("")
    var other by mutableStateOf("")

    fun isValid(): Boolean {
        return !(bloodGlucoseError
                || abdominalCircumferenceError
                || hipCircumferenceError
                || hbA1cError
                || serumCreatinineError
                || serumPotassiumError)
    }

    fun getTodayVital(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            val appointmentIds =
                getInProgressCompletedAppointmentIds(patientId, appointmentRepository)
            todayVital =
                vitalRepository.getLastVitalByAppointmentId(*appointmentIds.toTypedArray()).firstOrNull {
                    isToday(it.appUpdatedDate)
                }
            todayVital?.let { vital ->
                vital.bloodGlucose?.run {
                    selectedBloodGlucoseUnit = bloodGlucoseUnits.indexOf(unit)
                    bloodGlucose = if (selectedBloodGlucoseUnit == 0) value.toInt().toString()
                    else value.toString()
                    bgRandomChipSelected = type == BGEnum.RANDOM.value
                    bgFastingChipSelected = type == BGEnum.FASTING.value
                }
                footExamination = vital.footExamination.orEmpty()
                eyeExamination = vital.eyeExamination.orEmpty()
                vital.abdominalCircumference?.run {
                    abdominalCircumference = value.toString()
                    selectedAbdominalCircumferenceUnit = abdominalCircumferenceUnits.indexOf(unit)
                }
                vital.hipCircumference?.run {
                    hipCircumference = value.toString()
                    selectedHipCircumferenceUnit = hipCircumferenceUnits.indexOf(unit)
                }
                hbA1c = vital.hbA1cPercentage?.toString().orEmpty()
                vital.serumCreatinine?.run {
                    selectedSerumCreatinineUnit = serumCreatinineUnits.indexOf(unit)
                    serumCreatinine = if (selectedSerumCreatinineUnit == 0) value.toString()
                    else value.toInt().toString()
                }
                vital.serumPotassium?.run {
                    serumPotassium = value.toString()
                    selectedSerumPotassiumUnit = serumPotassiumUnits.indexOf(unit)
                }
                urineProtein = vital.urineProtein.orEmpty()
                urineKetones = vital.urineKetones.orEmpty()
                other = vital.others.orEmpty()
            }
        }
    }

    private fun getVitalResponse(
        vitalUuid: String = UUIDBuilder.generateUUID(),
        fhirId: String? = null,
        appUpdatedDate: Date = Date()
    ): VitalResponse {
        return VitalResponse(
            uuid = vitalUuid,
            fhirId = fhirId,
            appointmentId = appointmentResponseLocal!!.appointmentId
                ?: appointmentResponseLocal!!.uuid,
            patientId = patient!!.fhirId ?: patient!!.id,
            practitionerName = null,
            appUpdatedDate = appUpdatedDate,
            bloodGlucose = if (bloodGlucose.isBlank()) null else
                UnitValue(
                    unit = bloodGlucoseUnits[selectedBloodGlucoseUnit],
                    value = bloodGlucose.toDouble(),
                    type = getGlucoseType()
                ),
            footExamination = footExamination.ifBlank { null },
            eyeExamination = eyeExamination.ifBlank { null },
            abdominalCircumference = if (abdominalCircumference.isBlank()) null else
                UnitValue(
                    unit = abdominalCircumferenceUnits[selectedAbdominalCircumferenceUnit],
                    value = abdominalCircumference.toDouble(),
                    type = null
                ),
            hipCircumference = if (hipCircumference.isBlank()) null else
                UnitValue(
                    unit = hipCircumferenceUnits[selectedHipCircumferenceUnit],
                    value = hipCircumference.toDouble(),
                    type = null
                ),
            hbA1cPercentage = hbA1c.ifBlank { null }?.toDouble(),
            serumCreatinine = if (serumCreatinine.isBlank()) null else
                UnitValue(
                    unit = serumCreatinineUnits[selectedSerumCreatinineUnit],
                    value = serumCreatinine.toDouble(),
                    type = null
                ),
            serumPotassium = if (serumPotassium.isBlank()) null else
                UnitValue(
                    unit = serumPotassiumUnits[selectedSerumPotassiumUnit],
                    value = serumPotassium.toDouble(),
                    type = null
                ),
            urineProtein = urineProtein.ifBlank { null },
            urineKetones = urineKetones.ifBlank { null },
            others = other.ifBlank { null }
        )
    }

    fun insertVital(
        inserted: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            appointmentResponseLocal = getAppointment(
                patientId = patient!!.id,
                hospitalCode = user.hospitalCode,
                appointmentRepository = appointmentRepository
            )
            var uuid = UUIDBuilder.generateUUID()
            var fhirId: String? = null
            todayVital?.let {
                uuid = it.uuid
                fhirId = it.fhirId
            }
            val vitalResponse = getVitalResponse(uuid)
            vitalRepository.insertVital(
                vitalResponse.copy(
                    appointmentId = appointmentResponseLocal!!.uuid,
                    patientId = patient!!.id,
                    practitionerName = getFullName(
                        user.firstName,
                        user.lastName
                    ),
                    fhirId = fhirId
                )
            ).also {
                genericRepository.insertVital(vitalResponse)
                checkAndUpdateAppointmentStatusToInProgress(
                    inProgressTime = vitalResponse.appUpdatedDate,
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
            inserted()
        }
    }

    private fun getGlucoseType(): String? {
        return if (bloodGlucose.isNotBlank() && bgRandomChipSelected) BGEnum.RANDOM.value else if (bloodGlucose.isNotBlank() && bgFastingChipSelected) BGEnum.FASTING.value else null
    }
}