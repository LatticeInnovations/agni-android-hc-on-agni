package com.heartcare.agni.ui.patientregistration.step1

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.repository.nationalId.NationalIdRepository
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toFullMonth
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toMonthInteger
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toYear
import com.heartcare.agni.utils.regex.PhoneNumberRegex.phoneNumberRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PatientRegistrationStepOneViewModel @Inject constructor(
    private val nationalIdRepository: NationalIdRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel(), DefaultLifecycleObserver {
    internal var isLaunched by mutableStateOf(false)

    internal val onlyNumbers = Regex("^\\d+\$")

    internal val maxNameLength = 50
    internal val maxPhoneNumberLength = 15

    internal var firstName by mutableStateOf("")
    internal var lastName by mutableStateOf("")
    internal var phoneNumber by mutableStateOf("")
    internal var email by mutableStateOf("")
    internal var emailError by mutableStateOf(false)
    internal var dobAgeSelector by mutableStateOf("dob")
    internal var dobDay by mutableStateOf("")
    internal var dobMonth by mutableStateOf("")
    internal var dobYear by mutableStateOf("")
    internal var years by mutableStateOf("")
    internal var months by mutableStateOf("")
    internal var days by mutableStateOf("")
    internal var gender by mutableStateOf("")
    internal var isPersonDeceased by mutableIntStateOf(0)
    var showDeceasedReasonSheet by mutableStateOf(false)
    var selectedDeceasedReason by mutableStateOf("")

    internal var motherName by mutableStateOf("")
    internal var fatherName by mutableStateOf("")
    internal var spouseName by mutableStateOf("")

    internal var monthsList = mutableStateListOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    internal var isMotherNameValid by mutableStateOf(false)
    internal var isLastNameValid by mutableStateOf(false)
    internal var isFirstNameValid by mutableStateOf(false)
    internal var isPhoneValid by mutableStateOf(false)
    internal var isAgeDaysValid by mutableStateOf(false)
    internal var isAgeMonthsValid by mutableStateOf(false)
    internal var isAgeYearsValid by mutableStateOf(false)
    internal var isGenderBlank by mutableStateOf(false)
    internal var isDOBAgeBlank by mutableStateOf(false)

    val maxHospitalIdLength = 6
    val maxNationalIdLength = 7

    var hospitalId by mutableStateOf("")
    var isHospitalIdValid by mutableStateOf(false)

    var nationalId by mutableStateOf("")
    var isVerifyClicked by mutableStateOf(false)
    var isNationalIdVerified by mutableStateOf(false)

    var verifiedRecord: Map<String, Any?>? = null

    internal fun basicInfoValidation(): Boolean {
        isFirstNameValid = firstName.isBlank()
        isLastNameValid = lastName.isBlank()
        isPhoneValid = phoneNumber.isNotBlank() && !phoneNumber.matches(phoneNumberRegex)
        isMotherNameValid = motherName.isBlank()
        isGenderBlank = gender.isBlank()
        isDOBAgeBlank = if (dobAgeSelector == "dob") dobDay.isBlank() || dobMonth.isBlank() || dobYear.isBlank()
        else years.isBlank() && months.isBlank() && days.isBlank()
        return firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                motherName.isNotBlank() &&
                !verifyDOB() &&
                !verifyAge() &&
                !isPhoneValid &&
                !emailError &&
                gender.isNotBlank()
    }

    private fun verifyDOB(): Boolean{
        return dobAgeSelector == "dob" && ((dobDay.isBlank() || dobMonth.isBlank() || dobYear.isBlank()) || (!TimeConverter.isDOBValid(
            dobDay.toInt(),
            dobMonth.toMonthInteger(),
            dobYear.toInt()
        )))
    }

    private fun verifyAge(): Boolean{
        return dobAgeSelector == "age" && ((years.isBlank() && months.isBlank() && days.isBlank()) || (isAgeYearsValid || isAgeDaysValid || isAgeMonthsValid))
    }

    fun verifyNationalId() {
        viewModelScope.launch(ioDispatcher) {
            try {
                val json = nationalIdRepository.getNationalIdData()

                if (json.isNullOrBlank()) {
                    isNationalIdVerified = false
                    return@launch
                }

                val gson = Gson()

                val listType = object : TypeToken<List<Map<String, Any?>>>() {}.type

                val data: List<Map<String, Any?>> = gson.fromJson(json, listType)

                val matched = data.firstOrNull {
                    it["national_id"]?.toString() == nationalId
                }

                if (matched != null) {
                    verifiedRecord = matched

                    firstName = listOfNotNull(
                        matched["first_name"]?.toString()?.takeIf { it.isNotBlank() },
                        matched["middle_name"]?.toString()?.takeIf { it.isNotBlank() }
                    ).joinToString(" ")
                    lastName = matched["last_name"]?.toString().orEmpty()
                    gender = matched["gender"]?.toString().orEmpty().toLowerCase(Locale.current)

                    // DOB split (yyyy-MM-dd)
                    val dob = matched["date_of_birth"]?.toString()
                    dob?.let {
                        val dobDate = Date(it.toTimeInMilli())
                        dobYear = dobDate.toYear()
                        dobMonth = dobDate.toFullMonth()
                        dobDay = dobDate.toDay()
                        dobAgeSelector = "dob"
                    }

                    isFirstNameValid = false
                    isLastNameValid = false
                    isGenderBlank = false

                    isNationalIdVerified = true
                } else {
                    isNationalIdVerified = false
                }
            } catch (_: Exception) {
                isNationalIdVerified = false
            }
        }
    }

    fun verifyLastName(): Boolean {
        val record = verifiedRecord ?: return false
        return lastName.trim().equals(
            record["last_name"]?.toString().orEmpty(),
            ignoreCase = true
        )
    }

    fun verifyFirstAndMiddleName(): Boolean {
        val record = verifiedRecord ?: return false
        return firstName.trim().equals(
            listOfNotNull(
                record["first_name"]?.toString()?.takeIf { it.isNotBlank() },
                record["middle_name"]?.toString()?.takeIf { it.isNotBlank() }
            ).joinToString(" "),
            ignoreCase = true
        )
    }

    fun verifyDOBDay(): Boolean {
        val record = verifiedRecord ?: return false
        val dob = record["date_of_birth"]?.toString()
        return dob?.let {
            val dobDate = Date(it.toTimeInMilli())
            dobDay.equals(
                dobDate.toDay(),
                ignoreCase = true
            )
        } == true
    }

    fun verifyDOBMonth(): Boolean {
        val record = verifiedRecord ?: return false
        val dob = record["date_of_birth"]?.toString()
        return dob?.let {
            val dobDate = Date(it.toTimeInMilli())
            dobMonth.equals(
                dobDate.toFullMonth(),
                ignoreCase = true
            )
        } == true
    }

    fun verifyDOBYear(): Boolean {
        val record = verifiedRecord ?: return false
        val dob = record["date_of_birth"]?.toString()
        return dob?.let {
            val dobDate = Date(it.toTimeInMilli())
            dobYear.equals(
                dobDate.toYear(),
                ignoreCase = true
            )
        } == true
    }

    fun verifyGender(): Boolean {
        val record = verifiedRecord ?: return false
        return gender.trim().equals(
            record["gender"]?.toString().orEmpty(),
            ignoreCase = true
        )
    }
}