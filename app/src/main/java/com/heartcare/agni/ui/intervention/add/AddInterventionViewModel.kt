package com.heartcare.agni.ui.intervention.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.server.model.patient.PatientResponse

class AddInterventionViewModel: BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var listOfInterventions by mutableStateOf(listOf(
        "ABC0022 Education - Hypertension",
        "ABC002 Counselling - Diabetes diet",
        "ABC001 Education - Hypertension control",
        "EDU007 Education - High cardiovascular risk",
        "EDU006 Education - Hypercholesterolaemia control",
        "EDU005 Education - Diabetes control (insulin injection)",
        "EDU004 Education - Diabetes control (advanced)",
        "EDU003 Education - Diabetes control (basic)",
        "EDU002 Education - Hypertension control (advanced)",
        "EDU001 Education - Hypertension control (basic)",
        "CSL008 Counselling - weight control",
        "CSL007 Counselling - physical activity",
        "CSL006 Counselling - fruits and vegetables"
    ))

    var selectedInterventionList by mutableStateOf(listOf<String>())

    var bottomNavExpanded by mutableStateOf(false)
    var clearAllConfirmDialog by mutableStateOf(false)
}