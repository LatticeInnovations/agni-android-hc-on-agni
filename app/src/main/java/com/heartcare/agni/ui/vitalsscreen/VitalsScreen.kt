package com.heartcare.agni.ui.vitalsscreen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.github.mikephil.charting.data.Entry
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.CustomDialog
import com.heartcare.agni.ui.patientlandingscreen.AllSlotsBookedDialog
import com.heartcare.agni.ui.theme.VitalLabel
import com.heartcare.agni.ui.vitalsscreen.components.CustomChip
import com.heartcare.agni.ui.vitalsscreen.components.LineChartView
import com.heartcare.agni.ui.vitalsscreen.components.LineChartViewGlucose
import com.heartcare.agni.ui.vitalsscreen.components.SegmentedButtonForVital
import com.heartcare.agni.ui.vitalsscreen.enums.BGEnum
import com.heartcare.agni.ui.vitalsscreen.enums.VitalsTrendEnum
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.VitalConstants.ALL
import com.heartcare.agni.utils.constants.VitalConstants.CVD_RECORD
import com.heartcare.agni.utils.constants.VitalConstants.LIST_TYPE_CVD
import com.heartcare.agni.utils.constants.VitalConstants.LIST_TYPE_VITAL
import com.heartcare.agni.utils.constants.VitalConstants.VITAL_UPDATE_OR_ADD
import com.heartcare.agni.utils.converters.responseconverter.GsonConverters.toJson
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.convertDateFormat
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.convertedDate
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.formatDateToDayMonth
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsScreen(navController: NavController, vitalsViewModel: VitalsViewModel = hiltViewModel()) {

    HandleLaunchedEffect(vitalsViewModel, navController)

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = vitalsViewModel.msg) {
        if (vitalsViewModel.msg.isNotBlank()) {
            snackBarHostState.showSnackbar(vitalsViewModel.msg)
            vitalsViewModel.msg = ""
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "BACK_ICON"
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.vitals),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("VITAL_TITLE_TEXT")
                    )
                }
            )
        },
        content = { paddingValues ->
            ShowGraphAndList(vitalsViewModel, paddingValues, context)
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .padding()
                    .navigationBarsPadding(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(), onClick = {
                            vitalsViewModel.getAppointmentInfo {
                                if (vitalsViewModel.canAddAssessment) {
                                    scope.launch {
                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                            key = PATIENT, vitalsViewModel.patient
                                        )
                                        navController.navigate(Screen.AddVitalsScreen.route)
                                    }

                                } else if (vitalsViewModel.isAppointmentCompleted) {
                                    vitalsViewModel.showAppointmentCompletedDialog = true
                                } else {
                                    vitalsViewModel.showAddToQueueDialog = true
                                }
                            }

                        },
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = stringResource(R.string.add_icon),
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            stringResource(id = R.string.add_vitals)
                        )
                    }
                }
            }
        }
    )
    ShowDialogs(vitalsViewModel, navController, scope)
}

