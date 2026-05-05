package com.heartcare.agni.ui.examination

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.AppointmentCompletedDialog
import com.heartcare.agni.ui.common.CustomDialog
import com.heartcare.agni.ui.common.ExpandableCard
import com.heartcare.agni.ui.common.RecordTypeSelectionContent
import com.heartcare.agni.ui.common.ScreeningSiteListContent
import com.heartcare.agni.ui.patientlandingscreen.AllSlotsBookedDialog
import com.heartcare.agni.utils.constants.NavControllerConstants.CAMPAIGN_ID
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.NavControllerConstants.TEST_EXAMINATION_SAVED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestExaminationScreen(
    navController: NavController,
    viewModel: TestExaminationViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        if (viewModel.currentStep > 0) {
            if (viewModel.currentStep == 2) {
                viewModel.currentStep = 1
            } else if (viewModel.currentStep == 1) {
                viewModel.currentStep = 0
            }
        } else {
            navController.navigateUp()
        }
    }

    HandleLaunchedEffect(navController, viewModel, snackBarHostState, context)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState,
            modifier = if (viewModel.currentStep!=0) Modifier.padding(bottom = 80.dp) else Modifier
            ) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.test_and_examinations),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (viewModel.currentStep > 0) {
                            if (viewModel.currentStep == 2) {
                                viewModel.currentStep = 1
                            } else if (viewModel.currentStep == 1) {
                                viewModel.currentStep = 0
                            }
                        } else {
                            navController.navigateUp()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                when (viewModel.currentStep) {
                    0 -> TestExaminationContent(viewModel)
                    1 -> RecordTypeSelectionContent(
                        modifier = Modifier.fillMaxSize(),
                        selectedType = viewModel.selectedType,
                        onTypeSelected = { viewModel.selectedType = it },
                        onContinueClick = {
                            if (viewModel.selectedType == RecordType.FACILITY) {
                                viewModel.selectedCampaignId = null
                                handleAddTestLogic(viewModel, navController, coroutineScope, snackBarHostState, context)
                            } else if (viewModel.selectedType == RecordType.SCREENING_SITE) {
                                viewModel.currentStep = 2
                            }
                        }
                    )
                    2 -> ScreeningSiteListContent(
                        modifier = Modifier.fillMaxSize(),
                        sites = viewModel.screeningSites.map { it.name },
                        selectedSite = viewModel.screeningSites.find { it.id == viewModel.selectedCampaignId }?.name,
                        onSiteSelected = { siteName ->
                            viewModel.selectedCampaignId = viewModel.screeningSites.find { it.name == siteName }?.id
                        },
                        onBackClick = {
                            viewModel.currentStep = 1
                            viewModel.selectedCampaignId = null
                        },
                        onContinueClick = {
                            if (viewModel.selectedCampaignId != null) {
                                handleAddTestLogic(viewModel, navController, coroutineScope, snackBarHostState, context)
                            }
                        }
                    )
                }
            }
        },
        bottomBar = {
            if (viewModel.currentStep == 0) {
                TestExaminationBottomBar(
                    viewModel = viewModel,
                    onClickAdd = {
                        if (viewModel.patient!!.patientDeceasedReason.isNullOrBlank()) {
                            if (viewModel.isScreeningSiteEnabled) {
                                viewModel.currentStep = 1
                            } else {
                                handleAddTestLogic(viewModel, navController, coroutineScope, snackBarHostState, context)
                            }
                        } else {
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(
                                    context.getString(R.string.patient_deceased_error_msg)
                                )
                            }
                        }
                    }
                )
            }
        }
    )

    TestExaminationDialogs(navController, viewModel, coroutineScope)
}


@Composable
private fun HandleLaunchedEffect(
    navController: NavController,
    viewModel: TestExaminationViewModel,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    LaunchedEffect(Unit) {
        if (!viewModel.isLaunched) {
            navController.previousBackStackEntry?.savedStateHandle
                ?.get<PatientResponse>(PATIENT)?.let {
                    viewModel.patient = it
                }
            viewModel.getAppointmentInfo { }
            viewModel.isLaunched = true
        }
        viewModel.getExaminationRecords(viewModel.patient!!.id)
        navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
            viewModel.currentStep=0
            viewModel.selectedType=null
            viewModel.selectedCampaignId=null
            if (handle.remove<Boolean>(TEST_EXAMINATION_SAVED) == true) {
                snackBarHostState.showSnackbar(
                    context.getString(
                        if (viewModel.todayTestExamination == null) R.string.test_and_examinations_saved
                        else R.string.test_and_examinations_updated
                    )
                )
            }
        }
    }
}

