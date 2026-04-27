package com.heartcare.agni.ui.vitalsscreen.addvitals

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.SymbolEnum.Companion.getSymbolList
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.common.CustomTextField
import com.heartcare.agni.ui.common.CustomTextFieldWithLength
import com.heartcare.agni.ui.common.DropdownComposable
import com.heartcare.agni.ui.common.SingleFieldWithSwapUnit
import com.heartcare.agni.ui.theme.Black
import com.heartcare.agni.ui.theme.White
import com.heartcare.agni.ui.vitalsscreen.components.CustomChip
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.VitalConstants.VITAL_UPDATE_OR_ADD
import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbers
import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbersWithDecimal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AddVitalsScreen(
    navController: NavController,
    viewModel: AddVitalsViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            val handle = navController.previousBackStackEntry?.savedStateHandle
            handle?.get<PatientResponse>(PATIENT)?.let {
                viewModel.patient = it
                viewModel.selectedCampaignId = handle.get<String>(com.heartcare.agni.utils.constants.NavControllerConstants.CAMPAIGN_ID)
                viewModel.getTodayVital(it.id)
            }
            viewModel.isLaunched = true
        }
    }
    AddVitals(navController, viewModel)

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddVitals(navController: NavController, viewModel: AddVitalsViewModel) {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val sections: List<@Composable () -> Unit> = listOf(
        { BloodGlucoseCard(viewModel) },
        { FootExaminationField(viewModel) },
        { EyeExaminationField(viewModel) },
        { AbdominalCircumferenceField(viewModel) },
        { HipCircumferenceField(viewModel) },
        { HbA1cField(viewModel) },
        { SerumCreatinineField(viewModel) },
        { SerumPotassiumField(viewModel) },
        { UrineProteinField(viewModel) },
        { UrineKetonesField(viewModel) },
        { OtherField(viewModel) }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                title = {
                    Text(
                        text = if (viewModel.todayVital == null) stringResource(R.string.add_vitals) else stringResource(
                            R.string.update_vital
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag(stringResource(R.string.add_vital_title_text))
                    )
                },
                actions = {
                    IconButton(onClick = {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            VITAL_UPDATE_OR_ADD,
                            ""
                        )
                        navController.navigateUp()
                    }) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = stringResource(R.string.back_icon)
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(
                        color = if (isSystemInDarkTheme()) Black else White
                    )
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    sections.forEachIndexed { index, section ->
                        section()
                        if (index != sections.lastIndex) {
                            HorizontalDivider(
                                thickness = 4.dp,
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            SaveButton(navController, viewModel, coroutineScope, context)
        }
    )
}


@Composable
private fun BloodGlucoseCard(viewModel: AddVitalsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.blood_glucose),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        BgChip(viewModel)

        SingleFieldWithSwapUnit(
            value = viewModel.bloodGlucose,
            onValueChange = { value ->
                updateBloodGlucose(value, viewModel)
            },
            isError = viewModel.bloodGlucoseError,
            errorMessage = if (viewModel.selectedBloodGlucoseUnit == 0)
                stringResource(R.string.value_in_range_with_unit, "20", "600", "mg/dL")
            else stringResource(R.string.value_in_range_with_unit, "1.1", "33.4", "mmol/L"),
            label = "${stringResource(id = if (viewModel.bgRandomChipSelected) R.string.random else R.string.fasting)} ${
                stringResource(
                    id = R.string.blood_glucose
                ).lowercase()
            }",
            selectedUnit = viewModel.bloodGlucoseUnits[viewModel.selectedBloodGlucoseUnit],
            onSwapUnit = {
                viewModel.selectedBloodGlucoseUnit =
                    1 - viewModel.selectedBloodGlucoseUnit
                viewModel.bloodGlucoseError = false
                viewModel.bloodGlucose = ""
            },
            weight = 7f
        )
    }
}

