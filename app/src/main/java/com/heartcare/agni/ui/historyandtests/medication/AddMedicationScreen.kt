package com.heartcare.agni.ui.historyandtests.medication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.MedicationAdherence.Companion.getAdherenceList
import com.heartcare.agni.data.local.enums.MedicationEnum
import com.heartcare.agni.data.local.enums.MedicationEnum.Companion.getMedicationList
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.common.CheckBoxRow
import com.heartcare.agni.ui.common.OtherField
import com.heartcare.agni.ui.theme.Black
import com.heartcare.agni.ui.theme.White
import com.heartcare.agni.utils.constants.NavControllerConstants
import com.heartcare.agni.utils.constants.NavControllerConstants.MEDICATION_SAVED
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(
    navController: NavController,
    viewModel: AddMedicationViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            val handle = navController.previousBackStackEntry?.savedStateHandle
            viewModel.selectedCampaignId = handle?.get<String>(NavControllerConstants.CAMPAIGN_ID)
            handle?.get<PatientResponse>(PATIENT)?.let {
                    viewModel.patient = it
                    viewModel.getLastHistoryMedication(it.id)
                }
            viewModel.isLaunched = true
        }
    }
    Scaffold(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.add_medication),
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
                modifier = Modifier.padding(paddingValues)
            ) {
                Text(
                    text = stringResource(R.string.medication_header_text),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
                MedicationListComposable(viewModel)
            }
        }
    )
    Box(
        modifier = if (!viewModel.isAdherenceExpanded) Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0f))
        else Modifier
            .navigationBarsPadding()
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            .clickable(enabled = false) { },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.imePadding()
        ) {
            AnimatedVisibility(
                visible = viewModel.showAdherenceCard
            ) {
                AdherenceComposable(viewModel)
            }
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
                        // save medication
                        viewModel.addHistoryMedication {
                            coroutineScope.launch {
                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                    MEDICATION_SAVED,
                                    true
                                )
                                navController.navigateUp()
                            }
                        }
                    },
                    enabled = viewModel.isValid(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

@Composable
private fun MedicationListComposable(
    viewModel: AddMedicationViewModel
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = if (viewModel.showAdherenceCard) 140.dp else 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        getMedicationList().forEach { medication ->
            val isChecked = medication in viewModel.selectedMedication

            CheckBoxRow(
                isChecked = isChecked,
                onCheckedChange = { checked ->
                    updateList(checked, medication, viewModel)
                },
                label = medication
            )

            OtherField(
                isVisible = medication == MedicationEnum.SIDE_EFFECTS.display && isChecked,
                value = viewModel.sideEffectsField,
                isError = viewModel.isSideEffectsFieldError,
                errorMessage = stringResource(R.string.side_effects_required),
                maxLength = viewModel.maxOtherFieldLength,
                onValueChange = {
                    viewModel.sideEffectsField = it
                    viewModel.isSideEffectsFieldError = it.isBlank()
                }
            )

            OtherField(
                isVisible = medication == MedicationEnum.OTHERS.display && isChecked,
                value = viewModel.otherField,
                isError = viewModel.isOtherFieldError,
                maxLength = viewModel.maxOtherFieldLength,
                onValueChange = {
                    viewModel.otherField = it
                    viewModel.isOtherFieldError = it.isBlank()
                }
            )
        }
    }
}

fun updateList(
    checked: Boolean,
    medication: String,
    viewModel: AddMedicationViewModel
) {

    if (checked) {
        viewModel.selectedMedication += medication
    } else {
        viewModel.selectedMedication -= medication
        when (medication) {
            MedicationEnum.SIDE_EFFECTS.display -> {
                viewModel.sideEffectsField = ""
                viewModel.isSideEffectsFieldError = false
            }

            MedicationEnum.OTHERS.display -> {
                viewModel.otherField = ""
                viewModel.isOtherFieldError = false
            }
        }
    }
    if (viewModel.selectedMedication.any { it != MedicationEnum.SIDE_EFFECTS.display }) {
        if (!viewModel.showAdherenceCard) {
            viewModel.showAdherenceCard = true
            viewModel.isAdherenceExpanded = true
        }
    } else {
        viewModel.showAdherenceCard = false
        viewModel.adherence = ""
    }
}

@Composable
private fun AdherenceComposable(
    viewModel: AddMedicationViewModel
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .background(
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                color = if (isSystemInDarkTheme()) Black else White
            )
    ) {
        Row(
            modifier = Modifier
                .padding(top = 8.dp, start = 12.dp, end = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.adherence),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.isAdherenceExpanded = !viewModel.isAdherenceExpanded
                }
            ) {
                Icon(
                    imageVector = if (viewModel.isAdherenceExpanded) Icons.Default.KeyboardArrowDown
                    else Icons.Default.KeyboardArrowUp,
                    contentDescription = null
                )
            }
        }
        AnimatedVisibility(
            visible = viewModel.isAdherenceExpanded
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                getAdherenceList().forEach { adherence ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    viewModel.adherence = adherence
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = adherence == viewModel.adherence,
                            onClick = {
                                viewModel.adherence = adherence
                            }
                        )
                        Text(
                            text = adherence,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}