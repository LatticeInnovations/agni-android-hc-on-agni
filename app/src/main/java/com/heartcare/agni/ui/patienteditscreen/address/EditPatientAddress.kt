package com.heartcare.agni.ui.patienteditscreen.address

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heartcare.agni.R
import com.heartcare.agni.data.server.model.patient.PatientAddressResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.common.CustomTextFieldWithLength
import com.heartcare.agni.ui.patientregistration.step3.LevelDropDownComposable
import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPatientAddress(
    navController: NavController,
    viewModel: EditPatientAddressViewModel = hiltViewModel()
) {
    val patientResponse =
        navController.previousBackStackEntry?.savedStateHandle?.get<PatientResponse>(key = "patient_details")
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            patientResponse?.run {
                viewModel.postalCode = permanentAddress.postalCode?:""
                viewModel.province = viewModel.getLevelByFhirId(permanentAddress.province)
                viewModel.areaCouncil = viewModel.getLevelByFhirId(permanentAddress.areaCouncil)
                viewModel.island = viewModel.getLevelByFhirId(permanentAddress.island)
                viewModel.village = if (permanentAddress.village == null) null
                    else {
                        viewModel.getLevelByFhirId(permanentAddress.village)
                }
                viewModel.otherVillage = permanentAddress.addressLine2 ?: ""
                viewModel.isVillageOtherSelected = viewModel.otherVillage.isNotBlank()

                viewModel.getLists()

                viewModel.postalCodeTemp = viewModel.postalCode
                viewModel.provinceTemp = viewModel.province
                viewModel.areaCouncilTemp = viewModel.areaCouncil
                viewModel.islandTemp = viewModel.island
                viewModel.villageTemp = viewModel.village
                viewModel.otherVillageTemp = viewModel.otherVillage
            }
        }
        viewModel.isLaunched = true

    }
    BackHandler(enabled = true) {
        navController.previousBackStackEntry?.savedStateHandle?.set("isProfileUpdated", false)
        navController.popBackStack()
    }

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Address",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "isProfileUpdated",
                            false
                        )
                        navController.popBackStack()

                    }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "clear icon"
                        )
                    }

                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                actions = {
                    TextButton(
                        onClick = {
                            if (viewModel.revertChanges()) {
                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar("Changes undone")

                                }
                            }
                        },
                        enabled = viewModel.checkIsEdit()
                    ) {
                        Text(text = stringResource(R.string.undo_all))
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(15.dp)
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    key(
                        viewModel.provinceList,
                        viewModel.areaCouncilList,
                        viewModel.islandList,
                        viewModel.villageList,
                        viewModel.province,
                        viewModel.areaCouncil,
                        viewModel.island
                    ) {
                        AddressHierarchy(viewModel)
                    }
                    Spacer(Modifier.height(64.dp))
                }
            }
        },
        floatingActionButton = {
            Surface(
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = {
                        viewModel.updateAddressInfo(
                            patientResponse!!.copy(
                                permanentAddress = PatientAddressResponse(
                                    village = viewModel.village?.fhirId,
                                    areaCouncil = viewModel.areaCouncil!!.fhirId,
                                    island = viewModel.island!!.fhirId,
                                    province = viewModel.province!!.fhirId,
                                    postalCode = viewModel.postalCode,
                                    country = "Vanuatu",
                                    addressLine2 = viewModel.otherVillage.ifBlank { null },
                                )
                            )
                        )

                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "isProfileUpdated",
                            true
                        )
                        navController.popBackStack()


                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 30.dp),
                    enabled = viewModel.addressInfoValidation() && viewModel.checkIsEdit()
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    )

}

@Composable
private fun AddressHierarchy(viewModel: EditPatientAddressViewModel) {
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
            resetVillage(viewModel)
            viewModel.getVillageList()
        },
        label = stringResource(id = R.string.island_mandatory),
        dropdownList = viewModel.islandList,
        errorText = stringResource(R.string.island_required),
        isMandatory = true,
        isEnabled = viewModel.areaCouncil != null
    )

    LevelDropDownComposable(
        value = viewModel.village?.name ?: "",
        updateValue = {
            viewModel.village = it
            viewModel.isVillageOtherSelected = it?.name == viewModel.otherName
            if (!viewModel.isVillageOtherSelected) {
                viewModel.otherVillage = ""
            }
        },
        label = stringResource(id = R.string.village),
        dropdownList = viewModel.villageList,
        errorText = stringResource(R.string.village_required),
        isMandatory = false,
        isEnabled = viewModel.island != null
    )
    if (viewModel.isVillageOtherSelected) {
        OtherVillageComposable(viewModel)
    }

    PostalCodeComposable(viewModel)
}

@Composable
private fun OtherVillageComposable(
    viewModel: EditPatientAddressViewModel
) {
    CustomTextFieldWithLength(
        value = viewModel.otherVillage,
        maxLength = viewModel.maxLength,
        weight = 1f,
        isError = viewModel.otherVillageError,
        error = stringResource(R.string.village_required),
        label = stringResource(R.string.other_village_mandatory),
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Sentences,
        updateValue = {
            viewModel.otherVillage = it
            viewModel.otherVillageError = viewModel.otherVillage.isBlank()
        }
    )
}

@Composable
private fun PostalCodeComposable(
    viewModel: EditPatientAddressViewModel
) {
    CustomTextFieldWithLength(
        value = viewModel.postalCode,
        label = stringResource(R.string.postal_code),
        placeholder = null,
        weight = 1f,
        maxLength = viewModel.postalCodeLength,
        isError = false,
        error = "",
        keyboardType = KeyboardType.Number,
        keyboardCapitalization = KeyboardCapitalization.Characters,
        updateValue = {
            if ((it.matches(onlyNumbers) || it.isEmpty()))
                viewModel.postalCode = it
        }
    )
}

private fun resetAreaCouncilHierarchy(viewModel: EditPatientAddressViewModel) {
    viewModel.areaCouncil = null
    resetIslandHierarchy(viewModel)
}

private fun resetIslandHierarchy(viewModel: EditPatientAddressViewModel) {
    viewModel.island = null
    resetVillage(viewModel)
}

private fun resetVillage(viewModel: EditPatientAddressViewModel) {
    viewModel.village = null
    viewModel.otherVillage = ""
    viewModel.isVillageOtherSelected = false
}