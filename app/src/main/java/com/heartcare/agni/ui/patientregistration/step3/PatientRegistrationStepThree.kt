package com.heartcare.agni.ui.patientregistration.step3

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.LocationStateEnum
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.navigation.Screen
import com.heartcare.agni.ui.common.CustomDialog
import com.heartcare.agni.ui.common.CustomTextFieldWithLength
import com.heartcare.agni.ui.patientregistration.PatientRegistrationViewModel
import com.heartcare.agni.ui.patientregistration.model.PatientRegister
import com.heartcare.agni.ui.theme.Neutral40
import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbers
import timber.log.Timber

@Composable
fun PatientRegistrationStepThree(
    navController: NavController,
    patientRegister: PatientRegister,
    viewModel: PatientRegistrationStepThreeViewModel = hiltViewModel()
) {
    val patientRegistrationViewModel: PatientRegistrationViewModel = viewModel()

    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            with(viewModel) {
                province = patientRegister.province
                areaCouncil = patientRegister.areaCouncil
                island = patientRegister.island
                village = patientRegister.village
                otherVillage = patientRegister.otherVillage.toString()
                postalCode = patientRegister.postalCode.toString()
                isVillageOtherSelected = otherVillage.isNotBlank()
            }
            viewModel.isLaunched = true
        }
    }

    Column(
        modifier = Modifier.padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection(patientRegistrationViewModel)

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            key(
                viewModel.provinceList,
                viewModel.areaCouncilList,
                viewModel.islandList,
                viewModel.villageList
            ) {
                AddressHierarchy(viewModel)
            }
        }

        Button(
            onClick = {
                if (viewModel.addressInfoValidation()) {
                    with(patientRegister) {
                        province = viewModel.province
                        areaCouncil = viewModel.areaCouncil
                        island = viewModel.island
                        village = viewModel.village
                        postalCode = viewModel.postalCode
                        otherVillage = viewModel.otherVillage
                    }

                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "patient_register_details",
                        patientRegister
                    )

                    navController.navigate(Screen.PatientRegistrationPreviewScreen.route)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
        ) {
            Text(text = stringResource(id = R.string.preview))
        }
    }
}

@Composable
private fun HeaderSection(viewModel: PatientRegistrationViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.addresses),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Page 3/${viewModel.totalSteps}",
            style = MaterialTheme.typography.bodySmall,
            color = Neutral40
        )
    }
}

