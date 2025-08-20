package com.heartcare.agni.ui.prescription.previousprescription

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.model.prescription.medication.MedicationResponseWithMedication
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.heartcare.agni.data.server.model.prescription.prescriptionresponse.Medication
import com.heartcare.agni.ui.prescription.PrescriptionViewModel
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toPrescriptionDate
import com.heartcare.agni.utils.converters.responseconverter.medication.MedicationInfoConverter.getMedInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun PreviousPrescriptionsScreen(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    viewModel: PrescriptionViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (viewModel.previousPrescriptionList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.no_previous_prescription))
            }
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                viewModel.previousPrescriptionList.forEachIndexed { index, previousPrescription ->
                    previousPrescription?.let { prescription ->
                        PrescriptionCard(
                            viewModel,
                            prescription,
                            index == 0,
                            snackbarHostState,
                            coroutineScope
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PrescriptionCard(
    viewModel: PrescriptionViewModel,
    prescription: String,
    isLatest: Boolean,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    val context = LocalContext.current
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Rotation state of expand icon button",
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { expanded = !expanded }
                    .testTag("PREVIOUS_PRESCRIPTION_TITLE_ROW"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Date().toPrescriptionDate(),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "DOWN_ARROW",
                    modifier = Modifier.rotate(rotationState)
                )
            }
            AnimatedVisibility(
                visible = expanded
            ) {
                Column(
                    modifier = Modifier.padding(top = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    listOf(
                        "", ""
                    ).forEach { _ ->
                        MedicineDetails(
                            medName = "Metformin 650",
                            brandName = "Cipla",
                            details = "1 ml OD, Before food\n" +
                                    "Duration : 7 days , Qty : 7 \n" +
                                    "Notes : Take rest "
                        )
                    }
                    if (isLatest) {
                        TextButton(
                            onClick = {
                                // re prescribe
                                /*viewModel.appointmentResponseLocal.run {
                                    when (this?.status) {
                                        AppointmentStatusEnum.ARRIVED.value, AppointmentStatusEnum.WALK_IN.value -> {
                                            saveRePrescription(
                                                context,
                                                viewModel,
                                                prescription,
                                                coroutineScope,
                                                snackbarHostState
                                            )
                                        }

                                        AppointmentStatusEnum.IN_PROGRESS.value, AppointmentStatusEnum.COMPLETED.value -> {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(
                                                    context.getString(R.string.prescription_already_exists_for_today)
                                                )
                                            }
                                        }

                                        else -> coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                context.getString(R.string.please_add_patient_to_queue)
                                            )
                                        }
                                    }
                                }*/
                            },
                            modifier = Modifier
                                .align(Alignment.End)
                                .testTag("RE_PRESCRIBE_BTN")
                        ) {
                            Text(text = stringResource(R.string.re_precribe))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedicineDetails(
    medName: String,
    brandName: String? = null,
    details: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = medName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        brandName?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = details,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun saveRePrescription(
    context: Context,
    viewModel: PrescriptionViewModel,
    prescription: PrescriptionAndMedicineRelation,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    viewModel.medicationsResponseWithMedicationList = emptyList()
    viewModel.selectedActiveIngredientsList = emptyList()
    prescription.prescriptionDirectionAndMedicineView.forEach { directionAndMedication ->
        viewModel.selectedActiveIngredientsList += listOf(
            directionAndMedication.medicationEntity.activeIngredient
        )
        viewModel.medicationsResponseWithMedicationList += listOf(
            MedicationResponseWithMedication(
                activeIngredient = directionAndMedication.medicationEntity.activeIngredient,
                medName = directionAndMedication.medicationEntity.medName,
                medUnit = directionAndMedication.medicationEntity.medUnit,
                medication = Medication(
                    doseForm = directionAndMedication.medicationEntity.doseForm,
                    duration = directionAndMedication.prescriptionDirectionsEntity.duration,
                    qtyPerDose = directionAndMedication.prescriptionDirectionsEntity.qtyPerDose,
                    frequency = directionAndMedication.prescriptionDirectionsEntity.frequency,
                    medFhirId = directionAndMedication.medicationEntity.medFhirId,
                    note = directionAndMedication.prescriptionDirectionsEntity.note,
                    timing = directionAndMedication.prescriptionDirectionsEntity.timing,
                    qtyPrescribed = directionAndMedication.prescriptionDirectionsEntity.qtyPrescribed,
                    medReqUuid = UUIDBuilder.generateUUID(),
                    medReqFhirId = null
                )
            )
        )
    }
    viewModel.bottomNavExpanded = false
    coroutineScope.launch {
        snackbarHostState.showSnackbar(
            message = context.getString(R.string.re_prescribed_successfully)
        )
    }
}