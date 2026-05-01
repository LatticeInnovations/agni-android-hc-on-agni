package com.heartcare.agni.ui.patienteditscreen.basicinfo

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
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.identifier.IdentifierRepository
import com.heartcare.agni.data.local.repository.nationalId.NationalIdRepository
import com.heartcare.agni.data.local.repository.patient.PatientRepository
import com.heartcare.agni.data.server.model.patient.PatientIdentifier
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.constants.IdentificationConstants
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toFullMonth
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toMonthInteger
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toYear
import com.heartcare.agni.utils.regex.AgeRegex
import com.heartcare.agni.utils.regex.DobRegex
import com.heartcare.agni.utils.regex.OnlyNumberRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EditBasicInformationViewModel @Inject constructor(
    val patientRepository: PatientRepository,
    val genericRepository: GenericRepository,
    val identifierRepository: IdentifierRepository,
    private val nationalIdRepository: NationalIdRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)

    val onlyNumbers = OnlyNumberRegex.onlyNumbers
    val ageRegex = AgeRegex.ageRegex
    val dobRegex = DobRegex.dobRegex

    internal val maxNameLength = 50
    internal val maxPhoneNumberLength = 15

    internal var firstName by mutableStateOf("")
    internal var lastName by mutableStateOf("")
    internal var phoneNumber by mutableStateOf("")
    internal var email by mutableStateOf("")
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

    val identifierList = mutableListOf<PatientIdentifier>()

    val maxHospitalIdLength = 6
    val maxNationalIdLength = 7

    var hospitalId by mutableStateOf("")
    var nationalId by mutableStateOf("")
    var nationalIdUse by mutableStateOf("")
    var isVerifyClicked by mutableStateOf(false)
    var isNationalIdVerified by mutableStateOf(false)
    var isHospitalIdValid by mutableStateOf(false)

    //temp var
    var firstNameTemp by mutableStateOf("")
    var lastNameTemp by mutableStateOf("")
    var phoneNumberTemp by mutableStateOf("")
    var emailTemp by mutableStateOf("")
    var dobAgeSelectorTemp by mutableStateOf("dob")
    var dobDayTemp by mutableStateOf("")
    var dobMonthTemp by mutableStateOf("")
    var dobYearTemp by mutableStateOf("")
    var yearsTemp by mutableStateOf("")
    var monthsTemp by mutableStateOf("")
    var daysTemp by mutableStateOf("")
    var genderTemp by mutableStateOf("")
    var birthDate by mutableStateOf("")
    var motherNameTemp by mutableStateOf("")
    var fatherNameTemp by mutableStateOf("")
    var spouseNameTemp by mutableStateOf("")
    var isPersonDeceasedTemp by mutableIntStateOf(0)
    var selectedDeceasedReasonTemp by mutableStateOf("")

    var hospitalIdTemp by mutableStateOf("")
    var nationalIdTemp by mutableStateOf("")
    var nationalIdUseTemp by mutableStateOf("")
    var isNationalIdVerifiedTemp by mutableStateOf(false)
    var isVerifyClickedTemp by mutableStateOf(false)

    var verifiedRecord: Map<String, Any?>? = null

    var monthsList = mutableStateListOf(
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
    var emailError by mutableStateOf(false)

    fun basicInfoValidation(): Boolean {
        return firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                motherName.isNotBlank() &&
                !verifyDOB() &&
                !verifyAge() &&
                !isPhoneValid &&
                !emailError &&
                gender.isNotBlank() &&
                !isHospitalIdValid &&
                (isVerifyClicked || nationalId.isBlank())
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

    fun splitDOB(dob: String): Triple<Int, String, Int> {
        val dobParts = dob.trim().split("-")
        val year = dobParts[0].toInt()
        val month = dobParts[1].toInt()
        val day = dobParts[2].toInt()
        return Triple(day, monthsList[month - 1], year)
    }

    fun splitAge(dob: String): Triple<Int, Int, Int> {
        val dobParts = dob.trim().split("-")
        val year = dobParts[0].toInt()
        val month = dobParts[1].toInt()
        val day = dobParts[2].toInt()

        return Triple(day, month, year)
    }

    fun checkIsEdit(): Boolean {
        return firstName.trim() != firstNameTemp ||
                lastName.trim() != lastNameTemp ||
                phoneNumber != phoneNumberTemp ||
                email != emailTemp ||
                dobAgeSelector != dobAgeSelectorTemp ||
                dobDay != dobDayTemp ||
                dobMonth != dobMonthTemp ||
                dobYear != dobYearTemp ||
                gender != genderTemp ||
                motherName.trim() != motherNameTemp ||
                fatherName.trim() != fatherNameTemp ||
                spouseName.trim() != spouseNameTemp ||
                isPersonDeceased != isPersonDeceasedTemp ||
                selectedDeceasedReason != selectedDeceasedReasonTemp ||
                hospitalId != hospitalIdTemp ||
                nationalId != nationalIdTemp ||
                isNationalIdVerified != isNationalIdVerifiedTemp ||
                nationalIdUse != nationalIdUseTemp ||
                isVerifyClicked != isVerifyClickedTemp
    }

    fun revertChanges(): Boolean {
        firstName = firstNameTemp
        lastName = lastNameTemp
        phoneNumber = phoneNumberTemp
        email = emailTemp
        dobAgeSelector = dobAgeSelectorTemp
        dobDay = dobDayTemp
        dobMonth = dobMonthTemp
        dobYear = dobYearTemp
        days = ""
        months = ""
        years = ""
        gender = genderTemp
        motherName = motherNameTemp
        fatherName = fatherNameTemp
        spouseName = spouseNameTemp
        isPersonDeceased = isPersonDeceasedTemp
        selectedDeceasedReason = selectedDeceasedReasonTemp
        isFirstNameValid = false
        isLastNameValid = false
        isMotherNameValid = false
        isPhoneValid = false
        isAgeDaysValid = false
        isAgeMonthsValid = false
        isAgeYearsValid = false
        emailError = false

        hospitalId = hospitalIdTemp
        nationalId = nationalIdTemp
        isHospitalIdValid = false
        isNationalIdVerified = isNationalIdVerifiedTemp
        nationalIdUse = nationalIdUseTemp
        isVerifyClicked = isVerifyClickedTemp
        return true
    }


    fun updateBasicInfo(patientResponse: PatientResponse) {

        viewModelScope.launch(Dispatchers.IO) {
            val toBeDeletedList = mutableListOf<PatientIdentifier>()
            if (hospitalIdTemp != hospitalId) {
                toBeDeletedList.add(
                    PatientIdentifier(
                        identifierType = IdentificationConstants.HOSPITAL_ID,
                        identifierNumber = hospitalIdTemp,
                        code = null,
                        use = null
                    )
                )
            }

            if (nationalIdTemp != nationalId) {
                toBeDeletedList.add(
                    PatientIdentifier(
                        identifierType = IdentificationConstants.NATIONAL_ID,
                        identifierNumber = nationalIdTemp,
                        code = null,
                        use = nationalIdUseTemp
                    )
                )
            }

            identifierRepository.deleteIdentifier(
                patientIdentifier = toBeDeletedList.toTypedArray(),
                patientId = patientResponse.id
            )

            val response = patientRepository.updatePatientData(patientResponse = patientResponse)
            if (response > 0) {
                identifierRepository.insertIdentifierList(patientResponse = patientResponse)
                if (patientResponse.fhirId != null) {
                    genericRepository.insertOrUpdatePatientPatchEntity(
                        patientFhirId = patientResponse.fhirId,
                        patientResponse = patientResponse
                    )
                } else {
                    genericRepository.insertPatient(
                        patientResponse
                    )
                }
            }
        }
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