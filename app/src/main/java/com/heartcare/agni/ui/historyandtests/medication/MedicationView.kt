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
import com.heartcare.agni.ui.common.ExpandableCard
import com.heartcare.agni.ui.historyandtests.HistoryTakingAndTestsViewModel
import java.util.Date

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
                viewModel.medicationList.forEach { _ ->
                    ExpandableCard(
                        createdOn = Date(),
                        practitionerName = "Dr. Anamika Sood",
                        lisOfItems = listOf(
                            "Hypertension",
                            "Hypercholesterolaemia: statin",
                            "Chronic obstructive pulmonary disease (COPD)"
                        ),
                        isBulleted = true,
                        extraInfoComposable = @Composable fun() {
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
                                    text = "Taking medication as prescribed",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}