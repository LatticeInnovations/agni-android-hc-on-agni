package com.heartcare.agni.ui.screeningreportdownload

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.BuildConfig
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.utils.QR.generateQrCode
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAppointmentDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreeningReportDownloadScreen(
    navController: NavController,
    viewModel: ScreeningReportDownloadViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            viewModel.patient = navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(PATIENT)
            viewModel.patient?.let { patient ->
                viewModel.getAppointmentsList(patient.id)
            }
        }
        viewModel.isLaunched = true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.screening_report_download),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON")
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (viewModel.appointmentList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_record_found),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        viewModel.appointmentList.forEach { appointment ->
                            var isExpanded by rememberSaveable { mutableStateOf(false) }
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = appointment.slot.start.toAppointmentDate(),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        appointment.campaignId?.let {
                                            Text(
                                                text = appointment.campaignId,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    FilledTonalIconButton(
                                        onClick = {
                                            // download pdf
                                            viewModel.getAssessmentsByAppointmentId(
                                                appointment,
                                                context
                                            )
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.file_download),
                                            contentDescription = null
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            isExpanded = !isExpanded
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp
                                            else Icons.Default.KeyboardArrowDown,
                                            contentDescription = null
                                        )
                                    }
                                }
                                AnimatedVisibility(
                                    visible = isExpanded
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (appointment.reportToken == null) {
                                            Text(
                                                text = stringResource(R.string.qr_not_generated_yet),
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        } else {
                                            val qr = generateQrCode("${BuildConfig.BASE_URL}access/${appointment.reportToken}")
                                            Image(
                                                bitmap = qr.asImageBitmap(),
                                                contentDescription = "QR Code",
                                                modifier = Modifier.fillMaxWidth(0.5f)
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    )
}