@Composable
private fun AddressHierarchy(viewModel: PatientRegistrationStepThreeViewModel) {
    LevelDropDownComposable(
        value = viewModel.province?.name ?: "",
        updateValue = {
            viewModel.province = it
            viewModel.provinceError = false
            resetAreaCouncilHierarchy(viewModel)
            viewModel.getAreaCouncilList()
        },
        label = stringResource(id = R.string.province_mandatory),
        dropdownList = viewModel.provinceList,
        errorText = stringResource(R.string.province_required),
        isMandatory = true,
        isEnabled = true,
        showError = viewModel.provinceError
    )

    LevelDropDownComposable(
        value = viewModel.areaCouncil?.name ?: "",
        updateValue = {
            viewModel.areaCouncil = it
            viewModel.areaCouncilError = false
            resetIslandHierarchy(viewModel)
            viewModel.getIslandList()
        },
        label = stringResource(id = R.string.area_council_mandatory),
        dropdownList = viewModel.areaCouncilList,
        errorText = stringResource(R.string.area_council_required),
        isMandatory = true,
        isEnabled = viewModel.province != null,
        showError = viewModel.areaCouncilError
    )

    LevelDropDownComposable(
        value = viewModel.island?.name ?: "",
        updateValue = {
            viewModel.island = it
            viewModel.islandError = false
            resetVillage(viewModel)
            viewModel.getVillageList()
        },
        label = stringResource(id = R.string.island_mandatory),
        dropdownList = viewModel.islandList,
        errorText = stringResource(R.string.island_required),
        isMandatory = true,
        isEnabled = viewModel.areaCouncil != null,
        showError = viewModel.islandError
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
    CurrentLocationLayout(viewModel)
}

@Composable
fun LevelDropDownComposable(
    value: String,
    updateValue: (LevelResponse?) -> Unit,
    label: String,
    dropdownList: List<LevelResponse>,
    errorText: String,
    isMandatory: Boolean,
    isEnabled: Boolean,
    showError: Boolean = false
) {
    val defaultOption = LevelResponse(
        fhirId = "",
        code = "",
        levelType = "",
        name = "Select",
        population = null,
        precedingLevelId = null,
        secondaryName = null,
        status = ""
    )
    val finalList = if (!isMandatory) listOf(defaultOption) + dropdownList else dropdownList
    var expanded by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            readOnly = true,
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth(),
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(interactionSource, isEnabled) {
                    interactionSource.interactions.collect {
                        if (isEnabled && it is PressInteraction.Release) {
                            expanded = !expanded
                        }
                    }
                }
            },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            },
            supportingText = if (isError || showError) {
                {
                    Text(errorText)
                }
            } else null,
            isError = isError || showError
        )

        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth(0.91f)
                .heightIn(0.dp, 300.dp),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                isError = value.isBlank() && isMandatory
            },
        ) {
            finalList.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        expanded = !expanded
                        isError = false
                        updateValue(if (label == defaultOption && !isMandatory) null else label)
                    },
                    text = {
                        Text(
                            text = label.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun OtherVillageComposable(
    viewModel: PatientRegistrationStepThreeViewModel
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
    viewModel: PatientRegistrationStepThreeViewModel
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

private fun resetAreaCouncilHierarchy(viewModel: PatientRegistrationStepThreeViewModel) {
    viewModel.areaCouncil = null
    resetIslandHierarchy(viewModel)
}

private fun resetIslandHierarchy(viewModel: PatientRegistrationStepThreeViewModel) {
    viewModel.island = null
    resetVillage(viewModel)
}

private fun resetVillage(viewModel: PatientRegistrationStepThreeViewModel) {
    viewModel.village = null
    viewModel.otherVillage = ""
    viewModel.isVillageOtherSelected = false
}

@Composable
private fun CurrentLocationLayout(viewModel: PatientRegistrationStepThreeViewModel) {
    val context = LocalContext.current

    // check location turn on or not
    val settingResultRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            viewModel.fetchLocation()

            Timber.d("location permission: Accepted")
        } else {
            Timber.d("location permission : Denied")
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, update the location
                checkLocationSetting(
                    context = context,
                    onDisabled = { intentSenderRequest ->
                        settingResultRequest.launch(intentSenderRequest)
                    },
                    onEnabled = {
                        if (viewModel.latitude == 0.0 && viewModel.longitude == 0.0) {
                            viewModel.fetchLocation()
                        }
                    })
            } else {
                Timber.d("Permission not granted")
            }
        }
    )

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        ElevatedCard(
            modifier = Modifier.clickable {
                if (hasLocationPermission(context)) {
                    // ask to turn on location
                    checkLocationSetting(context = context, onDisabled = { intentSenderRequest ->
                        settingResultRequest.launch(intentSenderRequest)
                    }, onEnabled = {
                        if (viewModel.latitude == 0.0 && viewModel.longitude == 0.0) {
                            viewModel.fetchLocation()
                        }
                    })
                } else {
                    // Request location permission
                    viewModel.openPermissionDialog = true
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(10.dp)
            ) {
                LocationStateIcon(stepState = viewModel.stepState)
                Text(
                    modifier = Modifier.padding(end = 10.dp),
                    text = when (viewModel.stepState) {
                        LocationStateEnum.TODO -> {
                            context.getString(R.string.use_my_current_location)
                        }

                        LocationStateEnum.LOADING -> {
                            context.getString(R.string.fetching_location)
                        }

                        LocationStateEnum.SAVED -> {
                            context.getString(R.string.gps_location_saved)
                        }
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
        if (viewModel.stepState == LocationStateEnum.SAVED) {
            TextButton(
                onClick = {
                    viewModel.latitude = 0.0
                    viewModel.longitude = 0.0
                    viewModel.fetchLocation()
                }
            ) {
                Text(
                    text = stringResource(R.string.update_location),
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
    if (viewModel.openPermissionDialog) {
        CustomDialog(
            canBeDismissed = true,
            title = stringResource(id = R.string.location_permission),
            text = stringResource(id = R.string.location_dialog_description),
            dismissBtnText = stringResource(id = R.string.discard),
            confirmBtnText = stringResource(id = R.string.allow),
            dismiss = {
                viewModel.openPermissionDialog = false
            },
            confirm = {
                viewModel.openPermissionDialog = false
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        )
    }
}

private fun checkLocationSetting(
    context: Context,
    onDisabled: (IntentSenderRequest) -> Unit,
    onEnabled: () -> Unit
) {
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)

    val client: SettingsClient = LocationServices.getSettingsClient(context)
    val builder: LocationSettingsRequest.Builder =
        LocationSettingsRequest.Builder().addLocationRequest(locationRequest.build())

    val gpsSettingTask: Task<LocationSettingsResponse> =
        client.checkLocationSettings(builder.build())

    gpsSettingTask.addOnSuccessListener { onEnabled() }
    gpsSettingTask.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            try {
                val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                onDisabled(intentSenderRequest)
            } catch (_: IntentSender.SendIntentException) {
                // ignore here
            }
        }
    }
}

private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
private fun LocationStateIcon(stepState: LocationStateEnum) {
    when (stepState) {
        LocationStateEnum.TODO -> {
            Icon(
                painter = painterResource(id = R.drawable.current_location_icon),
                contentDescription = null
            )
        }

        LocationStateEnum.LOADING -> {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(20.dp),
                strokeWidth = 2.dp,
                trackColor = MaterialTheme.colorScheme.onSurface,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }

        LocationStateEnum.SAVED -> {
            Icon(
                painter = painterResource(id = R.drawable.sync_completed_icon),
                contentDescription = null
            )
        }
    }
}