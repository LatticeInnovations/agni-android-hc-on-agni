package com.heartcare.agni.ui.prescription.filldetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heartcare.agni.R
import com.heartcare.agni.data.local.model.prescription.medication.MedicationResponseWithMedication
import com.heartcare.agni.data.server.model.prescription.prescriptionresponse.Medication
import com.heartcare.agni.ui.common.DropdownComposable
import com.heartcare.agni.ui.prescription.PrescriptionViewModel
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.regex.OnlyNumberRegex
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FillDetailsScreen(
    prescriptionViewModel: PrescriptionViewModel,
    viewModel: FillDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(prescriptionViewModel.checkedActiveIngredient) {
        if (prescriptionViewModel.checkedActiveIngredient.isNotBlank()) {
            viewModel.getMedicationByActiveIngredient(prescriptionViewModel.checkedActiveIngredient) {
                viewModel.medicationSelected = it[0]
                viewModel.medSelected = it[0].name
                viewModel.medUnit = it[0].medUnit
                viewModel.medDoseForm = it[0].doseForm
                viewModel.medFhirId = it[0].medFhirId
            }
            viewModel.reset()
        }
    }
    LaunchedEffect(viewModel.isLaunched) {
        if (prescriptionViewModel.medicationToEdit != null) {
            setMedicationData(viewModel, prescriptionViewModel)
        }
    }
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.fill_details),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("HEADING_TAG")
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        prescriptionViewModel.checkedActiveIngredient = ""
                        prescriptionViewModel.medicationToEdit = null
                        viewModel.reset()
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "CLEAR_ICON")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    TextButton(
                        onClick = {
                            doneButtonClick(viewModel, prescriptionViewModel)
                        },
                        enabled = viewModel.quantityPrescribed().isNotBlank(),
                        modifier = Modifier.testTag("DONE_BTN")
                    ) {
                        Text(text = stringResource(id = R.string.done))
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        var formulationExpanded by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ACTIVE_INGREDIENT_FIELD"),
                            value = prescriptionViewModel.checkedActiveIngredient.replaceFirstChar { char ->
                                char.titlecase(Locale.getDefault())
                            },
                            onValueChange = {
                            },
                            label = {
                                Text(text = stringResource(id = R.string.medicine_name_label))
                            },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "DROP_DOWN_ARROW"
                                )
                            },
                            readOnly = true,
                            interactionSource = remember {
                                MutableInteractionSource()
                            }.also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    interactionSource.interactions.collect { interaction ->
                                        if (interaction is PressInteraction.Release) {
                                            formulationExpanded = !formulationExpanded
                                        }
                                    }
                                }
                            },
                            singleLine = true
                        )
                        DropdownMenu(
                            modifier = Modifier
                                .fillMaxHeight(0.6f)
                                .fillMaxWidth(0.9f)
                                .testTag("ACTIVE_INGREDIENT_DROPDOWN_LIST"),
                            expanded = formulationExpanded,
                            onDismissRequest = { formulationExpanded = !formulationExpanded },
                        ) {
                            prescriptionViewModel.activeIngredientsList.filter { ingredient ->
                                !prescriptionViewModel.selectedActiveIngredientsList.contains(
                                    ingredient
                                )
                            }.forEach { label ->
                                DropdownMenuItem(
                                    onClick = {
                                        formulationExpanded = !formulationExpanded
                                        prescriptionViewModel.checkedActiveIngredient = label
                                        viewModel.reset()
                                    },
                                    text = {
                                        Text(
                                            text = label.replaceFirstChar { char ->
                                                char.titlecase(Locale.getDefault())
                                            },
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                )
                            }
                        }
                    }
                    Text(
                        text = "${viewModel.medicationSelected?.code ?: ""} · ${viewModel.medicationSelected?.categoryName ?: ""} · ${viewModel.medicationSelected?.className ?: ""} ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    DropdownComposable(
                        value = viewModel.selectedBrand,
                        updateValue = { viewModel.selectedBrand = it },
                        label = stringResource(R.string.brand),
                        dropdownList = viewModel.medicationSelected?.brandList ?: listOf(),
                        errorText = "",
                        isMandatory = false,
                        dropdownWeight = 0.9f
                    )
                    FormulationsForm(prescriptionViewModel, viewModel)
                }
            }
        }
    )
}

