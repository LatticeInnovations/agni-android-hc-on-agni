package com.heartcare.agni.ui.historyandtests.tobacco.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.QuitPlan
import com.heartcare.agni.data.server.model.patient.PatientResponse
import java.util.Date

class AddTobaccoCessationViewModel: BaseViewModel() {
    var patient by mutableStateOf<PatientResponse?>(null)
    var isLaunched by mutableStateOf(false)

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
        return !((assistQuit == QuitPlan.YES_BRIEF_QUIT_PLAN.display
                || assistQuit == QuitPlan.YES_INTENSIVE_QUIT_PLAN.display) && planStatus.isBlank())
    }
}