package com.heartcare.agni.ui.patientregistration.step1

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heartcare.agni.R
import com.heartcare.agni.data.local.enums.GenderEnum
import com.heartcare.agni.data.local.enums.NationalIdUse
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.ui.common.CustomFilterChip
import com.heartcare.agni.ui.common.CustomTextField
import com.heartcare.agni.ui.common.CustomTextFieldWithLength
import com.heartcare.agni.ui.common.DeceasedReasonComposable
import com.heartcare.agni.ui.patientregistration.PatientRegistrationViewModel
import com.heartcare.agni.ui.patientregistration.model.PatientRegister
import com.heartcare.agni.ui.theme.Neutral40
import com.heartcare.agni.utils.converters.responseconverter.MonthsList.getMonthsList
import com.heartcare.agni.utils.converters.responseconverter.StringUtils.capitalizeFirst
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isDOBValid
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toMonthInteger
import com.heartcare.agni.utils.regex.EmailRegex.emailPattern
import com.heartcare.agni.utils.regex.NameRegex.nameRegex
import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbers
import com.heartcare.agni.utils.regex.PhoneNumberRegex.phoneNumberRegex
import com.heartcare.agni.utils.regex.RegexPatterns.atLeastOneAlphaAndNumber

@Composable
fun PatientRegistrationStepOne(
    patientRegister: PatientRegister,
    viewModel: PatientRegistrationStepOneViewModel = hiltViewModel(),
    patientRegistrationViewModel: PatientRegistrationViewModel = viewModel()
) {
    LaunchedEffect(viewModel.isLaunched) {
        if (!viewModel.isLaunched) {
            patientRegister.run {
                viewModel.firstName = firstName.toString()
                viewModel.lastName = lastName.toString()
                viewModel.phoneNumber = phoneNumber.toString()
                viewModel.dobAgeSelector = dobAgeSelector.toString()
                viewModel.dobDay = dobDay.toString()
                viewModel.dobMonth = dobMonth.toString()
                viewModel.dobYear = dobYear.toString()
                viewModel.years = years.toString()
                viewModel.months = months.toString()
                viewModel.days = days.toString()
                viewModel.gender = gender.toString()
                viewModel.isPersonDeceased = isPersonDeceased ?: 0
                viewModel.selectedDeceasedReason = personDeceasedReason.toString()
                viewModel.motherName = motherName.toString()
                viewModel.fatherName = fatherName.toString()
                viewModel.spouseName = spouseName.toString()
                viewModel.hospitalId = hospitalId.toString()
                viewModel.nationalId = nationalId.toString()
                viewModel.isNationalIdVerified = nationalIdUse == NationalIdUse.OFFICIAL.use
                viewModel.isVerifyClicked = !nationalIdUse.isNullOrBlank()
            }
            viewModel.isLaunched = true
        }
    }
    Column(
        modifier = Modifier
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderRow(patientRegistrationViewModel)
        Spacer(modifier = Modifier.height(15.dp))
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .testTag("columnLayout")
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NationalIdComposable(viewModel)
            Spacer(modifier = Modifier.height(1.dp))
            LastNameField(viewModel)
            FirstNameField(viewModel)
            DOBAndAgeFields(viewModel)
            PhoneNumberField(viewModel)
            EmailField(viewModel)
            GenderComposable(viewModel)
            PatientDeceasedComposable(viewModel)
            MotherNameField(viewModel)
            FatherNameField(viewModel)
            SpouseNameField(viewModel)
            HospitalIdComposable(viewModel)
        }
        NextButton(patientRegister, viewModel, patientRegistrationViewModel)
    }
    if (viewModel.showDeceasedReasonSheet) {
        DeceasedReasonComposable(
            selectedReason = viewModel.selectedDeceasedReason,
            dismiss = {
                viewModel.showDeceasedReasonSheet = false
            },
            updatedReasons = { reasons ->
                viewModel.selectedDeceasedReason = reasons
                viewModel.showDeceasedReasonSheet = false
                viewModel.isPersonDeceased = YesNoEnum.YES.code
            }
        )
    }
}

@Composable
private fun HeaderRow(patientRegistrationViewModel: PatientRegistrationViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.basic_information),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Page 1/${patientRegistrationViewModel.totalSteps}",
            style = MaterialTheme.typography.bodySmall,
            color = Neutral40
        )
    }
}