private fun updateBloodGlucose(
    value: String,
    viewModel: AddVitalsViewModel
) {
    if (viewModel.selectedBloodGlucoseUnit == 0) {
        if (value.isBlank() || (value.matches(onlyNumbers) && value.length < 4)) {
            viewModel.bloodGlucose = value
            viewModel.bloodGlucoseError = viewModel.bloodGlucose.isNotBlank() &&
                    viewModel.bloodGlucose.toInt() !in 20..600
        }
    } else {
        if (value.isBlank() || (value.matches(onlyNumbersWithDecimal) && value.length < 5)) {
            viewModel.bloodGlucose = value
            viewModel.bloodGlucoseError = viewModel.bloodGlucose.isNotBlank() &&
                    viewModel.bloodGlucose.toDouble() !in 1.1..33.4
        }
    }
}

@Composable
private fun BgChip(viewModel: AddVitalsViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomChip(
            idSelected = viewModel.bgRandomChipSelected, label = stringResource(R.string.random)
        ) {
            viewModel.bgRandomChipSelected = !viewModel.bgRandomChipSelected
            if (viewModel.bgRandomChipSelected) {
                viewModel.bgFastingChipSelected = false
            }
        }
        CustomChip(
            idSelected = viewModel.bgFastingChipSelected,
            label = stringResource(R.string.fasting)
        ) {
            viewModel.bgFastingChipSelected = !viewModel.bgFastingChipSelected
            if (viewModel.bgFastingChipSelected) viewModel.bgRandomChipSelected = false

        }
    }
}

