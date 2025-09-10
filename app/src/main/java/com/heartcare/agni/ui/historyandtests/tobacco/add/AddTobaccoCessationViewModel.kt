package com.heartcare.agni.ui.historyandtests.tobacco.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.Pharmacotherapy.Companion.pharmacotherapyCodeFromDisplay
import com.heartcare.agni.data.local.enums.Pharmacotherapy.Companion.pharmacotherapyDisplayFromCode
import com.heartcare.agni.data.local.enums.QuitPlan
import com.heartcare.agni.data.local.enums.QuitPlan.Companion.quitPlanCodeFromDisplay
import com.heartcare.agni.data.local.enums.QuitPlan.Companion.quitPlanDisplayFromCode
import com.heartcare.agni.data.local.enums.StatusOfPlan.Companion.statusOfPlanCodeFromDisplay
import com.heartcare.agni.data.local.enums.StatusOfPlan.Companion.statusOfPlanDisplayFromCode
import com.heartcare.agni.data.local.enums.TobaccoUsage
import com.heartcare.agni.data.local.enums.TobaccoUsage.Companion.tobaccoUsageCodeFromDisplay
import com.heartcare.agni.data.local.enums.TobaccoUsage.Companion.tobaccoUsageDisplayFromCode
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.data.local.enums.YesNoEnum.Companion.booleanFromDisplay
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.repository.tobacco.TobaccoCessationRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.tobacco.TobaccoCessationResponse
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
class AddTobaccoCessationViewModel @Inject constructor(
    private val tobaccoCessationRepository: TobaccoCessationRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    val user = preferenceRepository.getUserDetails()!!
    var appointmentResponseLocal by mutableStateOf<AppointmentResponseLocal?>(null)
    var patient by mutableStateOf<PatientResponse?>(null)
    var isLaunched by mutableStateOf(false)

    var todayTobaccoCessation by mutableStateOf<TobaccoCessationResponse?>(null)

    var tobaccoUse by mutableStateOf("")
    var briefAdvice by mutableStateOf("")
    var assessedStatus by mutableStateOf("")
    var assistQuit by mutableStateOf("")
    var pharmacotherapy by mutableStateOf("")
    var dateOfPlan by mutableStateOf(Date())
    var showDatePicker by mutableStateOf(false)
    var planStatus by mutableStateOf("")

    fun resetBriefAdviceQuestions() {
        briefAdvice = ""
        resetAssessedStatusQuestions()
    }

    fun resetAssessedStatusQuestions() {
        assessedStatus = ""
        resetAssistToQuitQuestions()
    }

    fun resetAssistToQuitQuestions() {
        assistQuit = ""
        resetQuitPlanQuestions()
    }

    fun resetQuitPlanQuestions() {
        pharmacotherapy = ""
        planStatus = ""
        dateOfPlan = Date()
    }

    fun isValid(): Boolean {
        return when (tobaccoUse) {
            TobaccoUsage.NO_I_DO_NOT_USE_TOBACCO.display -> true
            else -> {
                if (tobaccoUse.isBlank()) false
                else {
                    briefAdvice.isNotBlank() && (
                            when (assessedStatus) {
                                YesNoEnum.YES.display -> {
                                    when (assistQuit) {
                                        QuitPlan.NO.display, QuitPlan.NO_REFER_TO_INTENSIVE_COUNSELLING.display -> true

                                        QuitPlan.YES_BRIEF_QUIT_PLAN.display -> {
                                            planStatus.isNotBlank()
                                        }

                                        QuitPlan.YES_INTENSIVE_QUIT_PLAN.display -> {
                                            planStatus.isNotBlank() && pharmacotherapy.isNotBlank()
                                        }

                                        else -> false
                                    }
                                }

                                else -> assessedStatus.isNotBlank()
                            }
                            )
                }
            }
        }
    }

    fun getTodayTobaccoCessation(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            val appointmentIds =
                getInProgressCompletedAppointmentIds(patientId, appointmentRepository)
            todayTobaccoCessation =
                tobaccoCessationRepository.getTobaccoCessationRecordsByAppointmentIds(*appointmentIds.toTypedArray()).firstOrNull {
                    isToday(it.appUpdatedDate)
                }
            todayTobaccoCessation?.let { tobaccoCessation ->
                tobaccoUse = tobaccoUsageDisplayFromCode(tobaccoCessation.tobaccoUse ?: "") ?: ""
                briefAdvice = when (tobaccoCessation.briefAdvice) {
                    null -> ""
                    true -> YesNoEnum.YES.display
                    false -> YesNoEnum.NO.display
                }
                assessedStatus = when (tobaccoCessation.assessedStatus) {
                    null -> ""
                    true -> YesNoEnum.YES.display
                    false -> YesNoEnum.NO.display
                }
                assistQuit = quitPlanDisplayFromCode(tobaccoCessation.assistQuit ?: "") ?: ""
                pharmacotherapy =
                    pharmacotherapyDisplayFromCode(tobaccoCessation.pharmacotherapy ?: "") ?: ""
                dateOfPlan = tobaccoCessation.dateOfPlan ?: Date()
                planStatus = statusOfPlanDisplayFromCode(tobaccoCessation.planStatus ?: "") ?: ""
            }
        }
    }

    private fun getTobaccoCessationResponse(
        uuid: String = UUIDBuilder.generateUUID(),
        fhirId: String? = null,
        appUpdatedDate: Date = Date()
    ): TobaccoCessationResponse {
        return TobaccoCessationResponse(
            uuid = uuid,
            fhirId = fhirId,
            appointmentId = appointmentResponseLocal!!.appointmentId
                ?: appointmentResponseLocal!!.uuid,
            patientId = patient!!.fhirId ?: patient!!.id,
            practitionerId = null,
            practitionerName = null,
            appUpdatedDate = appUpdatedDate,
            tobaccoUse = tobaccoUsageCodeFromDisplay(tobaccoUse),
            briefAdvice = booleanFromDisplay(briefAdvice),
            assessedStatus = booleanFromDisplay(assessedStatus),
            assistQuit = quitPlanCodeFromDisplay(assistQuit),
            dateOfPlan = if (assistQuit == QuitPlan.YES_INTENSIVE_QUIT_PLAN.display || assistQuit == QuitPlan.YES_BRIEF_QUIT_PLAN.display) dateOfPlan else null,
            pharmacotherapy = pharmacotherapyCodeFromDisplay(pharmacotherapy),
            planStatus = statusOfPlanCodeFromDisplay(planStatus)
        )
    }

    fun addTobaccoCessation(
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
            todayTobaccoCessation?.let {
                uuid = it.uuid
                fhirId = it.fhirId
            }
            val tobaccoCessationResponse = getTobaccoCessationResponse(uuid)
            tobaccoCessationRepository.insertTobaccoCessation(
                tobaccoCessationResponse.copy(
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
            genericRepository.insertTobaccoCessationRecord(tobaccoCessationResponse)
            checkAndUpdateAppointmentStatusToInProgress(
                inProgressTime = tobaccoCessationResponse.appUpdatedDate,
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