@Composable
fun FormulationsForm(
    prescriptionViewModel: PrescriptionViewModel,
    viewModel: FillDetailsViewModel
) {
    // quantity per dose
    QuantitySelectorDropdown(
        label = stringResource(R.string.qty_per_dose),
        unit = viewModel.medUnit,
        value = viewModel.quantityPerDose,
        qtyRange = viewModel.qtyRange,
        updateValue = { viewModel.quantityPerDose = it }
    )

    // frequency
    QuantitySelectorDropdown(
        label = stringResource(id = R.string.frequency),
        unit = stringResource(id = R.string.dose_per_day),
        value = viewModel.frequency,
        qtyRange = viewModel.frequencyRange,
        updateValue = { viewModel.frequency = it }
    )

    // timing
    DropdownComposable(
        value = viewModel.timing,
        updateValue = { viewModel.timing = it },
        label = stringResource(id = R.string.timing_optional),
        dropdownList = prescriptionViewModel.medicationDirectionsList.map { it.medicalDosage },
        errorText = "",
        isMandatory = false,
        dropdownWeight = 0.9f
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Duration
        OutlinedTextField(
            modifier = Modifier
                .weight(1f),
            value = viewModel.duration,
            onValueChange = {
                if (it.matches(OnlyNumberRegex.onlyNumbers) && it != "0" && it.length <= 3) viewModel.duration =
                    it
                else if (it.isEmpty()) viewModel.duration = it
                viewModel.isDurationInvalid =
                    viewModel.duration.isNotBlank() && viewModel.duration.toInt() > 180
            },
            label = {
                Text(text = stringResource(id = R.string.duration_days))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            isError = viewModel.isDurationInvalid,
            supportingText = if (viewModel.isDurationInvalid) {
                {
                    Text(text = stringResource(id = R.string.duration_error_msg))
                }
            } else null
        )
        // Quantity prescribed
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = false) { },
            value = viewModel.quantityPrescribed(),
            onValueChange = {},
            label = {
                Text(text = stringResource(id = R.string.quantity_prescribed))
            },
            readOnly = true,
            singleLine = true
        )
    }
    // notes
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("NOTES"),
        value = viewModel.notes,
        onValueChange = {
            if (it.length <= 100) viewModel.notes = it
        },
        label = {
            Text(text = stringResource(id = R.string.notes_optional))
        }
    )
}

@Composable
private fun QuantitySelectorDropdown(
    label: String,
    unit: String,
    value: String,
    qtyRange: List<String>,
    updateValue: (String) -> Unit
) {
    Column {
        var quantityExpanded by remember {
            mutableStateOf(false)
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("QUANTITY_PER_DOSE"),
            value = unit,
            onValueChange = {},
            label = {
                Text(text = label)
            },
            leadingIcon = {
                Row(
                    modifier = Modifier.fillMaxWidth(0.4f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = value)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "DOWN_ARROW")
                }
            },
            readOnly = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            quantityExpanded = !quantityExpanded
                        }
                    }
                }
            },
            singleLine = true
        )
        DropdownMenu(
            modifier = Modifier
                .fillMaxHeight(0.3f),
            expanded = quantityExpanded,
            onDismissRequest = { quantityExpanded = !quantityExpanded },
        ) {
            qtyRange.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        quantityExpanded = !quantityExpanded
                        updateValue(label)
                    },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }
    }
}

private fun setMedicationData(
    viewModel: FillDetailsViewModel,
    prescriptionViewModel: PrescriptionViewModel
) {
    viewModel.getMedicationByActiveIngredient(prescriptionViewModel.medicationToEdit!!.activeIngredient) {
        viewModel.medicationSelected = it[0]
    }
    viewModel.medSelected = prescriptionViewModel.medicationToEdit!!.medName
    viewModel.medUnit = prescriptionViewModel.medicationToEdit!!.medUnit
    viewModel.medDoseForm = prescriptionViewModel.medicationToEdit!!.medication.doseForm
    viewModel.quantityPerDose =
        prescriptionViewModel.medicationToEdit!!.medication.qtyPerDose.toString()
    viewModel.frequency =
        prescriptionViewModel.medicationToEdit!!.medication.frequency.toString()
    viewModel.notes = prescriptionViewModel.medicationToEdit!!.medication.note ?: ""
    viewModel.medFhirId = prescriptionViewModel.medicationToEdit!!.medication.medFhirId
    viewModel.timing = prescriptionViewModel.medicationToEdit!!.medication.timing ?: ""
    viewModel.duration =
        prescriptionViewModel.medicationToEdit!!.medication.duration.toString()
}

private fun doneButtonClick(
    viewModel: FillDetailsViewModel,
    prescriptionViewModel: PrescriptionViewModel
) {
    if (prescriptionViewModel.medicationToEdit != null) {
        prescriptionViewModel.selectedActiveIngredientsList -= listOf(
            prescriptionViewModel.medicationToEdit!!.activeIngredient
        ).toSet()
        prescriptionViewModel.medicationsResponseWithMedicationList -= listOf(
            prescriptionViewModel.medicationToEdit!!
        ).toSet()
    }
    prescriptionViewModel.selectedActiveIngredientsList += listOf(
        prescriptionViewModel.checkedActiveIngredient
    )
    prescriptionViewModel.medicationsResponseWithMedicationList += listOf(
        MedicationResponseWithMedication(
            medName = viewModel.medSelected,
            medUnit = viewModel.medUnit,
            activeIngredient = prescriptionViewModel.checkedActiveIngredient,
            medication = Medication(
                duration = viewModel.duration.toInt(),
                frequency = viewModel.frequency.toInt(),
                note = viewModel.notes.trim(),
                qtyPerDose = viewModel.quantityPerDose.toDouble(),
                qtyPrescribed = viewModel.quantityPrescribed().toDouble(),
                timing = viewModel.timing,
                doseForm = viewModel.medDoseForm,
                medFhirId = viewModel.medFhirId,
                medReqFhirId = null,
                medReqUuid = UUIDBuilder.generateUUID(),
                brandName = viewModel.selectedBrand.ifBlank { null },
                doseFormCode = null
            )
        )
    )
    prescriptionViewModel.checkedActiveIngredient = ""
    prescriptionViewModel.medicationToEdit = null
    viewModel.reset()
}