package com.heartcare.agni.ui.sitescreendashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.DateRangeEnum
import com.heartcare.agni.data.local.enums.LevelsEnum.Companion.getLevelsDisplay
import com.heartcare.agni.data.local.model.report.StatSubGroup
import com.heartcare.agni.ui.common.DropdownComposable
import com.heartcare.agni.ui.patientregistration.step3.LevelDropDownComposable
import com.heartcare.agni.ui.sitescreendashboard.components.DateRangeBottomSheet
import com.heartcare.agni.ui.sitescreendashboard.components.StatProgressCard
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toDateRange

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
            TabDropDownComposable(viewModel)

            // Date Range (not shown on Screening Site tab — campaign defines its own dates)
            DateRangeComposable(viewModel)

            AnimatedVisibility(
                visible = viewModel.showSummary(),
                enter = fadeIn()
            ) {
                if (viewModel.currentState.totalScreened > 0) {
                    Column {
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
                                    text = stringResource(
                                        R.string.screened_count,
                                        viewModel.currentState.totalScreened
                                    ),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = stringResource(
                                        R.string.screened_gender_breakdown,
                                        viewModel.currentState.totalMale,
                                        viewModel.currentState.totalFemale,
                                        viewModel.currentState.totalOther
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
                                    viewModel.currentState.ageGroups.forEach { (range, count) ->
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
                            title = stringResource(R.string.stat_bmi_categories, viewModel.currentState.bmiTotal),
                            stats = viewModel.currentState.bmiStats,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        StatProgressCard(
                            title = stringResource(R.string.stat_blood_pressure, viewModel.currentState.bloodPressureTotal),
                            stats = viewModel.currentState.bloodPressureStats,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        StatProgressCard(
                            title = stringResource(R.string.stat_smoking_status, viewModel.currentState.smokingTotal),
                            stats = viewModel.currentState.smokingStats,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        StatProgressCard(
                            title = stringResource(R.string.stat_blood_sugar, viewModel.currentState.bloodSugarFastingTotal + viewModel.currentState.bloodSugarRandomTotal),
                            subGroups = listOf(
                                StatSubGroup(stringResource(R.string.blood_sugar_fasting, viewModel.currentState.bloodSugarFastingTotal), viewModel.currentState.bloodSugarFastingStats),
                                StatSubGroup(stringResource(R.string.blood_sugar_random, viewModel.currentState.bloodSugarRandomTotal), viewModel.currentState.bloodSugarRandomStats)
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        StatProgressCard(
                            title = stringResource(R.string.stat_total_cholesterol, viewModel.currentState.cholesterolTotal),
                            stats = viewModel.currentState.cholesterolStats,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        StatProgressCard(
                            title = stringResource(R.string.stat_cvd_risk, viewModel.currentState.cvdRiskTotal),
                            stats = viewModel.currentState.cvdRiskStats,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.no_record_found),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }

    if (viewModel.showDateRangeSheet) {
        DateRangeBottomSheet(
            selected = viewModel.currentState.selectedDateRangeLabel,
            dateRangeStart = viewModel.currentState.dateRangeStart,
            dateRangeEnd = viewModel.currentState.dateRangeEnd,
            onDismissRequest = { viewModel.showDateRangeSheet = false },
            onSaveClick = { rangeType, start, end ->
                viewModel.updateDateRange(rangeType, start, end)
            }
        )
    }
}

@Composable
private fun TabDropDownComposable(viewModel: ReportsViewModel) {
    when (viewModel.selectedTabIndex) {
        0 -> {
            Text(
                text = stringResource(R.string.choose_screening_campaign_hint),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            DropdownComposable(
                value = viewModel.selectedCampaign,
                onValueSelected = {
                    viewModel.selectedCampaign = it
                    viewModel.getDataOfCampaign()
                },
                label = "",
                dropdownList = viewModel.campaignOptions,
                itemToString = { it.name},
                errorText = stringResource(R.string.required),
                isMandatory = true,
                dropdownWeight = .92f
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campaign info card
            if (viewModel.showSummary()) {
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
        }

        1 -> {
            Text(
                text = stringResource(R.string.choose_health_facility_hint),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LevelDropDownComposable(
                value = viewModel.selectedFacility?.name ?: "",
                updateValue = {
                    viewModel.selectedFacility = it
                    viewModel.getDataOfFacility()
                },
                label = "",
                dropdownList = viewModel.facilityOptions,
                errorText = stringResource(R.string.health_facility_required),
                isMandatory = true,
                isEnabled = true
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
                dropdownList = getLevelsDisplay(),
                label = "",
                updateValue = {
                    viewModel.selectedDivisionType = it
                    viewModel.getDivisionOptions()
                },
                errorText = "",
                isMandatory = true,
                dropdownWeight = .92f
            )
            Spacer(modifier = Modifier.height(8.dp))
            key(viewModel.selectedDivisionType) {
                LevelDropDownComposable(
                    value = viewModel.selectedDivision?.name ?: "",
                    updateValue = {
                        viewModel.selectedDivision = it
                        viewModel.getDataOfDivision()
                    },
                    label = "",
                    dropdownList = viewModel.divisionOptions,
                    errorText = stringResource(R.string.required),
                    isMandatory = true,
                    isEnabled = true
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DateRangeComposable(viewModel: ReportsViewModel) {
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

            Column(
                horizontalAlignment = Alignment.End
            ) {
                OutlinedButton(
                    onClick = { viewModel.showDateRangeSheet = true },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = viewModel.currentState.selectedDateRangeLabel,
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

                if (viewModel.currentState.selectedDateRangeLabel == DateRangeEnum.CUSTOM_RANGE.label) {
                    Text(
                        text = "${viewModel.currentState.dateRangeStart.toDateRange()} - ${viewModel.currentState.dateRangeEnd.toDateRange()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportsScreenPreview() {
    MaterialTheme {
        ReportsScreen()
    }
}
