package com.heartcare.agni.ui.patientregistration

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.ui.patientregistration.model.PatientRegister
import com.heartcare.agni.ui.patientregistration.preview.DiscardDialog
import com.heartcare.agni.ui.patientregistration.step1.PatientRegistrationStepOne
import com.heartcare.agni.ui.patientregistration.step2.PatientRegistrationStepTwo
import com.heartcare.agni.ui.patientregistration.step3.PatientRegistrationStepThree

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientRegistration(
    navController: NavController,
    viewModel: PatientRegistrationViewModel = viewModel()
) {
    var patientRegister = PatientRegister()
    if (navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>(
            "isEditing"
        ) == true
    ) {
        viewModel.currentStep = navController.currentBackStackEntry?.savedStateHandle?.get<Int>(
            "currentStep"
        )!!
        viewModel.isEditing = navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>(
            "isEditing"
        )!!
        patientRegister =
            navController.currentBackStackEntry?.savedStateHandle?.get<PatientRegister>(
                "patient_register_details"
            )!!
    }
    BackHandler {
        if (viewModel.currentStep > 1) viewModel.currentStep -= 1
        else viewModel.openDialog = true
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.patient_registration),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    if (viewModel.currentStep != 1) {
                        IconButton(onClick = {
                            viewModel.currentStep -= 1
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "BACK_ICON"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    IconButton(onClick = {
                        viewModel.openDialog = true
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "CLEAR_ICON")
                    }
                }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                when (viewModel.currentStep) {
                    1 -> PatientRegistrationStepOne(patientRegister)
                    2 -> PatientRegistrationStepTwo(patientRegister)
                    3 -> PatientRegistrationStepThree(navController, patientRegister)
                }
                if (viewModel.openDialog) {
                    DiscardDialog(
                        closeDialog = {
                            viewModel.openDialog = false
                        },
                        navigateBack = {
                            viewModel.openDialog = false
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    )
}
