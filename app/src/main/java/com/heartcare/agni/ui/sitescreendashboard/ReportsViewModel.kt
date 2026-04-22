package com.heartcare.agni.ui.sitescreendashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.enums.DateRangeEnum
import com.heartcare.agni.data.local.enums.GenderEnum
import com.heartcare.agni.data.local.model.report.ServerStatDto
import com.heartcare.agni.data.local.model.report.StatRowData
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.healthfacility.HealthFacilityRepository
import com.heartcare.agni.data.local.repository.patient.PatientRepository
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.ui.theme.HighRiskCircle
import com.heartcare.agni.ui.theme.LowRiskCircle
import com.heartcare.agni.ui.theme.VeryHighRiskCircle2
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.plusMinusDays
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAge
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.Date

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val healthFacilityRepository: HealthFacilityRepository,
    private val appointmentRepository: AppointmentRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    var selectedTabIndex by mutableIntStateOf(0)
    var showDateRangeSheet by mutableStateOf(false)
    var selectedDateRangeLabel by mutableStateOf(DateRangeEnum.LAST_7_DAYS.label)
    var dateRangeStart by mutableStateOf(Date(Date().plusMinusDays(-7).toTodayStartDate()))
    var dateRangeEnd by mutableStateOf(Date(Date().toEndOfDay()))

    val campaignOptions = listOf("Vila Central Outreach", "NCD Screening", "Hypertension follow-up")
    var selectedCampaign by mutableStateOf(campaignOptions.first())

    var facilityOptions : List<LevelResponse> by mutableStateOf(emptyList())
    var selectedFacility : LevelResponse? by mutableStateOf(null)

    val divisionTypeOptions = listOf("Province", "Island", "Area Council", "Village")
    var selectedDivisionType by mutableStateOf(divisionTypeOptions.first())

    val divisionNameOptions = listOf("Shefa province", "Torba province","Efate Island","Malampa Council","Pango Village")
    var selectedDivisionName by mutableStateOf(divisionNameOptions.first())


    var campaignPractitionerName by mutableStateOf("Dr. Sarah Naupa")
    var campaignContact by mutableStateOf("sarah@moh.vu, +678 55123")
    var campaignDateRange by mutableStateOf("Mar 1 - Mar 15, 2025")
    var campaignLocation by mutableStateOf("Port Vila Central")


    // Summary Card
    var totalScreened by mutableStateOf(0)
    var totalMale by mutableStateOf(0)
    var totalFemale by mutableStateOf(0)
    var totalOther by mutableStateOf(0)

    var ageGroups: List<Pair<String, String>> = emptyList()

    var bmiStats = mutableListOf<StatRowData>()
    var bloodPressureStats = mutableListOf<StatRowData>()
    var smokingStats = mutableListOf<StatRowData>()
    var bloodSugarFastingStats = mutableListOf<StatRowData>()
    var bloodSugarRandomStats = mutableListOf<StatRowData>()
    var cholesterolStats = mutableListOf<StatRowData>()

    var cvdRiskStats = mutableListOf<StatRowData>()


    init {
        getMasterLists()
        fetchBmiDataFromServer()
        fetchBloodPressureData()
        fetchSmokingData()
        fetchBloodSugarData()
        fetchCholesterolData()
        fetchCvdRiskData()
    }

    private fun getMasterLists() {
        viewModelScope.launch(ioDispatcher) {
            facilityOptions = healthFacilityRepository.getHealthFacilityInLevelResponse()
        }
    }

    fun showSummary(): Boolean {
        return when (selectedTabIndex) {
            0 -> true
            1 -> selectedFacility != null
            2 -> true
            else -> false
        }
    }

    fun getDataOfFacility(hospitalCode: String) {
        viewModelScope.launch(ioDispatcher) {
            val appointments = appointmentRepository.getAppointmentListByDateRange(
                startOfDay = dateRangeStart.time,
                endOfDay = dateRangeEnd.time
            ).filter { appointmentResponseLocal ->
                (appointmentResponseLocal.status == AppointmentStatusEnum.IN_PROGRESS.value ||
                        appointmentResponseLocal.status == AppointmentStatusEnum.COMPLETED.value)
                        && appointmentResponseLocal.hospitalCode == hospitalCode
            }

            val patients = patientRepository.getPatientById(*appointments.map { it.patientId }.toTypedArray())
            totalScreened = patients.size
            totalMale = patients.filter { it.gender == GenderEnum.MALE.value }.size
            totalFemale = patients.filter { it.gender == GenderEnum.FEMALE.value }.size
            totalOther = patients.filter { it.gender == GenderEnum.OTHER.value }.size

            ageGroups = listOf(
                "18-29" to patients.filter { it.birthDate.toTimeInMilli().toAge() in 18..29 }.size.toString(),
                "30-44" to patients.filter { it.birthDate.toTimeInMilli().toAge() in 30..44 }.size.toString(),
                "45-59" to patients.filter { it.birthDate.toTimeInMilli().toAge() in 45..59 }.size.toString(),
                "60+" to patients.filter { it.birthDate.toTimeInMilli().toAge() >= 60 }.size.toString()
            )
        }
    }

    private fun fetchBmiDataFromServer() {
        val mockNetworkResponse = listOf(
            ServerStatDto(
                "Underweight (≤ 18.5 kg/m²)",
                maleCount = 32,
                femaleCount = 30,
                severityLevel = 1
            ),
            ServerStatDto(
                "Normal (18.6 - 24.9 kg/m²)",
                maleCount = 123,
                femaleCount = 110,
                severityLevel = 0
            ),
            ServerStatDto(
                "Overweight (25.0 - 29.9 kg/m²)",
                maleCount = 54,
                femaleCount = 23,
                severityLevel = 1
            ),
            ServerStatDto(
                "Obese (≥ 30.0 kg/m²)",
                maleCount = 23,
                femaleCount = 34,
                severityLevel = 2
            )
        )
        bmiStats = mapServerDataToUiModel(
            totalScreened = 429,
            rawData = mockNetworkResponse
        ).toMutableStateList()
    }

    private fun mapServerDataToUiModel(
        totalScreened: Int,
        rawData: List<ServerStatDto>
    ): List<StatRowData> {
        return rawData.map { dto ->
            val totalForCategory = dto.maleCount + dto.femaleCount
            val percentage =
                if (totalScreened > 0) totalForCategory.toFloat() / totalScreened.toFloat() else 0f
            val percentageInt = (percentage * 100).toInt()
            val color = when (dto.severityLevel) {
                0 -> LowRiskCircle
                1 -> HighRiskCircle
                else -> VeryHighRiskCircle2
            }
            StatRowData(
                label = dto.categoryName,
                valueStr = "$percentageInt% (F ${dto.femaleCount}, M ${dto.maleCount})",
                progress = percentage,
                progressColor = color
            )
        }
    }


    private fun fetchBloodPressureData() {
        bloodPressureStats = mutableListOf(
            StatRowData("Normal (< 140/90 mmHg)", "43% (F110, M 123)", 0.43f, LowRiskCircle),
            StatRowData("High (140/90 - 159/99 mmHg)", "32% (F 23, M 54)", 0.32f, HighRiskCircle),
            StatRowData(
                "Very High (≥ 160/100 mmHg)",
                "20% (F 34, M 23)",
                0.20f,
                VeryHighRiskCircle2
            )
        )
    }


    private fun fetchSmokingData() {
        smokingStats = mutableListOf(
            StatRowData("Yes", "57% (F 30, M 32)", 0.57f, VeryHighRiskCircle2),
            StatRowData("No", "43% (F 110, M 123)", 0.43f, LowRiskCircle)
        )
    }

    private fun fetchBloodSugarData() {
        bloodSugarFastingStats = mutableListOf(
            StatRowData("Normal", "43% (F 30, M 32)", 0.43f, LowRiskCircle),
            StatRowData("Above Normal", "32% (F 32, M 12)", 0.32f, HighRiskCircle)
        )
        bloodSugarRandomStats = mutableListOf(
            StatRowData("Normal", "43% (F 23, M 12)", 0.43f, LowRiskCircle),
            StatRowData("Above Normal", "32% (F 45, M 23)", 0.32f, HighRiskCircle)
        )
    }

    private fun fetchCholesterolData() {
        cholesterolStats = mutableListOf(
            StatRowData("Normal", "43% (F110, M 123)", 0.43f, LowRiskCircle),
            StatRowData("Above Normal", "32% (F 23, M 54)", 0.32f, HighRiskCircle)
        )
    }

    private fun fetchCvdRiskData() {
        cvdRiskStats = mutableListOf(
            StatRowData("Low (<10%)", "43% (F110, M 123)", 0.43f, LowRiskCircle),
            StatRowData("Moderate (10-20%)", "32% (F 23, M 54)", 0.32f, HighRiskCircle),
            StatRowData("High (>20%)", "20% (F 34, M 23)", 0.20f, VeryHighRiskCircle2)
        )
    }

    fun updateDateRange(rangeType: String, start: Date?, end: Date?) {
        selectedDateRangeLabel = rangeType
        dateRangeEnd = Date(Date().toEndOfDay())
        when (rangeType) {
            DateRangeEnum.LAST_7_DAYS.label -> {
                dateRangeStart = Date(Date().plusMinusDays(-7).toTodayStartDate())
            }
            DateRangeEnum.LAST_30_DAYS.label -> {
                dateRangeStart = Date(Date().plusMinusDays(-30).toTodayStartDate())
            }
            DateRangeEnum.LAST_90_DAYS.label -> {
                dateRangeStart = Date(Date().plusMinusDays(-90).toTodayStartDate())
            }
            DateRangeEnum.CUSTOM_RANGE.label -> {
                dateRangeStart = start!!
                dateRangeEnd = end!!
            }
        }

        getDataOfFacility(selectedFacility!!.code)
    }
}
