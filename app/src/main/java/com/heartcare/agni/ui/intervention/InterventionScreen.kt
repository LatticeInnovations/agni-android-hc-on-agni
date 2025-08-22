package com.heartcare.agni.ui.intervention

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT

@Composable
fun InterventionScreen(
    navController: NavController,
    viewModel: InterventionViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        if (!viewModel.isLaunched) {
            navController.previousBackStackEntry?.savedStateHandle
                ?.get<PatientResponse>(PATIENT)?.let {
                    viewModel.patient = it
                }
            viewModel.isLaunched = true
        }
    }
}