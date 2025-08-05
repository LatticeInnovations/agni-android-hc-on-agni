package com.heartcare.agni.ui.historyandtests.allergy

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
fun AllergyView(
    viewModel: HistoryTakingAndTestsViewModel
) {
    if (viewModel.allergyList.isEmpty()) {
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
                viewModel.allergyList.forEach { allergy ->
                    ExpandableCard(
                        createdOn = allergy.appUpdatedDate,
                        practitionerName = allergy.practitionerName!!,
                        listOfItems = allergy.allergy?.let { listOf(it) } ?: emptyList(),
                        isBulleted = false
                    )
                }
            }
        }
    }
}