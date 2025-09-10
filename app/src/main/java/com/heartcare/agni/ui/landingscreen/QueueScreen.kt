package com.heartcare.agni.ui.landingscreen

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.appointments.CancelAppointmentDialog
import com.heartcare.agni.ui.common.Loader
import com.heartcare.agni.ui.common.WeekDaysComposable
import com.heartcare.agni.utils.constants.NavControllerConstants
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.NavControllerConstants.SELECTED_INDEX
import com.heartcare.agni.utils.converters.responseconverter.NameConverter
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.to14DaysWeek
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAge
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAppointmentDate
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAppointmentTime
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toOneYearFuture
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toOneYearPast
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun QueueScreen(
    navController: NavController,
    dateScrollState: LazyListState,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    viewModel: QueueViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val queueListState = rememberLazyListState()
    viewModel.rescheduled = navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>(
        NavControllerConstants.RESCHEDULED
    ) == true
    HandleLaunchedEffect(
        viewModel = viewModel,
        navController = navController,
        dateScrollState = dateScrollState,
        coroutineScope = coroutineScope,
        snackBarHostState = snackBarHostState,
        context = context
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AnimatedVisibility(queueListState.firstVisibleItemScrollOffset == 0 && queueListState.firstVisibleItemIndex == 0) {
            WeekDaysComposable(
                dateScrollState,
                viewModel.selectedDate,
                viewModel.weekList
            ) { showDialog, date ->
                if (showDialog == true) viewModel.showDatePicker = true
                else {
                    if (date != null) {
                        viewModel.selectedDate = date
                    }
                    viewModel.selectedChip = R.string.total_appointment
                    viewModel.getAppointmentListByDate()
                }
            }
        }
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Loader()
            }
        } else QueueList(queueListState, viewModel, navController)
    }
    QueueDialogs(
        viewModel = viewModel,
        coroutineScope = coroutineScope,
        dateScrollState = dateScrollState,
        snackBarHostState = snackBarHostState,
        context = context
    )
}

@Composable
private fun HandleLaunchedEffect(
    viewModel: QueueViewModel,
    navController: NavController,
    dateScrollState: LazyListState,
    coroutineScope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            dateScrollState.scrollToItem(7, scrollOffset = -130)
            viewModel.isLaunched = true
        }
        if (viewModel.rescheduled) {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(
                    message = context.getString(R.string.appointment_rescheduled)
                )
            }
            navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>(
                NavControllerConstants.RESCHEDULED
            )
        }
        viewModel.syncData()
    }
}

@Composable
private fun QueueList(
    queueListState: LazyListState,
    viewModel: QueueViewModel,
    navController: NavController
) {
    LazyColumn(
        state = queueListState,
        content = {
            item {
                Spacer(Modifier.height(10.dp))
            }
            // scheduled
            item {
                ListWithLabel(
                    surfaceColor = if (viewModel.scheduledQueueListWithPatient.isEmpty()
                        || viewModel.selectedDate.toTodayStartDate() != Date().toTodayStartDate()
                    ) MaterialTheme.colorScheme.surface
                    else MaterialTheme.colorScheme.secondaryContainer,
                    label = stringResource(
                        id = R.string.scheduled_count,
                        viewModel.scheduledQueueListWithPatient.size
                    ),
                    listOfAppointment = viewModel.scheduledQueueListWithPatient,
                    viewModel = viewModel,
                    navController = navController
                )
            }
            // assessed
            item {
                ListWithLabel(
                    surfaceColor = MaterialTheme.colorScheme.surface,
                    label = stringResource(
                        id = R.string.assessed_count,
                        viewModel.assessedQueueListWithPatient.size
                    ),
                    listOfAppointment = viewModel.assessedQueueListWithPatient,
                    viewModel = viewModel,
                    navController = navController
                )
            }
            // prescribed
            item {
                ListWithLabel(
                    surfaceColor = MaterialTheme.colorScheme.surface,
                    label = stringResource(
                        id = R.string.prescribed_count,
                        viewModel.prescribedQueueListWithPatient.size
                    ),
                    listOfAppointment = viewModel.prescribedQueueListWithPatient,
                    viewModel = viewModel,
                    navController = navController
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

@Composable
private fun ListWithLabel(
    surfaceColor: Color,
    label: String,
    listOfAppointment: List<Pair<PatientResponse, AppointmentResponseLocal>>,
    viewModel: QueueViewModel,
    navController: NavController
) {
    Surface(
        color = surfaceColor,
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 18.dp,
                    start = 18.dp,
                    end = 18.dp,
                    bottom = 10.dp
                )
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.outline
            )
            listOfAppointment.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QueuePatientCard(
                        navController,
                        viewModel,
                        item.second,
                        item.first
                    )
                }
            }
        }
    }
}

