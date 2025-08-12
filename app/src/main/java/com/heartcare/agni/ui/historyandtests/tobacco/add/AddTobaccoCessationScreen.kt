package com.heartcare.agni.ui.historyandtests.tobacco.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.Pharmacotherapy.Companion.pharmacotherapyList
import com.heartcare.agni.data.local.enums.QuitPlan
import com.heartcare.agni.data.local.enums.QuitPlan.Companion.quitPlanList
import com.heartcare.agni.data.local.enums.StatusOfPlan.Companion.statusOfPlanList
import com.heartcare.agni.data.local.enums.TobaccoUsage
import com.heartcare.agni.data.local.enums.TobaccoUsage.Companion.tobaccoUsageList
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.common.DatePickerDialog
import com.heartcare.agni.ui.common.Header
import com.heartcare.agni.ui.common.RadioButtonRow
import com.heartcare.agni.ui.theme.Black
import com.heartcare.agni.ui.theme.White
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.NavControllerConstants.TOBACCO_CESSATION_SAVED
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toddMMYYYYString
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTobaccoCessationScreen(
    navController: NavController,
    viewModel: AddTobaccoCessationViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            navController.previousBackStackEntry?.savedStateHandle
                ?.get<PatientResponse>(PATIENT)
                ?.let {
                    viewModel.patient = it
                    viewModel.getTodayTobaccoCessation(it.id)
                }
            viewModel.isLaunched = true
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                title = {
                    Text(
                        stringResource(R.string.add_tobacco_cessation),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.Clear, contentDescription = "CLEAR_ICON")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TobaccoCessationQuestions(viewModel)
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(if (isSystemInDarkTheme()) Black else White)
            ) {
                Button(
                    onClick = {
                        viewModel.addTobaccoCessation {
                            coroutineScope.launch {
                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                    TOBACCO_CESSATION_SAVED,
                                    true
                                )
                                navController.navigateUp()
                            }
                        }
                    },
                    enabled = viewModel.isValid(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    )

    if (viewModel.showDatePicker) {
        DatePickerDialog(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) =
                    utcTimeMillis <= Date().toEndOfDay()
            },
            initialSelectedDate = viewModel.dateOfPlan,
            dismissBtnText = stringResource(R.string.cancel),
            confirmBtnText = stringResource(R.string.ok),
            dismiss = { viewModel.showDatePicker = false },
            confirm = {
                viewModel.dateOfPlan = it
                viewModel.showDatePicker = false
            }
        )
    }
}

@Composable
private fun TobaccoCessationQuestions(viewModel: AddTobaccoCessationViewModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RadioQuestion(
                question = stringResource(R.string.tobacco_cessation_question_one),
                options = tobaccoUsageList(),
                selected = viewModel.tobaccoUse
            ) { selected ->
                viewModel.tobaccoUse = if (selected == viewModel.tobaccoUse) "" else selected
                if (viewModel.tobaccoUse.isBlank() || viewModel.tobaccoUse == TobaccoUsage.NO_I_DO_NOT_USE_TOBACCO.display) {
                    viewModel.resetBriefAdviceQuestions()
                }
            }

            AnimatedVisibility(viewModel.tobaccoUse.isNotBlank() && viewModel.tobaccoUse != TobaccoUsage.NO_I_DO_NOT_USE_TOBACCO.display) {
                BriefAdviceQuestion(viewModel)
            }
        }
    }
}

@Composable
private fun BriefAdviceQuestion(viewModel: AddTobaccoCessationViewModel) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RadioQuestion(
            title = stringResource(R.string.brief_advice),
            question = stringResource(R.string.brief_advice_question),
            options = YesNoEnum.listOfDisplay(),
            selected = viewModel.briefAdvice
        ) { selected ->
            viewModel.briefAdvice = if (selected == viewModel.briefAdvice) "" else selected
            if (viewModel.briefAdvice.isBlank()) viewModel.resetAssessedStatusQuestions()
        }

        AnimatedVisibility(viewModel.briefAdvice.isNotBlank()) {
            AssessedStatusQuestion(viewModel)
        }
    }
}

