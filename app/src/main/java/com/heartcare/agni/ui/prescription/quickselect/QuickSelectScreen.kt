package com.heartcare.agni.ui.prescription.quickselect

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heartcare.agni.data.server.model.prescription.medication.MedicationResponse
import com.heartcare.agni.ui.prescription.PrescriptionViewModel

@Composable
fun QuickSelectScreen(viewModel: PrescriptionViewModel = hiltViewModel()) {
    key(viewModel.selectedMedicationsList) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
                .padding(bottom = if (viewModel.selectedMedicationsList.isNotEmpty()) 60.dp else 0.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.medicationsList.forEach { medicationResponse ->
                CompoundRow(medication = medicationResponse, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CompoundRow(medication: MedicationResponse, viewModel: PrescriptionViewModel) {
    val checkedState =
        remember { mutableStateOf(viewModel.selectedMedicationsList.contains(medication)) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                updateList(!checkedState.value, viewModel, medication)
            },
    ) {
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                updateList(it, viewModel, medication)
            },
        )
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = medication.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${medication.code} · ${medication.categoryName} · ${medication.className} ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun updateList(
    condition: Boolean,
    viewModel: PrescriptionViewModel,
    activeIngredient: MedicationResponse
) {
    if (condition) {
        if (viewModel.selectedMedicationsList.size < 10) {
            viewModel.checkedMedication = activeIngredient
        }
    } else {
        viewModel.selectedMedicationsList -= listOf(activeIngredient).toSet()
        viewModel.medicationsResponseWithMedicationList.forEach { medication ->
            if (medication.medication.medFhirId == activeIngredient.medFhirId) {
                viewModel.medicationsResponseWithMedicationList -= listOf(medication).toSet()
            }
        }
    }
}
