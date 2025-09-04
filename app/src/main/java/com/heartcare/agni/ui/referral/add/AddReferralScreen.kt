package com.heartcare.agni.ui.referral.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.common.CustomTextFieldWithLength
import com.heartcare.agni.ui.patientregistration.step3.LevelDropDownComposable
import com.heartcare.agni.ui.referral.ReferringDetailComposable
import com.heartcare.agni.utils.constants.NavControllerConstants.PATIENT
import com.heartcare.agni.utils.constants.NavControllerConstants.REFERRAL_SAVED
import com.heartcare.agni.utils.converters.responseconverter.NameConverter.getFullName
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReferralScreen(
    navController: NavController,
    viewModel: AddReferralViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(PATIENT)
                ?.let {
                    viewModel.patient = it
                    viewModel.getListAndRecords(it.id)
                }
            viewModel.isLaunched = true
        }
    }

    Scaffold(
        modifier = Modifier
            .imePadding(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.add_referral),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("HEADING_TAG")
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
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ReferringDetailComposable(
                        physician = getFullName(
                            viewModel.user.firstName,
                            viewModel.user.lastName
                        ),
                        facility = "${viewModel.user.hospitalName} | ${viewModel.user.levelThreeName}"
                    )
                    key(
                        viewModel.province,
                        viewModel.areaCouncil,
                        viewModel.island
                    ) {
                        LevelDropDownComposable(
                            value = viewModel.province?.name ?: "",
                            updateValue = {
                                viewModel.province = it
                                resetAreaCouncilHierarchy(viewModel)
                                viewModel.getAreaCouncilList()
                            },
                            label = stringResource(id = R.string.province_mandatory),
                            dropdownList = viewModel.provinceList,
                            errorText = stringResource(R.string.province_required),
                            isMandatory = true,
                            isEnabled = true
                        )

                        LevelDropDownComposable(
                            value = viewModel.areaCouncil?.name ?: "",
                            updateValue = {
                                viewModel.areaCouncil = it
                                resetIslandHierarchy(viewModel)
                                viewModel.getIslandList()
                            },
                            label = stringResource(id = R.string.area_council_mandatory),
                            dropdownList = viewModel.areaCouncilList,
                            errorText = stringResource(R.string.area_council_required),
                            isMandatory = true,
                            isEnabled = viewModel.province != null
                        )

                        LevelDropDownComposable(
                            value = viewModel.island?.name ?: "",
                            updateValue = {
                                viewModel.island = it
                                resetHealthFacility(viewModel)
                                viewModel.getHealthFacilityList()
                            },
                            label = stringResource(id = R.string.island_mandatory),
                            dropdownList = viewModel.islandList,
                            errorText = stringResource(R.string.island_required),
                            isMandatory = true,
                            isEnabled = viewModel.areaCouncil != null
                        )

                        LevelDropDownComposable(
                            value = viewModel.heathFacility?.name ?: "",
                            updateValue = {
                                viewModel.heathFacility = it
                            },
                            label = stringResource(id = R.string.health_facility_mandatory),
                            dropdownList = viewModel.heathFacilityList,
                            errorText = stringResource(R.string.health_facility_required),
                            isMandatory = true,
                            isEnabled = viewModel.island != null
                        )


                    }
                    CustomTextFieldWithLength(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        value = viewModel.note,
                        label = stringResource(R.string.add_notes),
                        weight = 1f,
                        maxLength = 500,
                        isError = false,
                        error = "",
                        keyboardType = KeyboardType.Text,
                        keyboardCapitalization = KeyboardCapitalization.Sentences,
                        singleLine = false,
                        updateValue = { viewModel.note = it }
                    )
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = {
                        viewModel.addReferral {
                            coroutineScope.launch {
                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                    REFERRAL_SAVED,
                                    true
                                )
                                navController.navigateUp()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.isValid()
                ) {
                    Text(
                        text = stringResource(R.string.save)
                    )
                }
            }
        }
    )
}


private fun resetAreaCouncilHierarchy(viewModel: AddReferralViewModel) {
    viewModel.areaCouncil = null
    resetIslandHierarchy(viewModel)
}

private fun resetIslandHierarchy(viewModel: AddReferralViewModel) {
    viewModel.island = null
    resetHealthFacility(viewModel)
}

private fun resetHealthFacility(viewModel: AddReferralViewModel) {
    viewModel.heathFacility = null
}