@Composable
private fun FootExaminationField(
    viewModel: AddVitalsViewModel
) {
    CustomTextFieldWithLength(
        value = viewModel.footExamination,
        label = stringResource(R.string.foot_examination),
        weight = 1f,
        maxLength = 200,
        isError = false,
        error = "",
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Sentences,
        singleLine = false,
        updateValue = {
            viewModel.footExamination = it
        },
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun EyeExaminationField(
    viewModel: AddVitalsViewModel
) {
    CustomTextFieldWithLength(
        value = viewModel.eyeExamination,
        label = stringResource(R.string.eye_examination),
        weight = 1f,
        maxLength = 200,
        isError = false,
        error = "",
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Sentences,
        singleLine = false,
        updateValue = {
            viewModel.eyeExamination = it
        },
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun AbdominalCircumferenceField(
    viewModel: AddVitalsViewModel
) {
    SingleFieldWithSwapUnit(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        value = viewModel.abdominalCircumference,
        onValueChange = { value ->
            updateAbdominalCircumference(value, viewModel)
        },
        isError = viewModel.abdominalCircumferenceError,
        errorMessage = if (viewModel.abdominalCircumferenceError) {
            if (viewModel.selectedAbdominalCircumferenceUnit == 0)
                stringResource(R.string.value_in_range_with_unit, "25.0", "250.0", "cm")
            else
                stringResource(R.string.value_in_range_with_unit, "9.8", "98.5", "inch")
        } else null,
        label = stringResource(R.string.abdominal_circumference),
        selectedUnit = viewModel.abdominalCircumferenceUnits[viewModel.selectedAbdominalCircumferenceUnit],
        onSwapUnit = {
            viewModel.selectedAbdominalCircumferenceUnit =
                1 - viewModel.selectedAbdominalCircumferenceUnit
            viewModel.abdominalCircumferenceError = false
            viewModel.abdominalCircumference = ""
        },
        weight = 9f
    )
}

private fun updateAbdominalCircumference(
    value: String,
    viewModel: AddVitalsViewModel
) {
    if (viewModel.selectedAbdominalCircumferenceUnit == 0) {
        if (value.isBlank() || (value.matches(onlyNumbersWithDecimal) && value.length < 6)) {
            viewModel.abdominalCircumference = value
            viewModel.abdominalCircumferenceError =
                viewModel.abdominalCircumference.isNotBlank() &&
                        viewModel.abdominalCircumference.toDouble() !in 25.0..250.0
        }
    } else {
        if (value.isBlank() || (value.matches(onlyNumbersWithDecimal) && value.length < 5)) {
            viewModel.abdominalCircumference = value
            viewModel.abdominalCircumferenceError =
                viewModel.abdominalCircumference.isNotBlank() &&
                        viewModel.abdominalCircumference.toDouble() !in 9.8..98.5
        }
    }
}

@Composable
private fun HipCircumferenceField(
    viewModel: AddVitalsViewModel
) {
    SingleFieldWithSwapUnit(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        value = viewModel.hipCircumference,
        onValueChange = { value ->
            updateHipCircumference(value, viewModel)
        },
        isError = viewModel.hipCircumferenceError,
        errorMessage = if (viewModel.hipCircumferenceError) {
            if (viewModel.selectedHipCircumferenceUnit == 0)
                stringResource(R.string.value_in_range_with_unit, "25.0", "250.0", "cm")
            else
                stringResource(R.string.value_in_range_with_unit, "9.8", "98.5", "inch")
        } else null,
        label = stringResource(R.string.hip_circumference),
        selectedUnit = viewModel.hipCircumferenceUnits[viewModel.selectedHipCircumferenceUnit],
        onSwapUnit = {
            viewModel.selectedHipCircumferenceUnit =
                1 - viewModel.selectedHipCircumferenceUnit
            viewModel.hipCircumferenceError = false
            viewModel.hipCircumference = ""
        },
        weight = 9f
    )
}

private fun updateHipCircumference(
    value: String,
    viewModel: AddVitalsViewModel
) {
    if (viewModel.selectedHipCircumferenceUnit == 0) {
        if (value.isBlank() || (value.matches(onlyNumbersWithDecimal) && value.length < 6)) {
            viewModel.hipCircumference = value
            viewModel.hipCircumferenceError =
                viewModel.hipCircumference.isNotBlank() &&
                        viewModel.hipCircumference.toDouble() !in 25.0..250.0
        }
    } else {
        if (value.isBlank() || (value.matches(onlyNumbersWithDecimal) && value.length < 5)) {
            viewModel.hipCircumference = value
            viewModel.hipCircumferenceError =
                viewModel.hipCircumference.isNotBlank() &&
                        viewModel.hipCircumference.toDouble() !in 9.8..98.5
        }
    }
}

@Composable
private fun HbA1cField(
    viewModel: AddVitalsViewModel
) {
    OutlinedTextField(
        value = viewModel.hbA1c,
        onValueChange = { value ->
            if (value.isBlank() || (value.matches(onlyNumbersWithDecimal) && value.length < 5)) {
                viewModel.hbA1c = value
                viewModel.hbA1cError =
                    viewModel.hbA1c.isNotBlank() &&
                            viewModel.hbA1c.toDouble() !in 2.0..20.0
            }
        },
        label = {
            Text(stringResource(R.string.HbA1c))
        },
        isError = viewModel.hbA1cError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        supportingText = if (viewModel.hbA1cError) {
            {
                Text(stringResource(R.string.value_in_range_with_unit, "2.0", "20.0", "%"))
            }
        } else null,
        trailingIcon = {
            Text("%")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}

@Composable
private fun SerumCreatinineField(
    viewModel: AddVitalsViewModel
) {
    SingleFieldWithSwapUnit(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        value = viewModel.serumCreatinine,
        onValueChange = { value ->
            updateSerumCreatinine(value, viewModel)
        },
        isError = viewModel.serumCreatinineError,
        errorMessage = if (viewModel.serumCreatinineError) {
            if (viewModel.selectedSerumCreatinineUnit == 0)
                stringResource(R.string.value_in_range_with_unit, "0.0", "50.0", "mg/dL")
            else
                stringResource(R.string.value_in_range_with_unit, "0", "4420", "µmol/L")
        } else null,
        label = stringResource(R.string.serum_creatinine),
        selectedUnit = viewModel.serumCreatinineUnits[viewModel.selectedSerumCreatinineUnit],
        onSwapUnit = {
            viewModel.selectedSerumCreatinineUnit =
                1 - viewModel.selectedSerumCreatinineUnit
            viewModel.serumCreatinineError = false
            viewModel.serumCreatinine = ""
        },
        weight = 7f
    )
}

private fun updateSerumCreatinine(
    value: String,
    viewModel: AddVitalsViewModel
) {
    if (viewModel.selectedSerumCreatinineUnit == 0) {
        if (value.isBlank() || (value.matches(onlyNumbersWithDecimal) && value.length < 5)) {
            viewModel.serumCreatinine = value
            viewModel.serumCreatinineError =
                viewModel.serumCreatinine.isNotBlank() &&
                        viewModel.serumCreatinine.toDouble() !in 0.0..50.0
        }
    } else {
        if (value.isBlank() || (value.matches(onlyNumbers) && value.length < 5)) {
            viewModel.serumCreatinine = value
            viewModel.serumCreatinineError =
                viewModel.serumCreatinine.isNotBlank() &&
                        viewModel.serumCreatinine.toInt() !in 0..4420
        }
    }
}

@Composable
private fun SerumPotassiumField(
    viewModel: AddVitalsViewModel
) {
    SingleFieldWithSwapUnit(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        value = viewModel.serumPotassium,
        onValueChange = { value ->
            if (value.isBlank() || (value.matches(onlyNumbersWithDecimal) && value.length < 5)) {
                viewModel.serumPotassium = value
                viewModel.serumPotassiumError =
                    viewModel.serumPotassium.isNotBlank() &&
                            viewModel.serumPotassium.toDouble() !in 0.0..50.0
            }
        },
        isError = viewModel.serumPotassiumError,
        errorMessage = if (viewModel.serumPotassiumError) {
            if (viewModel.selectedSerumPotassiumUnit == 0)
                stringResource(R.string.value_in_range_with_unit, "0.0", "50.0", "mEq/L")
            else
                stringResource(R.string.value_in_range_with_unit, "0.0", "50.0", "µmol/L")
        } else null,
        label = stringResource(R.string.serum_potassium),
        selectedUnit = viewModel.serumPotassiumUnits[viewModel.selectedSerumPotassiumUnit],
        onSwapUnit = {
            viewModel.selectedSerumPotassiumUnit =
                1 - viewModel.selectedSerumPotassiumUnit
            viewModel.serumPotassiumError = false
            viewModel.serumPotassium = ""
        },
        weight = 7f
    )
}

@Composable
private fun UrineProteinField(
    viewModel: AddVitalsViewModel
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        DropdownComposable(
            value = viewModel.urineProtein,
            updateValue = { viewModel.urineProtein = it },
            label = stringResource(R.string.urine_protein),
            dropdownList = getSymbolList(),
            errorText = "",
            isMandatory = false
        )
    }
}

@Composable
private fun UrineKetonesField(
    viewModel: AddVitalsViewModel
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        DropdownComposable(
            value = viewModel.urineKetones,
            updateValue = { viewModel.urineKetones = it },
            label = stringResource(R.string.urine_ketones),
            dropdownList = getSymbolList(),
            errorText = "",
            isMandatory = false
        )
    }
}

@Composable
private fun OtherField(
    viewModel: AddVitalsViewModel
) {
    CustomTextField(
        value = viewModel.other,
        label = stringResource(R.string.other),
        weight = 1f,
        maxLength = 200,
        isError = false,
        error = "",
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Sentences,
        singleLine = false,
        updateValue = {
            viewModel.other = it
        },
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun SaveButton(
    navController: NavController,
    viewModel: AddVitalsViewModel,
    coroutineScope: CoroutineScope,
    context: Context
) {
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
                enabled = viewModel.isValid(),
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(), onClick = {
                    if (!viewModel.isButtonClicked) {
                        handleNavigate(
                            viewModel, coroutineScope, navController, context
                        )
                        viewModel.isButtonClicked = true
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

private fun handleNavigate(
    viewModel: AddVitalsViewModel,
    coroutineScope: CoroutineScope,
    navController: NavController,
    context: Context
) {
    viewModel.insertVital {
        coroutineScope.launch {
            navController.previousBackStackEntry?.savedStateHandle?.set(
                VITAL_UPDATE_OR_ADD,
                if (viewModel.todayVital == null) context.getString(R.string.vitals_added_successfully)
                else context.getString(R.string.vitals_update_successfully)
            )
            navController.navigateUp()
        }
    }
}