@Composable
private fun QueuePatientCard(
    navController: NavController,
    viewModel: QueueViewModel,
    appointmentResponseLocal: AppointmentResponseLocal,
    patient: PatientResponse?
) {
    val age = patient?.birthDate?.toTimeInMilli()?.toAge()
    ElevatedCard(
        modifier = Modifier
            .clickable {
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    PATIENT,
                    patient
                )
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    SELECTED_INDEX,
                    1
                )
                navController.navigate(Screen.PatientLandingScreen.route)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 12.dp,
                    end = 24.dp,
                    bottom = 10.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PatientCardDetails(
                NameConverter.getFullName(
                    patient?.firstName,
                    patient?.lastName
                ),
                stringResource(
                    R.string.patient_queue_card_subtitle,
                    patient?.gender?.get(0)?.uppercase() ?: "",
                    age ?: 0,
                    if (patient?.heartcareId.isNullOrEmpty()) "--"
                    else patient.heartcareId
                ),
                appointmentResponseLocal.slot.start.toAppointmentTime()
            )
        }
        if (
            appointmentResponseLocal.slot.start.time >= Date().toEndOfDay()
        ) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = {
                        viewModel.showCancelAppointmentDialog = true
                        viewModel.patientSelected = patient
                        viewModel.appointmentSelected = appointmentResponseLocal
                    },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    TextButton(
                        onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                NavControllerConstants.APPOINTMENT_SELECTED,
                                appointmentResponseLocal
                            )
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                PATIENT,
                                patient
                            )
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                NavControllerConstants.IF_RESCHEDULING,
                                true
                            )
                            navController.navigate(Screen.ScheduleAppointments.route)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.reschedule))
                    }
                }
            }
        }
    }
}

@Composable
private fun PatientCardDetails(name: String, subTitle: String, time: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subTitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.schedule_icon),
                contentDescription = "SCHEDULE_ICON",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = time,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun QueueDialogs(
    viewModel: QueueViewModel,
    coroutineScope: CoroutineScope,
    dateScrollState: LazyListState,
    snackBarHostState: SnackbarHostState,
    context: Context
) {
    if (viewModel.showCancelAppointmentDialog) {
        CancelAppointmentDialog(
            patient = viewModel.patientSelected!!,
            dateAndTime = viewModel.appointmentSelected?.slot?.start?.toAppointmentDate()!!
        ) { cancel ->
            if (cancel) {
                viewModel.cancelAppointment {
                    viewModel.getAppointmentListByDate()
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = context.getString(R.string.appointment_cancelled)
                        )
                    }
                }
            }
            viewModel.showCancelAppointmentDialog = false
        }
    }
    if (viewModel.showDatePicker) {
        DatePickerComposable(viewModel, coroutineScope, dateScrollState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerComposable(
    viewModel: QueueViewModel,
    coroutineScope: CoroutineScope,
    dateScrollState: LazyListState
) {
    val selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis >= Date().toTodayStartDate()
                .toOneYearPast().time && utcTimeMillis <= Date().toOneYearFuture().time
        }
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = viewModel.selectedDate.time,
        selectableDates = selectableDates
    )
    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }
    DatePickerDialog(
        onDismissRequest = {
            viewModel.showDatePicker = false
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.showDatePicker = false
                    viewModel.selectedChip = R.string.total_appointment
                    viewModel.selectedDate =
                        datePickerState.selectedDateMillis?.let { dateInLong ->
                            Date(
                                dateInLong
                            )
                        } ?: Date()
                    viewModel.weekList = viewModel.selectedDate.to14DaysWeek()
                    viewModel.getAppointmentListByDate()
                    coroutineScope.launch {
                        dateScrollState.scrollToItem(7, scrollOffset = -130)
                    }
                },
                enabled = confirmEnabled.value
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.showDatePicker = false
                }
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}