@Composable
private fun NationalIdComposable(
    viewModel: PatientRegistrationStepOneViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = viewModel.nationalId,
            onValueChange = {
                if (it.length <= viewModel.maxNationalIdLength && (it.matches(onlyNumbers) || it.isEmpty())) {
                    viewModel.nationalId = it
                    viewModel.isVerifyClicked = false
                    viewModel.isNationalIdVerified = false
                    viewModel.verifiedRecord = null
                }
            },
            label = {
                Text(stringResource(R.string.national_id))
            },
            modifier = Modifier.weight(2.5f),
            supportingText = {
                NationalIdSupportingText(viewModel)
            },
            trailingIcon = {
                if (viewModel.nationalId.isNotBlank())
                    IconButton(
                        onClick = {
                            viewModel.nationalId = ""
                            viewModel.isVerifyClicked = false
                            viewModel.isNationalIdVerified = false
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.cancel),
                            contentDescription = null
                        )
                    }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isError = viewModel.verifyNationIdError
        )
        FilledTonalButton(
            onClick = {
                viewModel.verifyNationIdError = false
                viewModel.isVerifyClicked = true
                viewModel.verifyNationalId()
            },
            modifier = Modifier.weight(1f),
            enabled = viewModel.nationalId.isNotBlank() && !viewModel.isNationalIdVerified
        ) {
            Text(stringResource(R.string.verify))
        }
    }
}