@Composable
private fun TestExaminationContent(
    viewModel: TestExaminationViewModel
) {
    if (viewModel.testExaminationLists.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.no_record_found),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.weight(2f))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                viewModel.testExaminationLists.forEach { testExamination ->
                    ExpandableCard(
                        createdOn = testExamination.appUpdatedDate,
                        practitionerName = testExamination.practitionerName,
                        screenSiteName = testExamination.screeningSiteName,
                        listOfItems = testExamination.examinations.map { "${it.code} ${it.display}" },
                        isBulleted = true
                    )
                }
            }
        }
    }
}


private fun handleAddTestLogic(
    viewModel: TestExaminationViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    viewModel.getAppointmentInfo(
        callback = {
            when {
                viewModel.existsInOtherHospital -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = context.getString(R.string.appointment_exists_in_other_hospital)
                        )
                    }
                }

                viewModel.canAddAssessment -> {
                    viewModel.currentStep = 0
                    coroutineScope.launch {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            PATIENT,
                            viewModel.patient
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            CAMPAIGN_ID,
                            viewModel.selectedCampaignId
                        )
                        navController.navigate(Screen.AddTestExaminationScreen.route)
                    }
                }

                viewModel.isAppointmentCompleted -> {
                    viewModel.showAppointmentCompletedDialog = true
                }

                else -> {
                    if (viewModel.selectedCampaignId != null) {
                        viewModel.addPatientToCampaignQueue(
                            viewModel.patient!!,
                            viewModel.selectedCampaignId!!,
                            addedToQueue = {
                                viewModel.showAddToQueueDialog = false
                                coroutineScope.launch {
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        PATIENT,
                                        viewModel.patient
                                    )
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        CAMPAIGN_ID,
                                        viewModel.selectedCampaignId
                                    )
                                    navController.navigate(Screen.AddTestExaminationScreen.route)
                                }
                            }
                        )
                    }else {
                        viewModel.showAddToQueueDialog = true
                    }
                }
            }
        }
    )
}


@Composable
private fun TestExaminationBottomBar(
    viewModel: TestExaminationViewModel,
    onClickAdd: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(0.5.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            onClick = onClickAdd
        ) {
            Icon(Icons.Filled.Add, Icons.Filled.Add.name)
            Spacer(Modifier.width(6.dp))
            Text(
                text = getBtnText(viewModel)
            )
        }
    }
}

@Composable
private fun getBtnText(viewModel: TestExaminationViewModel): String {
    return if (viewModel.todayTestExamination != null && viewModel.todayTestExamination!!.campaignId != null) {
        stringResource(id =  R.string.update_interventions)
    } else {
        stringResource(
            id = if (viewModel.todayTestExamination == null || viewModel.existsInOtherHospital) R.string.add_test_and_examinations
            else R.string.update_test_and_examinations
        )
    }
}
@Composable
private fun TestExaminationDialogs(
    navController: NavController,
    viewModel: TestExaminationViewModel,
    coroutineScope: CoroutineScope
) {
    if (viewModel.showAddToQueueDialog) {
        AddToQueueDialog(viewModel, navController, coroutineScope)
    }

    if (viewModel.ifAllSlotsBooked) {
        AllSlotsBookedDialog {
            viewModel.showAllSlotsBookedDialog = false
        }
    }

    if (viewModel.showAppointmentCompletedDialog) {
        AppointmentCompletedDialog {
            viewModel.showAppointmentCompletedDialog = false
        }
    }
}

@Composable
private fun AddToQueueDialog(
    viewModel: TestExaminationViewModel,
    navController: NavController,
    coroutineScope: CoroutineScope
) {
    CustomDialog(
        title = stringResource(
            if (viewModel.appointment != null) R.string.patient_arrived_question else R.string.add_to_queue_question
        ),
        text = stringResource(R.string.add_to_queue_assessment_dialog_description),
        dismissBtnText = stringResource(R.string.dismiss),
        confirmBtnText = stringResource(
            if (viewModel.appointment != null) R.string.mark_arrived else R.string.add_to_queue
        ),
        dismiss = { viewModel.showAddToQueueDialog = false },
        confirm = {
            if (viewModel.appointment != null) {
                viewModel.updateStatusToArrived(
                    patient = viewModel.patient!!,
                    appointment = viewModel.appointment!!,
                    updated = {
                        viewModel.showAddToQueueDialog = false
                        coroutineScope.launch {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                PATIENT,
                                viewModel.patient
                            )
                            navController.navigate(Screen.AddTestExaminationScreen.route)
                        }
                    }
                )
            } else {
                if (viewModel.ifAllSlotsBooked) {
                    viewModel.showAllSlotsBookedDialog = true
                } else {
                    viewModel.addPatientToQueue(
                        viewModel.patient!!,
                        addedToQueue = {
                            viewModel.showAddToQueueDialog = false
                            coroutineScope.launch {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    PATIENT,
                                    viewModel.patient
                                )
                                navController.navigate(Screen.AddTestExaminationScreen.route)
                            }
                        }
                    )
                }
            }
        }
    )
}