@Composable
private fun ShowGraphAndList(
    vitalsViewModel: VitalsViewModel, paddingValues: PaddingValues, context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .padding(paddingValues = paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (vitalsViewModel.isVitalExist || vitalsViewModel.previousRecords.isNotEmpty()) {
            val vitals by vitalsViewModel._vitals.collectAsState()
            val combinedList = getCombinedList(vitalsViewModel, vitals)
            LazyColumn(userScrollEnabled = true) {
                item {
                    VitalsTrendGraph(vitalsViewModel)
                }
                item {
                    SegmentedButtonForVital(
                        options = listOf(ALL, CVD_RECORD),
                        isCVDListEmpty = vitalsViewModel.previousRecords.isEmpty(),
                        selectedOption = vitalsViewModel.selectedOption,
                        onOptionSelected = { vitalsViewModel.selectedOption = it })
                }
                items(if (vitalsViewModel.selectedOption == CVD_RECORD) combinedList.filter { it.type == LIST_TYPE_CVD } else combinedList) { item ->
                    when (item.type) {
                        LIST_TYPE_VITAL -> VitalsCardLayout(
                            vital = item.content as VitalResponse, context = context
                        )

                        LIST_TYPE_CVD -> CVDRecordCardLayout(
                            cvdResponse = item.content as CVDResponse
                        )
                    }

                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.no_record_found),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}

private fun getCombinedList(
    vitalsViewModel: VitalsViewModel, vitals: List<VitalResponse>
): List<CombineVitalAndCVDRecord> {
    return (vitals.map { vital ->
        CombineVitalAndCVDRecord(
            LIST_TYPE_VITAL, vital.appUpdatedDate, vital
        )
    } + vitalsViewModel.previousRecords.map {
        CombineVitalAndCVDRecord(
            LIST_TYPE_CVD, it.createdOn, it
        )
    }).sortedBy { it.date }.reversed()
}

@Composable
fun HandleLaunchedEffect(vitalsViewModel: VitalsViewModel, navController: NavController) {
    LaunchedEffect(key1 = vitalsViewModel.isLaunched) {
        vitalsViewModel.apply {
            if (isLaunched) {
                patient =
                    navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(
                        PATIENT
                    )
                patient?.let {
                    getStudentTodayAppointment(
                        Date(Date().toTodayStartDate()), Date(Date().toEndOfDay()), patient!!.id
                    )
                    msg = navController.currentBackStackEntry?.savedStateHandle?.get<String>(
                        VITAL_UPDATE_OR_ADD
                    ) ?: ""
                    getVitals()
                    getRecords()

                    Timber.d("Student:  ${_vitals.toJson()} : ${patient.toJson()} $appointmentResponseLocal")
                } ?: run {
                    Timber.d("Student: null")

                }

            }
            isLaunched = true

        }

    }
}

@Composable
private fun ShowDialogs(
    vitalsViewModel: VitalsViewModel, navController: NavController, scope: CoroutineScope
) {
    if (vitalsViewModel.showAddToQueueDialog) {
        CustomDialog(
            title = if (vitalsViewModel.appointment != null) stringResource(id = R.string.patient_arrived_question) else stringResource(
                id = R.string.add_to_queue_question
            ),
            text = stringResource(id = R.string.add_to_queue_vital_dialog_description),
            dismissBtnText = stringResource(id = R.string.dismiss),
            confirmBtnText = if (vitalsViewModel.appointment != null) stringResource(id = R.string.mark_arrived) else stringResource(
                id = R.string.add_to_queue
            ),
            dismiss = { vitalsViewModel.showAddToQueueDialog = false },
            confirm = {
                if (vitalsViewModel.appointment != null) {
                    vitalsViewModel.updateStatusToArrived(
                        vitalsViewModel.patient!!, vitalsViewModel.appointment!!
                    ) {
                        vitalsViewModel.showAddToQueueDialog = false
                        scope.launch {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                key = PATIENT, vitalsViewModel.patient
                            )
                            navController.navigate(Screen.AddVitalsScreen.route)
                        }

                    }
                } else {
                    if (vitalsViewModel.ifAllSlotsBooked) {
                        vitalsViewModel.showAllSlotsBookedDialog = true
                    } else {
                        vitalsViewModel.addPatientToQueue(vitalsViewModel.patient!!) {
                            vitalsViewModel.showAddToQueueDialog = false

                            scope.launch {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    key = PATIENT, vitalsViewModel.patient
                                )
                                navController.navigate(Screen.AddVitalsScreen.route)
                            }
                        }


                    }
                }
            })
    }
    if (vitalsViewModel.ifAllSlotsBooked) {
        AllSlotsBookedDialog {
            vitalsViewModel.showAllSlotsBookedDialog = false
        }
    }
}


@Composable
private fun VitalsTrendGraph(vitalsViewModel: VitalsViewModel, modifier: Modifier = Modifier) {
    val list by vitalsViewModel._vitals.collectAsState()

    if (list
            .groupBy { it.appUpdatedDate.formatDateToDayMonth() }.keys.size > 1 || list.filter { it.bloodGlucose != null }
            .groupBy { it.appUpdatedDate.formatDateToDayMonth() }.keys.size > 1 || getCVDAndVitalListSize(
            list,
            vitalsViewModel
        ) > 1
    ) {
        ShowTrendGraphCard(modifier, vitalsViewModel, list.reversed())
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceBright
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .testTag("VITAL_TITLE_TEXT")
                    .padding(20.dp),
                text = stringResource(R.string.graph_empty_error_msg),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ShowTrendGraphCard(
    modifier: Modifier,
    vitalsViewModel: VitalsViewModel,
    list: List<VitalResponse>
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                MaterialTheme.colorScheme.surfaceBright
            )
    ) {
        Text(
            modifier = Modifier
                .testTag("VITAL_TITLE_TEXT")
                .padding(start = 16.dp, top = 16.dp),
            text = stringResource(R.string.vitals_trend),
            style = MaterialTheme.typography.bodyMedium
        )
        HorizontalDivider(
            modifier = Modifier.padding(
                start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp
            )
        )
        LaunchedEffect(key1 = vitalsViewModel.isFistLaunch) {
            if (!vitalsViewModel.isFistLaunch) {
                showChipSelection(vitalsViewModel, list)
                vitalsViewModel.isFistLaunch = true
            }

        }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedVisibility(
                visible = getCVDAndVitalListSize(list, vitalsViewModel) > 1
            ) {
                CustomChip(
                    idSelected = vitalsViewModel.isWeightSelected,
                    label = VitalsTrendEnum.Weight.name
                ) {
                    vitalsViewModel.apply {
                        isWeightSelected = true
                        isHRSelected = false
                        isRRSelected = false
                        isSpO2Selected = false
                        isGlucoseSelected = false
                        isBPSelected = false
                    }
                }
            }
            AnimatedVisibility(visible = list.filter { it.bloodGlucose != null && it.bloodGlucose.type == BGEnum.RANDOM.value || it.bloodGlucose?.type == BGEnum.FASTING.value }
                .groupBy { it.appUpdatedDate.formatDateToDayMonth() }.keys.size > 1) {

                CustomChip(
                    idSelected = vitalsViewModel.isGlucoseSelected,
                    label = VitalsTrendEnum.Glucose.name
                ) {
                    vitalsViewModel.apply {
                        isWeightSelected = false
                        isHRSelected = false
                        isRRSelected = false
                        isSpO2Selected = false
                        isGlucoseSelected = true
                        isBPSelected = false
                    }
                }
            }
            Timber.d("SIZE: ${getCVDAndVitalListSize(list, vitalsViewModel)}")
            AnimatedVisibility(visible = getCVDAndVitalListSize(list, vitalsViewModel) > 1) {
                CustomChip(
                    idSelected = vitalsViewModel.isBPSelected, label = VitalsTrendEnum.BP.name
                ) {
                    vitalsViewModel.apply {
                        isWeightSelected = false
                        isHRSelected = false
                        isRRSelected = false
                        isSpO2Selected = false
                        isGlucoseSelected = false
                        isBPSelected = true
                    }
                }
            }
        }
        AnimatedVisibility(vitalsViewModel.isGlucoseSelected) {
            GraphLegendGroup(
                legendList = listOf(
                    Pair(BGEnum.RANDOM.value.capitalizeFirstLetter(), VitalLabel),
                    Pair(
                        BGEnum.FASTING.value.capitalizeFirstLetter(),
                        MaterialTheme.colorScheme.primary
                    )

                )
            )

        }
        AnimatedVisibility(vitalsViewModel.isBPSelected) {
            GraphLegendGroup(
                legendList = listOf(
                    Pair(stringResource(id = R.string.systolic), VitalLabel),
                    Pair(stringResource(id = R.string.diastolic), MaterialTheme.colorScheme.primary)

                )
            )
        }

        if (!vitalsViewModel.isGlucoseSelected && (vitalsViewModel.isWeightSelected || vitalsViewModel.isHRSelected || vitalsViewModel.isRRSelected || vitalsViewModel.isSpO2Selected || vitalsViewModel.isBPSelected)) {
            LineChartView(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                    .height(200.dp),
                entries1 = getChartEntries(list, vitalsViewModel),
                entries2 = getChartEntries2(list, vitalsViewModel),
                labels = getLabels(vitalsViewModel, list),
                isBp = vitalsViewModel.isBPSelected
            )
            Timber.d("List : ${list.map { it.appUpdatedDate.formatDateToDayMonth() }}")
        } else if (vitalsViewModel.isGlucoseSelected) {
            LineChartViewGlucose(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                    .height(200.dp),
                entriesRandom = getChartEntries2(list, vitalsViewModel),
                entriesFasting = getChartEntries(list, vitalsViewModel),
                labels = getLabels(vitalsViewModel, list)
            )
        }
    }


}