@Composable
private fun NationalIdSupportingText(
    viewModel: PatientRegistrationStepOneViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (viewModel.verifyNationIdError) {
            Text(
                text = "Please verify National ID",
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (viewModel.isVerifyClicked) {
            val text: String
            val icon: Int
            val color: Color
            if (viewModel.isNationalIdVerified) {
                text = stringResource(R.string.verified)
                icon = R.drawable.sync_completed_icon
                color = MaterialTheme.colorScheme.primary
            } else {
                text = stringResource(R.string.unverified)
                icon = R.drawable.info
                color = MaterialTheme.colorScheme.onSurfaceVariant
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = color
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${viewModel.nationalId.length}/${viewModel.maxNationalIdLength}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LastNameField(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextFieldWithLength(
        value = viewModel.lastName,
        label = stringResource(id = R.string.last_name_mandatory),
        placeholder = null,
        weight = 1f,
        maxLength = viewModel.maxNameLength,
        isError = viewModel.isLastNameValid,
        error = stringResource(R.string.last_name_required),
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Words
    ) {
        if (it.matches(nameRegex) || it.isEmpty()) {
            viewModel.lastName = it
            if (viewModel.verifiedRecord != null) {
                viewModel.isNationalIdVerified = viewModel.verifyLastName()
            }
        }
        viewModel.isLastNameValid = viewModel.lastName.isBlank()
    }
}

@Composable
private fun FirstNameField(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextFieldWithLength(
        value = viewModel.firstName,
        label = stringResource(id = R.string.first_name_mandatory),
        placeholder = null,
        weight = 1f,
        maxLength = viewModel.maxNameLength,
        isError = viewModel.isFirstNameValid,
        error = stringResource(id = R.string.first_name_required),
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Words
    ) {
        if (it.matches(nameRegex) || it.isEmpty()) {
            viewModel.firstName = it
            if (viewModel.verifiedRecord != null) {
                viewModel.isNationalIdVerified = viewModel.verifyFirstAndMiddleName()
            }
        }
        viewModel.isFirstNameValid = viewModel.firstName.isBlank()
    }
}

@Composable
private fun DOBAndAgeFields(viewModel: PatientRegistrationStepOneViewModel) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            CustomFilterChip(viewModel.dobAgeSelector, "dob", "Date of Birth") {
                viewModel.dobAgeSelector = it
                viewModel.days = ""
                viewModel.months = ""
                viewModel.years = ""
            }
            Spacer(modifier = Modifier.width(10.dp))
            CustomFilterChip(viewModel.dobAgeSelector, "age", "Age") {
                viewModel.dobAgeSelector = it
                viewModel.dobDay = ""
                viewModel.dobMonth = ""
                viewModel.dobYear = ""
                viewModel.isNationalIdVerified = false
            }
        }
        if (viewModel.dobAgeSelector == "dob") {
            DobTextField(viewModel)
        } else
            AgeTextField(viewModel)

        if (viewModel.isDOBAgeBlank) {
            Text(
                text = stringResource(id = R.string.dob_age_required),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun DobTextField(viewModel: PatientRegistrationStepOneViewModel) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DOBDayField(viewModel)
            MonthDropDown(viewModel)
            DOBYearField(viewModel)
        }
        DateErrorText(viewModel)
    }
}

@Composable
private fun DOBDayField(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextField(
        value = viewModel.dobDay,
        label = stringResource(id = R.string.day),
        weight = 0.23f,
        maxLength = 2,
        isError = false,
        error = "",
        KeyboardType.Number,
        KeyboardCapitalization.None
    ) {
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) {
            viewModel.dobDay = it
            viewModel.isDOBAgeBlank = false
            if (viewModel.verifiedRecord != null) {
                viewModel.isNationalIdVerified = viewModel.verifyDOBDay()
            }
        }
        if (viewModel.dobDay.isNotEmpty()) {
            viewModel.monthsList = getMonthsList(viewModel.dobDay)
        }
    }
}

@Composable
private fun MonthDropDown(viewModel: PatientRegistrationStepOneViewModel) {
    var monthExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .testTag("Month")
    ) {
        OutlinedTextField(
            value = viewModel.dobMonth,
            onValueChange = {
                viewModel.dobMonth = it
            },
            label = {
                Text(text = stringResource(id = R.string.month))
            },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "")
            },
            interactionSource = remember {
                MutableInteractionSource()
            }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            monthExpanded = !monthExpanded
                        }
                    }
                }
            },
            readOnly = true,
            singleLine = true
        )
        DropdownMenu(
            modifier = Modifier.fillMaxHeight(0.5f),
            expanded = monthExpanded,
            onDismissRequest = { monthExpanded = false },
        ) {
            viewModel.monthsList.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        monthExpanded = false
                        viewModel.isDOBAgeBlank = false
                        viewModel.dobMonth = label
                        if (viewModel.verifiedRecord != null) {
                            viewModel.isNationalIdVerified = viewModel.verifyDOBMonth()
                        }
                    },
                    text = {
                        Text(
                            text = label,
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
private fun DOBYearField(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextField(
        value = viewModel.dobYear,
        label = stringResource(id = R.string.year),
        weight = 1f,
        maxLength = 4,
        isError = false,
        error = "",
        KeyboardType.Number,
        KeyboardCapitalization.None
    ) {
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) {
            viewModel.dobYear = it
            viewModel.isDOBAgeBlank = false
            if (viewModel.verifiedRecord != null) {
                viewModel.isNationalIdVerified = viewModel.verifyDOBYear()
            }
        }
    }
}

@Composable
private fun DateErrorText(viewModel: PatientRegistrationStepOneViewModel) {
    if (viewModel.dobDay.isNotEmpty() && viewModel.dobMonth.isNotEmpty() && viewModel.dobYear.isNotEmpty()
        && !isDOBValid(
            viewModel.dobDay.toInt(),
            viewModel.dobMonth.toMonthInteger(),
            viewModel.dobYear.toInt()
        )
    ) {
        Text(
            text = stringResource(
                id = R.string.invalid_date,
                "${viewModel.dobDay}-${viewModel.dobMonth}-${viewModel.dobYear}"
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        )
    }
}

@Composable
private fun AgeTextField(viewModel: PatientRegistrationStepOneViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        AgeYearsComposable(viewModel)
        Spacer(modifier = Modifier.width(15.dp))
        AgeMonthsComposable(viewModel)
        Spacer(modifier = Modifier.width(15.dp))
        AgeDaysComposable(viewModel)
    }
}

@Composable
private fun AgeDaysComposable(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextField(
        viewModel.days,
        label = stringResource(id = R.string.age_days),
        0.5F,
        2,
        viewModel.isAgeDaysValid,
        stringResource(
            id = R.string.age_days_error_msg
        ),
        KeyboardType.Number,
        KeyboardCapitalization.None
    ) {
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) {
            viewModel.days = it
            viewModel.isDOBAgeBlank = false
        }
        if (viewModel.days.isNotEmpty()) viewModel.isAgeDaysValid = viewModel.days.toInt() !in 1..30
    }
}

