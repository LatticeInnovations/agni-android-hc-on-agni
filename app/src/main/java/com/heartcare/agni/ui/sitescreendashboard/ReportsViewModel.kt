package com.heartcare.agni.ui.sitescreendashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.model.report.ServerStatDto
import com.heartcare.agni.data.local.model.report.StatRowData
import com.heartcare.agni.ui.theme.HighRiskCircle
import com.heartcare.agni.ui.theme.LowRiskCircle
import com.heartcare.agni.ui.theme.VeryHighRiskCircle2
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor() : BaseViewModel() {

    var selectedTabIndex by mutableIntStateOf(0)
    var showDateRangeSheet by mutableStateOf(false)
    var selectedDateRangeLabel by mutableStateOf("")

    val campaignOptions = listOf("Vila Central Outreach", "NCD Screening", "Hypertension follow-up")
    var selectedCampaign by mutableStateOf(campaignOptions.first())

    val facilityOptions = listOf("Luganville Hispital", "Lenakel Hospital", "Norsup Hospital")
    var selectedFacility by mutableStateOf(facilityOptions.first())

    val divisionTypeOptions = listOf("Province", "Island", "Area Council", "Village")
    var selectedDivisionType by mutableStateOf(divisionTypeOptions.first())

    val divisionNameOptions = listOf("Shefa province", "Torba province","Efate Island","Malampa Council","Pango Village")
    var selectedDivisionName by mutableStateOf(divisionNameOptions.first())


    var campaignPractitionerName by mutableStateOf("Dr. Sarah Naupa")
    var campaignContact by mutableStateOf("sarah@moh.vu, +678 55123")
    var campaignDateRange by mutableStateOf("Mar 1 - Mar 15, 2025")
    var campaignLocation by mutableStateOf("Port Vila Central")


    // Summary Card
    var totalScreened by mutableStateOf("1,247")
    var totalMale by mutableStateOf("664")
    var totalFemale by mutableStateOf("583")

    val ageGroups = listOf(
        "18-29" to "198",
        "30-44" to "412",
        "45-59" to "389",
        "60+" to "248"
    )


    var bmiStats = mutableListOf<StatRowData>()
    var bloodPressureStats = mutableListOf<StatRowData>()
    var smokingStats = mutableListOf<StatRowData>()
    var bloodSugarFastingStats = mutableListOf<StatRowData>()
    var bloodSugarRandomStats = mutableListOf<StatRowData>()
    var cholesterolStats = mutableListOf<StatRowData>()

    var cvdRiskStats = mutableListOf<StatRowData>()


    init {
        fetchBmiDataFromServer()
        fetchBloodPressureData()
        fetchSmokingData()
        fetchBloodSugarData()
        fetchCholesterolData()
        fetchCvdRiskData()
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

    fun updateDateRange(rangeType: String, start: String?, end: String?) {
        selectedDateRangeLabel = if (rangeType == "Custom range" && start != null && end != null) {
            "$start - $end"
        } else {
            rangeType
        }
    }
}