private fun getLabels(vitalsViewModel: VitalsViewModel, list: List<VitalResponse>): List<String> {
    return if (!vitalsViewModel.isBPSelected) {
        (list.map { it.appUpdatedDate }).distinct().sorted().map { it.formatDateToDayMonth() }
            .distinct()
    } else {
        val vitalDates = list.map { it.appUpdatedDate }
        val cvdDates = vitalsViewModel.previousRecords.map { it.createdOn }

        // Combine and sort the dates
        (vitalDates + cvdDates).distinct().sorted().map { it.formatDateToDayMonth() }.distinct()
    }
}

private fun getCVDAndVitalListSize(list: List<VitalResponse>, vitalsViewModel: VitalsViewModel): Int {
    return list
        .groupBy { it.appUpdatedDate.formatDateToDayMonth() }.keys.size + vitalsViewModel.previousRecords.groupBy { it.createdOn.formatDateToDayMonth() }.keys.size
}

@Composable
private fun GraphLegendGroup(legendList: List<Pair<String, Color>>) {
    Column(modifier = Modifier.padding(start = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Iterate through each legend item (text and color pair)
            legendList.forEach { (label, color) ->
                IndicatorWithText(color = color, text = label)

                Spacer(modifier = Modifier.width(8.dp)) // Spacer between legend items
            }
        }
    }
}