@Composable
private fun AgeMonthsComposable(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextField(
        viewModel.months,
        label = stringResource(id = R.string.age_months),
        0.36F,
        2,
        viewModel.isAgeMonthsValid,
        stringResource(
            id = R.string.age_months_error_msg
        ),
        KeyboardType.Number,
        KeyboardCapitalization.None
    ) {
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) {
            viewModel.months = it
            viewModel.isDOBAgeBlank = false
        }
        if (viewModel.months.isNotEmpty()) viewModel.isAgeMonthsValid =  viewModel.months.toInt() !in 1..11
    }
}

@Composable
private fun AgeYearsComposable(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextField(
        viewModel.years,
        label = stringResource(id = R.string.age_years),
        0.25F,
        3,
        viewModel.isAgeYearsValid,
        stringResource(
            id = R.string.age_years_error_msg
        ),
        KeyboardType.Number,
        KeyboardCapitalization.None
    ) {
        if (it.matches(viewModel.onlyNumbers) || it.isEmpty()) {
            viewModel.years = it
            viewModel.isDOBAgeBlank = false
        }
        if (viewModel.years.isNotEmpty()) viewModel.isAgeYearsValid = viewModel.years.toInt() !in 0..150
    }
}

@Composable
private fun PhoneNumberField(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextFieldWithLength(
        value = viewModel.phoneNumber,
        label = stringResource(id = R.string.mobile),
        placeholder = null,
        weight = 1f,
        maxLength = viewModel.maxPhoneNumberLength,
        isError = viewModel.isPhoneValid,
        error = stringResource(id = R.string.phone_number_error_msg),
        keyboardType = KeyboardType.Number,
        keyboardCapitalization = KeyboardCapitalization.Words
    ) {
        if (it.length <= viewModel.maxPhoneNumberLength && (it.matches(viewModel.onlyNumbers) || it.isEmpty()))
            viewModel.phoneNumber = it
        viewModel.isPhoneValid =
            viewModel.phoneNumber.isNotBlank() && !viewModel.phoneNumber.matches(
                phoneNumberRegex
            )
    }
}

@Composable
private fun EmailField(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextField(
        value = viewModel.email,
        label = stringResource(id = R.string.email),
        weight = 1f,
        maxLength = viewModel.maxNameLength,
        isError = viewModel.emailError,
        error = stringResource(id = R.string.enter_valid_email),
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.None
    ) {
        viewModel.email = it
        viewModel.emailError = viewModel.email.isNotBlank() && !viewModel.email.matches(emailPattern)
    }
}

@Composable
private fun GenderComposable(viewModel: PatientRegistrationStepOneViewModel) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("genderRow"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.gender_mandatory),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(20.dp))
            CustomFilterChip(
                viewModel.gender,
                GenderEnum.MALE.value,
                stringResource(id = R.string.male)
            ) {
                viewModel.gender = it
                viewModel.isGenderBlank = false
                if (viewModel.verifiedRecord != null) {
                    viewModel.isNationalIdVerified = viewModel.verifyGender()
                }
            }
            Spacer(modifier = Modifier.width(15.dp))
            CustomFilterChip(
                viewModel.gender,
                GenderEnum.FEMALE.value,
                stringResource(id = R.string.female)
            ) {
                viewModel.gender = it
                viewModel.isGenderBlank = false
                if (viewModel.verifiedRecord != null) {
                    viewModel.isNationalIdVerified = viewModel.verifyGender()
                }
            }
        }
        if (viewModel.isGenderBlank) {
            Text(
                text = stringResource( id = R.string.gender_is_required),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun PatientDeceasedComposable(viewModel: PatientRegistrationStepOneViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.patient_deceased),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(20.dp))
        CustomFilterChip(
            viewModel.isPersonDeceased.toString(),
            YesNoEnum.YES.code.toString(),
            YesNoEnum.YES.display
        ) {
            viewModel.showDeceasedReasonSheet = true
        }
        Spacer(modifier = Modifier.width(15.dp))
        CustomFilterChip(
            viewModel.isPersonDeceased.toString(),
            YesNoEnum.NO.code.toString(),
            YesNoEnum.NO.display
        ) {
            viewModel.isPersonDeceased = it.toInt()
            viewModel.selectedDeceasedReason = ""
        }
    }
}

