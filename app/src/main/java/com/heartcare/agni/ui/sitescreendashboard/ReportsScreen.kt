package com.heartcare.agni.ui.sitescreendashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heartcare.agni.R
import com.heartcare.agni.data.local.model.report.StatSubGroup
import com.heartcare.agni.ui.common.DropdownComposable
import com.heartcare.agni.ui.sitescreendashboard.components.DateRangeBottomSheet
import com.heartcare.agni.ui.sitescreendashboard.components.StatProgressCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val tabs = listOf(
        stringResource(R.string.tab_screening_site),
        stringResource(R.string.tab_facility),
        stringResource(R.string.tab_division)
    )

    val fastingLabel = stringResource(R.string.blood_sugar_fasting)
    val randomLabel = stringResource(R.string.blood_sugar_random)
    val customRange = stringResource(R.string.custom_range)

    // Initialise date range label once with localised default
    LaunchedEffect(Unit) {
        if (viewModel.selectedDateRangeLabel.isEmpty()) {
            viewModel.selectedDateRangeLabel = customRange
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding()
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = viewModel.selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = viewModel.selectedTabIndex == index,
                    onClick = { viewModel.selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            color = if (viewModel.selectedTabIndex == index) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                )
            }
        }

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Dynamic Dropdowns per Tab
            when (viewModel.selectedTabIndex) {
                0 -> {
                    Text(
                        text = stringResource(R.string.choose_screening_campaign_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    DropdownComposable(
                        value = viewModel.selectedCampaign,
                        dropdownList = viewModel.campaignOptions,
                        label = "",
                        updateValue = { viewModel.selectedCampaign = it },
                        errorText = "",
                        isMandatory = true,
                        dropdownWeight = .92f
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Campaign info card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = viewModel.campaignPractitionerName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = viewModel.campaignContact,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.today_icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = viewModel.campaignDateRange,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = viewModel.campaignLocation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                1 -> {
                    Text(
                        text = stringResource(R.string.choose_health_facility_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    DropdownComposable(
                        value = viewModel.selectedFacility,
                        dropdownList = viewModel.facilityOptions,
                        label = "",
                        updateValue = { viewModel.selectedFacility = it },
                        errorText = "",
                        isMandatory = true,
                        dropdownWeight = .92f
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                2 -> {
                    Text(
                        text = stringResource(R.string.administrative_division),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    DropdownComposable(
                        value = viewModel.selectedDivisionType,
                        dropdownList = viewModel.divisionTypeOptions,
                        label = "",
                        updateValue = { viewModel.selectedDivisionType = it },
                        errorText = "",
                        isMandatory = true,
                        dropdownWeight = .92f
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DropdownComposable(
                        value = viewModel.selectedDivisionName,
                        dropdownList = viewModel.divisionNameOptions,
                        label = "",
                        updateValue = { viewModel.selectedDivisionName = it },
                        errorText = "",
                        isMandatory = true,
                        dropdownWeight = .92f

                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Date Range (not shown on Screening Site tab — campaign defines its own dates)
            if (viewModel.selectedTabIndex != 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(.4f)) {
                        Text(
                            text = stringResource(R.string.date_range),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = stringResource(R.string.date_range_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    OutlinedButton(
                        onClick = { viewModel.showDateRangeSheet = true },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = viewModel.selectedDateRangeLabel,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.screened_count, viewModel.totalScreened),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(
                            R.string.screened_gender_breakdown,
                            viewModel.totalMale,
                            viewModel.totalFemale
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = stringResource(R.string.age_group),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.ageGroups.forEach { (range, count) ->
                            Column(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(vertical = 12.dp)
                                    .padding(start = 16.dp, end = 25.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = range,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = count,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }

            // Statistics Cards
            StatProgressCard(
                title = stringResource(R.string.stat_bmi_categories),
                stats = viewModel.bmiStats,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            StatProgressCard(
                title = stringResource(R.string.stat_blood_pressure),
                stats = viewModel.bloodPressureStats,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            StatProgressCard(
                title = stringResource(R.string.stat_smoking_status),
                stats = viewModel.smokingStats,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            StatProgressCard(
                title = stringResource(R.string.stat_blood_sugar),
                subGroups = listOf(
                    StatSubGroup(fastingLabel, viewModel.bloodSugarFastingStats),
                    StatSubGroup(randomLabel, viewModel.bloodSugarRandomStats)
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            StatProgressCard(
                title = stringResource(R.string.stat_total_cholesterol),
                stats = viewModel.cholesterolStats,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            StatProgressCard(
                title = stringResource(R.string.stat_cvd_risk),
                stats = viewModel.cvdRiskStats,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }

    if (viewModel.showDateRangeSheet) {
        DateRangeBottomSheet(
            onDismissRequest = { viewModel.showDateRangeSheet = false },
            onSaveClick = { rangeType, start, end ->
                viewModel.updateDateRange(rangeType, start, end)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportsScreenPreview() {
    MaterialTheme {
        ReportsScreen()
    }
}