@Composable
private fun IndicatorWithText(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Circle indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Text next to the circle
        Text(
            text = text, color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge
        )
    }
}


private fun String.capitalizeFirstLetter(): String {
    return this.lowercase().replaceFirstChar { it.uppercaseChar() }
}

private fun getChartEntries(
    list: List<VitalResponse>,
    vitalsViewModel: VitalsViewModel
): List<Entry>? {

    return when {
        vitalsViewModel.isWeightSelected -> {
            getEntriesFromCVD(vitalsViewModel.previousRecords) { it.weight.toFloat() }
        }

        vitalsViewModel.isGlucoseSelected -> {
            getEntries(
                list.filter { it.bloodGlucose?.type == BGEnum.FASTING.value },
                list
            ) {
                if (it.bloodGlucose?.unit?.equals(BGEnum.BG_MMO.value) == true) it.bloodGlucose.value.toFloat()
                    .times(18.018f)
                else it.bloodGlucose?.value?.toFloat()
            }
        }

        vitalsViewModel.isBPSelected -> {
            getEntriesFromCVD(vitalsViewModel.previousRecords) { it.bpSystolic.toFloat() }
        }

        else -> {
            null
        }
    }

}


fun getChartEntries2(list: List<VitalResponse>, vitalsViewModel: VitalsViewModel): List<Entry>? {

    return if (vitalsViewModel.isGlucoseSelected) {
        getEntries(
            list.filter { it.bloodGlucose?.type == BGEnum.RANDOM.value }, list
        ) {
            if (it.bloodGlucose?.unit?.equals(BGEnum.BG_MMO.value) == true) it.bloodGlucose.value?.toFloat()
                ?.times(18.018f)?.roundToInt()?.toFloat()
            else it.bloodGlucose?.value?.toFloat()
        }
    } else if (vitalsViewModel.isBPSelected) {
        getEntriesFromCVD(
            vitalsViewModel.previousRecords
        ) { it.bpDiastolic.toFloat() }

    } else {
        null
    }

}


private fun getEntries(
    list: List<VitalResponse>,
    bgList: List<VitalResponse>? = null,
    valueSelector: (VitalResponse) -> Float?
): MutableList<Entry> {
    val mutableList: MutableList<Entry> = mutableListOf()

    // Group the list by formatted dates
    val filteredList = list.groupBy { it.appUpdatedDate.formatDateToDayMonth() }
    val vitalGroupedByDate = bgList?.groupBy { it.appUpdatedDate.formatDateToDayMonth() }
        ?: list.groupBy { it.appUpdatedDate.formatDateToDayMonth() }

    // Get a union of all dates from both lists
    (vitalGroupedByDate.keys).distinct()
        .sortedBy { SimpleDateFormat("dd MMM", Locale.getDefault()).parse(it) }.also { allDates ->
            Timber.d("Date: $allDates")
            for (date in allDates) {

                // Find the index of the date in the labels list
                val labelIndex = allDates.indexOf(date)
                if (labelIndex != -1) {
                    Timber.d(
                        "Value: $labelIndex: $date :\n${
                            vitalGroupedByDate[date]?.mapNotNull(
                                valueSelector
                            ).orEmpty()
                        }"
                    )
                    // Calculate the average value for the grouped records

                    val values = if (bgList != null) filteredList[date]?.mapNotNull(valueSelector)
                        .orEmpty() else vitalGroupedByDate[date]?.mapNotNull(valueSelector)
                        .orEmpty()
                    if (values.isNotEmpty()) {
                        // Calculate the average only if values are present
                        val averageValue = values.average().toFloat()
                        // Add the average value as an Entry for the graph
                        mutableList.add(
                            Entry(
                                labelIndex.toFloat(),
                                averageValue.roundToInt().toFloat()
                            )
                        )
                    }
                }
            }

        }

    return mutableList
}

