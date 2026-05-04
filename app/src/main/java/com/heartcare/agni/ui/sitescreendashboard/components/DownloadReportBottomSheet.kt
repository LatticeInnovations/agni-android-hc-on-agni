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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heartcare.agni.R
import com.heartcare.agni.ui.common.SelectableRadioCard
import com.heartcare.agni.ui.sitescreendashboard.ReportsViewModel
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toMMMMddyyyy
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toMMMddyyyyDateRange
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toddMMyy
import com.heartcare.agni.utils.pdf.PDFHelper.generatePdf
import com.heartcare.agni.utils.pdf.reports.ClinicalActionReportPDF.getClinicalActionReportHTML
import com.heartcare.agni.utils.pdf.reports.HighLevelScreeningReportPDF.getHighLevelScreeningReportHTML
import com.heartcare.agni.utils.pdf.reports.RiskRosterReportPDF.getRiskRosterReportHTML
import kotlinx.coroutines.launch
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadReportBottomSheet(
    onDismissRequest: () -> Unit,
    reportsViewModel: ReportsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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
                .padding(16.dp)
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
                    var html: String? = null
                    when (selectedOption) {
                        highLevel -> {
                            html = getHighLevelScreeningReportHTML(
                                getMetaData(reportsViewModel),
                                reportsViewModel.currentState,
                                getFooterData(reportsViewModel)
                            )
                        }
                        clinicalAction -> {
                            html = getClinicalActionReportHTML(
                                getMetaData(reportsViewModel),
                                reportsViewModel.currentState,
                                getFooterData(reportsViewModel)
                            )
                        }
                        riskRoster -> {
                            html = getRiskRosterReportHTML(
                                getMetaData(reportsViewModel),
                                reportsViewModel.currentState,
                                getFooterData(reportsViewModel)
                            )
                        }
                    }
                    if (!html.isNullOrBlank()) {
                        coroutineScope.launch {
                            val fileName = "${getFooterData(reportsViewModel)}-$selectedOption-${Date().toddMMyy()}"
                            generatePdf(context, html, fileName)
                        }
                    }
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.download), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

private fun getMetaData(reportsViewModel: ReportsViewModel): String {
    return when (reportsViewModel.selectedTabIndex) {
        0 -> {
            reportsViewModel.selectedCampaign?.let { campaign ->
                val teamLead = campaign.staff.first { it.isTeamLead }.let { lead ->
                    listOfNotNull(
                        lead.name.takeIf { it.isNotBlank() },
                        lead.email.takeIf { it.isNotBlank() },
                        lead.mobile.takeIf { it.isNotBlank() }
                    ).joinToString(", ")
                }
                val start = Date(campaign.fromDate.toTimeInMilli()).toMMMddyyyyDateRange()
                val end = Date(campaign.toDate.toTimeInMilli()).toMMMddyyyyDateRange()
                "Campaign: ${campaign.name}" +
                        "<BR />Site Location: ${
                            campaign.location.takeIf { it.isNotBlank() }
                                ?: campaign.areaCouncil.takeIf { it.isNotBlank() }
                                ?: ""}" +
                        "<BR />Team Lead: $teamLead" +
                        "<BR />Date Range: $start - $end" +
                        "<BR />Report Generated: ${Date().toMMMMddyyyy()}"
            } ?: ""
        }
        1 -> {
            val start = reportsViewModel.currentState.dateRangeStart.toMMMddyyyyDateRange()
            val end = reportsViewModel.currentState.dateRangeEnd.toMMMddyyyyDateRange()
            "Health Facility: ${reportsViewModel.selectedFacility?.name}" +
                    "<BR />Date Range: $start - $end" +
                    "<BR />Report Generated: ${Date().toMMMMddyyyy()}"
        }
        2 -> {
            val start = reportsViewModel.currentState.dateRangeStart.toMMMddyyyyDateRange()
            val end = reportsViewModel.currentState.dateRangeEnd.toMMMddyyyyDateRange()
            "Administrative Division: ${reportsViewModel.selectedDivision?.name} ${reportsViewModel.selectedDivisionType}" +
                    "<BR />Date Range: $start - $end" +
                    "<BR />Report Generated: ${Date().toMMMMddyyyy()}"
        }
        else -> ""
    }
}

private fun getFooterData(reportsViewModel: ReportsViewModel): String {
    return when (reportsViewModel.selectedTabIndex) {
        0 -> {
            reportsViewModel.selectedCampaign?.name ?: ""
        }
        1 -> {
            reportsViewModel.selectedFacility?.name ?: ""
        }
        2 -> {
            "${reportsViewModel.selectedDivision?.name} ${reportsViewModel.selectedDivisionType}"
        }
        else -> ""
    }
}