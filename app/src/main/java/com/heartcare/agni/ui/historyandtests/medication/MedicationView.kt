package com.heartcare.agni.ui.historyandtests.medication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.MedicationAdherence.Companion.getAdherenceDisplay
import com.heartcare.agni.data.local.enums.MedicationEnum
import com.heartcare.agni.data.local.enums.MedicationEnum.Companion.getMedicationFromCode
import com.heartcare.agni.data.server.model.historymedication.HistoryMedicationResponse
import com.heartcare.agni.ui.common.ExpandableCard
import com.heartcare.agni.ui.historyandtests.HistoryTakingAndTestsViewModel

@Composable
fun MedicationView(
    viewModel: HistoryTakingAndTestsViewModel
) {
    if (viewModel.medicationList.isEmpty()) {
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                viewModel.medicationList.forEach { historyMedication ->
                    ExpandableCard(
                        createdOn = historyMedication.appUpdatedDate,
                        practitionerName = historyMedication.practitionerName!!,
                        listOfItems = getListOfHistoryMedication(historyMedication),
                        isBulleted = true,
                        extraInfoComposable = historyMedication.adherence?.let {
                            @Composable {
                                Column(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.adherence),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = getAdherenceDisplay(it),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun getListOfHistoryMedication(
    historyMedication: HistoryMedicationResponse
): List<String> {
    return mutableListOf<String>().apply {
        addAll(historyMedication.medicinePrescribed.map {
            getMedicationFromCode(it) + if (it == MedicationEnum.OTHERS.code) ": ${historyMedication.medicinePrescribedOthers}" else ""
        })
        if (historyMedication.hasSideEffect) {
            add("${MedicationEnum.SIDE_EFFECTS.display}: ${historyMedication.sideEffects}")
        }
    }
}