private fun getEntriesFromCVD(
    list: List<CVDResponse>,
    valueSelector: (CVDResponse) -> Float?
): MutableList<Entry> {
    val mutableList: MutableList<Entry> = mutableListOf()

    // Group the list by formatted dates
    val filteredList = list.groupBy { it.createdOn.formatDateToDayMonth() }

    // Get a union of all dates from both lists
    (filteredList.keys).distinct()
        .sortedBy { SimpleDateFormat("dd MMM", Locale.getDefault()).parse(it) }.also { allDates ->
            Timber.d("Date: $allDates")
            for (date in allDates) {

                // Find the index of the date in the labels list
                val labelIndex = allDates.indexOf(date)
                if (labelIndex != -1) {
                    Timber.d(
                        "Value: $labelIndex: $date :\n${
                            filteredList[date]?.mapNotNull(
                                valueSelector
                            ).orEmpty()
                        }"
                    )
                    // Calculate the average value for the grouped records

                    val values: List<Float> = filteredList[date]?.mapNotNull(valueSelector)
                        .orEmpty()
                    if (values.isNotEmpty()) {
                        // Calculate the average only if values are present
                        val averageValue = values.average().toFloat()
                        // Add the average value as an Entry for the graph
                        mutableList.add(
                            Entry(
                                labelIndex.toFloat(),
                                averageValue.roundToInt().toFloat()
                            )
                        )
                    }
                }
            }

        }

    return mutableList
}


private fun getCombinedEntries(
    vitalList: List<VitalResponse>,
    cvdList: List<CVDResponse>,
    vitalValueSelector: (VitalResponse) -> Float?,
    cvdValueSelector: (CVDResponse) -> Float?
): MutableList<Entry> {
    val mutableList: MutableList<Entry> = mutableListOf()

    // Group both lists by formatted dates
    val vitalGroupedByDate = vitalList.groupBy { it.appUpdatedDate.formatDateToDayMonth() }
    val cvdGroupedByDate = cvdList.groupBy { it.createdOn.formatDateToDayMonth() }

    // Get a union of all dates from both lists
    val allDates = (vitalGroupedByDate.keys + cvdGroupedByDate.keys).distinct()
        .sortedBy { SimpleDateFormat("dd MMM", Locale.getDefault()).parse(it) }


    for (date in allDates) {
        val labelIndex = allDates.indexOf(date)
        if (labelIndex != -1) {
            // Collect all values from both VitalResponse and CVDResponse for this date
            val vitalValues = vitalGroupedByDate[date]?.mapNotNull(vitalValueSelector).orEmpty()
            val cvdValues = cvdGroupedByDate[date]?.mapNotNull(cvdValueSelector).orEmpty()

            // Combine all values into a single list
            val combinedValues = vitalValues + cvdValues

            // Calculate the average of the combined values
            if (combinedValues.isNotEmpty()) {
                val finalAverage = combinedValues.average().toFloat()
                mutableList.add(Entry(labelIndex.toFloat(), finalAverage.roundToInt().toFloat()))
            }
        }
    }

    return mutableList
}


@Composable
fun VitalsCardLayout(
    modifier: Modifier = Modifier, vital: VitalResponse, context: Context
) {
    var isViewMore by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                text = vital.appUpdatedDate.convertedDate().convertDateFormat(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                text = vital.practitionerName ?: stringResource(id = R.string.dash),
                style = MaterialTheme.typography.bodySmall
            )
            HorizontalDivider(
                modifier = Modifier.padding(
                    start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp
                )
            )
//            VitalCardItem(
//                title = stringResource(id = R.string.height), value = setHeight(vital)
//            )
//            VitalCardItem(
//                title = stringResource(id = R.string.weight),
//                value = "${vital.weight} ${
//                    stringResource(
//                        id = R.string.kg
//                    ).lowercase()
//                }"
//            )
//            VitalCardItem(
//                title = stringResource(R.string.hear_rate_min),
//                value = vital.heartRate ?: stringResource(id = R.string.dashes)
//            )
//            VitalCardItem(
//                title = stringResource(R.string.respiratory_rate_min),
//                value = vital.respRate ?: stringResource(id = R.string.dashes)
//            )
//            VitalCardItem(
//                title = stringResource(id = R.string.temperature),
//                value = if (vital.temp != null) "${vital.temp} ${if (vital.tempUnit == TemperatureEnum.CELSIUS.value) "°C" else "F"}" else stringResource(
//                    id = R.string.dashes
//                )
//            )
            AnimatedVisibility(visible = isViewMore) {
                ViewMoreItems(vital, context)
                Spacer(modifier = Modifier.height(16.dp))
            }
            Text(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        isViewMore = !isViewMore
                    },
                text = if (isViewMore) stringResource(R.string.view_less) else stringResource(R.string.view_more),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }

    }
}