@Composable
private fun MotherNameField(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextFieldWithLength(
        value = viewModel.motherName,
        label = stringResource(id = R.string.mother_name_mandatory),
        placeholder = null,
        weight = 1f,
        maxLength = viewModel.maxNameLength,
        isError = viewModel.isMotherNameValid,
        error = stringResource(id = R.string.mother_name_required),
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Words
    ) {
        if (it.matches(nameRegex) || it.isEmpty()) viewModel.motherName = it
        viewModel.isMotherNameValid = viewModel.motherName.isBlank()
    }
}

@Composable
private fun FatherNameField(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextFieldWithLength(
        value = viewModel.fatherName,
        label = stringResource(id = R.string.father_name),
        placeholder = null,
        weight = 1f,
        maxLength = viewModel.maxNameLength,
        isError = false,
        error = "",
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Words
    ) {
        if (it.matches(nameRegex) || it.isEmpty()) viewModel.fatherName = it
    }
}

@Composable
private fun SpouseNameField(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextFieldWithLength(
        value = viewModel.spouseName,
        label = stringResource(id = R.string.spouse_name),
        placeholder = null,
        weight = 1f,
        maxLength = viewModel.maxNameLength,
        isError = false,
        error = "",
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Words
    ) {
        if (it.matches(nameRegex) || it.isEmpty()) viewModel.spouseName = it
    }
}

@Composable
private fun HospitalIdComposable(viewModel: PatientRegistrationStepOneViewModel) {
    CustomTextFieldWithLength(
        value = viewModel.hospitalId,
        label = stringResource(id = R.string.hospital_id),
        placeholder = null,
        weight = 1f,
        maxLength = viewModel.maxHospitalIdLength,
        isError = viewModel.isHospitalIdValid,
        error = stringResource(id = R.string.hospital_id_error_msg),
        keyboardType = KeyboardType.Text,
        keyboardCapitalization = KeyboardCapitalization.Characters
    ) { value ->
        val filtered = value.filter { it.isLetterOrDigit() }
        if (value.length <= viewModel.maxHospitalIdLength && (value == filtered || value.isEmpty()))
            viewModel.hospitalId = value
        viewModel.isHospitalIdValid = viewModel.hospitalId.isNotBlank() &&
                !atLeastOneAlphaAndNumber.matches(viewModel.hospitalId)
    }
}

@Composable
private fun NextButton(
    patientRegister: PatientRegister,
    viewModel: PatientRegistrationStepOneViewModel,
    patientRegistrationViewModel: PatientRegistrationViewModel
) {
    Button(
        onClick = {
            if (viewModel.basicInfoValidation()) {
                patientRegister.run {
                    firstName = viewModel.firstName.capitalizeFirst().trim()
                    lastName = viewModel.lastName.capitalizeFirst().trim()
                    dobAgeSelector = viewModel.dobAgeSelector
                    dobDay = viewModel.dobDay
                    dobMonth = viewModel.dobMonth
                    dobYear = viewModel.dobYear
                    years = viewModel.years
                    months = viewModel.months
                    days = viewModel.days
                    phoneNumber = viewModel.phoneNumber
                    email = viewModel.email
                    gender = viewModel.gender
                    isPersonDeceased = viewModel.isPersonDeceased
                    personDeceasedReason = viewModel.selectedDeceasedReason
                    motherName = viewModel.motherName.capitalizeFirst().trim()
                    fatherName = viewModel.fatherName.capitalizeFirst().trim()
                    spouseName = viewModel.spouseName.capitalizeFirst().trim()
                    nationalId = viewModel.nationalId
                    hospitalId = viewModel.hospitalId
                    nationalIdUse = if (viewModel.nationalId.isNotBlank()) {
                        if (viewModel.isNationalIdVerified) NationalIdUse.OFFICIAL.use
                        else NationalIdUse.TEMP.use
                    } else null
                }
                patientRegistrationViewModel.currentStep = 2
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .testTag("step2")
    ) {
        Text(text = stringResource(id = R.string.next))
    }
}