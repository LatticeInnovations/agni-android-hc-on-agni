package com.heartcare.agni.ui.sitescreendashboard.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heartcare.agni.R
import com.heartcare.agni.ui.common.SelectableRadioCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadReportBottomSheet(
    onDismissRequest: () -> Unit,
    onDownloadClick: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedOption by remember { mutableStateOf("High-Level") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding( 16.dp)
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.download_report),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                IconButton(onClick = onDismissRequest) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cancel))
                }
            }



            val highLevel      = stringResource(R.string.report_high_level)
            val clinicalAction = stringResource(R.string.report_clinical_action)
            val riskRoster     = stringResource(R.string.report_risk_roster)

            if (selectedOption.isEmpty()) selectedOption = highLevel

            SelectableRadioCard(
                title    = highLevel,
                subtitle = stringResource(R.string.report_high_level_desc),
                selected = selectedOption == highLevel,
                onClick  = { selectedOption = highLevel },
                isShowBorderAndBG = false
            )
            Spacer(modifier = Modifier.height(8.dp))

            SelectableRadioCard(
                title    = clinicalAction,
                subtitle = stringResource(R.string.report_clinical_action_desc),
                selected = selectedOption == clinicalAction,
                onClick  = { selectedOption = clinicalAction },
                isShowBorderAndBG = false

            )
            Spacer(modifier = Modifier.height(8.dp))

            SelectableRadioCard(
                title    = riskRoster,
                subtitle = stringResource(R.string.report_risk_roster_desc),
                selected = selectedOption == riskRoster,
                onClick  = { selectedOption = riskRoster },
                isShowBorderAndBG = false

            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    onDownloadClick(selectedOption)
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.download), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}