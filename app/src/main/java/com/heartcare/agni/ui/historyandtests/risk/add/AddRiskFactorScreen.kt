package com.heartcare.agni.ui.historyandtests.risk.add

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.historyandtests.risk.add.questions.TobaccoQuestions
import com.heartcare.agni.ui.theme.Black
import com.heartcare.agni.ui.theme.White
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.NavControllerConstants.RISK_FACTORS_SAVED
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRiskFactorScreen(
    navController: NavController,
    viewModel: AddRiskFactorViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            navController.previousBackStackEntry?.savedStateHandle
                ?.get<PatientResponse>(PATIENT)?.let {
                    viewModel.patient = it
                }
            viewModel.isLaunched = true
        }
    }
    Scaffold(
        modifier = Modifier
            .imePadding(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.add_risk_factor),
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
            ) {
                RiskFactorContent(viewModel)
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(
                        color = if (isSystemInDarkTheme()) Black else White
                    )
            ) {
                Button(
                    onClick = {
                        // save
                        coroutineScope.launch {
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                RISK_FACTORS_SAVED,
                                true
                            )
                            navController.navigateUp()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    )
}

@Composable
private fun RiskFactorContent(
    viewModel: AddRiskFactorViewModel
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TobaccoQuestions(viewModel)
    }
}

@Composable
fun Question(
    question: String
) {
    Text(
        text = question,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}