@Composable
private fun AssessedStatusQuestion(viewModel: AddTobaccoCessationViewModel) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RadioQuestion(
            title = stringResource(R.string.assessed_status),
            question = stringResource(R.string.assessed_status_question),
            options = YesNoEnum.listOfDisplay(),
            selected = viewModel.assessedStatus
        ) { selected ->
            viewModel.assessedStatus = if (selected == viewModel.assessedStatus) "" else selected
            if (viewModel.assessedStatus.isBlank() || viewModel.assessedStatus == YesNoEnum.NO.display) {
                viewModel.resetAssistToQuitQuestions()
            }
        }

        AnimatedVisibility(viewModel.assessedStatus == YesNoEnum.YES.display) {
            AssistToQuitQuestion(viewModel)
        }
    }
}

@Composable
private fun AssistToQuitQuestion(viewModel: AddTobaccoCessationViewModel) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RadioQuestion(
            title = stringResource(R.string.assist_to_quit),
            question = stringResource(R.string.assist_to_quit_question),
            options = quitPlanList(),
            selected = viewModel.assistQuit
        ) { selected ->
            viewModel.assistQuit = if (selected == viewModel.assistQuit) "" else selected
            if (viewModel.assistQuit.isBlank() ||
                viewModel.assistQuit == QuitPlan.NO.display ||
                viewModel.assistQuit == QuitPlan.NO_REFER_TO_INTENSIVE_COUNSELLING.display
            ) {
                viewModel.resetQuitPlanQuestions()
            } else if (viewModel.assistQuit != QuitPlan.YES_INTENSIVE_QUIT_PLAN.display) {
                viewModel.pharmacotherapy = ""
            }
        }

        AnimatedVisibility(
            viewModel.assistQuit == QuitPlan.YES_BRIEF_QUIT_PLAN.display ||
                    viewModel.assistQuit == QuitPlan.YES_INTENSIVE_QUIT_PLAN.display
        ) {
            QuitPlanQuestions(viewModel)
        }
    }
}

@Composable
private fun QuitPlanQuestions(viewModel: AddTobaccoCessationViewModel) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AnimatedVisibility(viewModel.assistQuit == QuitPlan.YES_INTENSIVE_QUIT_PLAN.display) {
            PharmacotherapyQuestion(viewModel)
        }
        StartDateField(
            label = stringResource(R.string.start_date_of_plan),
            value = viewModel.dateOfPlan.toddMMYYYYString(),
            onClick = { viewModel.showDatePicker = true }
        )
        StatusOfPlanQuestion(viewModel)
    }
}

@Composable
private fun PharmacotherapyQuestion(viewModel: AddTobaccoCessationViewModel) {
    RadioQuestion(
        title = stringResource(R.string.pharmacotherapy),
        question = stringResource(R.string.pharmacotherapy_provided_question),
        options = pharmacotherapyList(),
        selected = viewModel.pharmacotherapy
    ) { selected ->
        viewModel.pharmacotherapy = if (selected == viewModel.pharmacotherapy) "" else selected
    }
}

@Composable
private fun StatusOfPlanQuestion(viewModel: AddTobaccoCessationViewModel) {
    RadioQuestion(
        question = stringResource(R.string.status_of_plan),
        options = statusOfPlanList(),
        selected = viewModel.planStatus
    ) { selected ->
        viewModel.planStatus = selected
    }
}

@Composable
private fun RadioQuestion(
    title: String = "",
    question: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (title.isNotEmpty()) {
            Header(title)
        }
        Text(
            question,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column {
            options.forEach { option ->
                RadioButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                    selected = selected,
                    option = option,
                    onClick = { onSelected(option) }
                )
            }
        }
    }
}

@Composable
private fun StartDateField(label: String, value: String, onClick: () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = onClick) {
                Icon(painterResource(R.drawable.today_calendar), null)
            }
        },
        readOnly = true
    )
}