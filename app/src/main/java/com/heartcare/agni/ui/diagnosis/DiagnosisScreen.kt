package com.heartcare.agni.ui.diagnosis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.ExpandableCard
import com.heartcare.agni.utils.constants.NavControllerConstants.DIAGNOSIS_SAVED
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisScreen(
    navController: NavController,
    viewModel: DiagnosisViewModel = viewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                PATIENT
            )?.let {
                viewModel.patient = it
            }
            viewModel.isLaunched = true
        }
        navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
            if (handle.remove<Boolean>(DIAGNOSIS_SAVED) == true) {
                snackBarHostState.showSnackbar(context.getString(R.string.diagnosis_added_successfully))
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.diagnosis),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("HEADING_TAG")
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
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
                if (viewModel.diagnosisList.isEmpty()) {
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
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.recent_diagnosis),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        viewModel.diagnosisList.forEach { _ ->
                            ExpandableCard(
                                createdOn = Date(),
                                practitionerName = "Dr. Anamika Sood",
                                listOfItems = listOf("A0100, Typhoid fever, unspecified", "A1810, Tuberculosis of genitourinary system, unspecified"),
                                isBulleted = true,
                                listTitle = stringResource(R.string.diagnosis_colon)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = {
                        // navigate to add diagnosis
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            PATIENT,
                            viewModel.patient
                        )
                        navController.navigate(Screen.AddDiagnosisScreen.route)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_icon),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = stringResource(R.string.add_diagnosis))
                }
            }
        }
    )
}