@Composable
private fun CVDRecordCardLayout(
    modifier: Modifier = Modifier, cvdResponse: CVDResponse
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {

                    Text(
                        text = cvdResponse.createdOn.convertedDate().convertDateFormat(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer

                    )
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                    Text(
                        text = cvdResponse.practitionerName ?: stringResource(id = R.string.dash),
                        style = MaterialTheme.typography.bodySmall
                    )

                }
                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                        .width(8.dp)
                )
                CustomChip(
                    idSelected = false,
                    label = "CVD record",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ) {

                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(
                    start = 16.dp, end = 16.dp, bottom = 8.dp
                )
            )
            VitalCardItem(
                title = stringResource(id = R.string.blood_pressure),
                value = if (cvdResponse.bpDiastolic != 0 && cvdResponse.bpSystolic != 0) "${cvdResponse.bpSystolic}/${cvdResponse.bpDiastolic} ${
                    stringResource(
                        id = R.string.mmhg
                    )
                }" else stringResource(id = R.string.dashes)
            )

            VitalCardItem(
                title = stringResource(id = R.string.total_cholestrol),
                value = if (cvdResponse.cholesterol != null) "${cvdResponse.cholesterol} ${cvdResponse.cholesterolUnit}" else stringResource(
                    id = R.string.dashes
                )
            )
            VitalCardItem(
                title = stringResource(id = R.string.height), value = setCVDHeight(cvdResponse)
            )
            VitalCardItem(
                title = stringResource(id = R.string.weight),
                value = "${cvdResponse.weight} ${
                    stringResource(
                        id = R.string.kg
                    ).lowercase()
                }"
            )

            VitalCardItem(
                title = stringResource(R.string.bmi_label),
                value = cvdResponse.bmi.toString()
            )
            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

@Composable
fun setCVDHeight(cvdResponse: CVDResponse): String {
    return when {
        cvdResponse.heightCm != null -> {
            "${cvdResponse.heightCm} ${stringResource(id = R.string.cm)}"
        }

        cvdResponse.heightFt != null || cvdResponse.heightInch != null -> {
            "${cvdResponse.heightFt ?: ""}.${cvdResponse.heightInch ?: "0"} ${
                stringResource(
                    id = R.string.ft_in
                )
            }"
        }

        else -> {
            stringResource(
                id = R.string.dashes
            )
        }
    }

}

@Composable
private fun ViewMoreItems(vital: VitalResponse, context: Context) {
    Column {
        VitalCardItem(
            title = "${
                stringResource(
                    id = if (vital.bloodGlucose?.type == stringResource(
                            id = R.string.fasting
                        ).lowercase()
                    ) R.string.fasting else R.string.random
                )
            } ${
                stringResource(
                    id = R.string.blood_glucose
                ).lowercase()
            }",
            value = if (vital.bloodGlucose != null) "${vital.bloodGlucose} ${vital.bloodGlucose.unit}"
            else stringResource(R.string.dashes)
        )

    }


}

@Composable
private fun VitalCardItem(modifier: Modifier = Modifier, title: String, value: String) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = modifier
                .testTag("VITAL_TITLE_TEXT")
                .padding(start = 16.dp, top = 8.dp),
            text = title,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            modifier = modifier
                .testTag("VITAL_TITLE_TEXT")
                .padding(start = 16.dp, top = 8.dp),
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun showChipSelection(vitalsViewModel: VitalsViewModel, list: List<VitalResponse>) {
    when {
        getCVDAndVitalListSize(list, vitalsViewModel) > 1 -> {
            vitalsViewModel.isWeightSelected = true
        }

        list.filter { it.bloodGlucose != null && it.bloodGlucose.type == BGEnum.RANDOM.value || it.bloodGlucose?.type == BGEnum.FASTING.value }
            .groupBy { it.appUpdatedDate.formatDateToDayMonth() }.keys.size > 1 -> {
            vitalsViewModel.isGlucoseSelected = true
            vitalsViewModel.isWeightSelected = false
        }
    }
}

@Preview
@Composable
private fun VitalScreenPreview() {
    VitalsScreen(rememberNavController())
}