package com.heartcare.agni.ui.sitescreendashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.enums.BloodSugarCategory
import com.heartcare.agni.data.local.enums.BloodSugarType
import com.heartcare.agni.data.local.enums.BmiCategory
import com.heartcare.agni.data.local.enums.BpCategory
import com.heartcare.agni.data.local.enums.CholesterolCategory
import com.heartcare.agni.data.local.enums.CvdRiskCategory
import com.heartcare.agni.data.local.enums.DateRangeEnum
import com.heartcare.agni.data.local.enums.GenderEnum
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.data.local.model.report.StatRowData
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.cvd.records.CVDAssessmentRepository
import com.heartcare.agni.data.local.repository.healthfacility.HealthFacilityRepository
import com.heartcare.agni.data.local.repository.patient.PatientRepository
import com.heartcare.agni.data.local.repository.vital.VitalRepository
import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.levels.LevelResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.ui.sitescreendashboard.state.ReportUiState
import com.heartcare.agni.ui.vitalsscreen.enums.BGEnum
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
    private val cvdAssessmentRepository: CVDAssessmentRepository,
    private val vitalRepository: VitalRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    var selectedTabIndex by mutableIntStateOf(0)
    var showDateRangeSheet by mutableStateOf(false)
    var selectedDateRangeLabel by mutableStateOf(DateRangeEnum.LAST_7_DAYS.label)
    var dateRangeStart by mutableStateOf(Date(Date().plusMinusDays(-7).toTodayStartDate()))
    var dateRangeEnd by mutableStateOf(Date(Date().toEndOfDay()))

    var facilityState by mutableStateOf(ReportUiState())
    var screeningSiteState by mutableStateOf(ReportUiState())
    var divisionState by mutableStateOf(ReportUiState())
    val currentState: ReportUiState
        get() = when (selectedTabIndex) {
            0 -> screeningSiteState
            1 -> facilityState
            2 -> divisionState
            else -> ReportUiState()
        }

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

    init {
        getMasterLists()
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
            val patientMap = patients.associateBy { it.id }
            val cvdList = cvdAssessmentRepository.getCVDRecordByAppointmentIds(*appointments.map { it.uuid }.toTypedArray())
            val latestCVDList = cvdList
                .groupBy {
                    it.patientId
                }.map { (_, cvd) ->
                    cvd.maxBy { it.createdOn }
                }

            val latestCholesterolCVDList = cvdList
                .filter { it.cholesterol != null && !it.cholesterolUnit.isNullOrBlank() }
                .groupBy {
                    it.patientId
                }.map { (_, cvd) ->
                    cvd.maxBy { it.createdOn }
                }

            val latestVitalsList = vitalRepository.getLastVitalByAppointmentId(*appointments.map { it.uuid }.toTypedArray())
                .filter { it.bloodGlucose != null }
                .groupBy {
                    it.patientId
                }.map { (_, vitals) ->
                    vitals.maxBy { it.appUpdatedDate }
                }

            val fastingVitalsList = latestVitalsList.filter { it.bloodGlucose!!.type == BGEnum.FASTING.value }
            val randomVitalsList = latestVitalsList.filter { it.bloodGlucose!!.type == BGEnum.RANDOM.value }

            val newState = ReportUiState(
                totalScreened = patients.size,
                totalMale = patients.count { it.gender == GenderEnum.MALE.value },
                totalFemale = patients.count { it.gender == GenderEnum.FEMALE.value },
                totalOther = patients.count { it.gender == GenderEnum.OTHER.value },

                ageGroups = listOf(
                    "18-29" to patients.filter { it.birthDate.toTimeInMilli().toAge() in 18..29 }.size.toString(),
                    "30-44" to patients.filter { it.birthDate.toTimeInMilli().toAge() in 30..44 }.size.toString(),
                    "45-59" to patients.filter { it.birthDate.toTimeInMilli().toAge() in 45..59 }.size.toString(),
                    "60+" to patients.filter { it.birthDate.toTimeInMilli().toAge() >= 60 }.size.toString()
                ),

                bmiTotal = latestCVDList.size,
                bmiStats = getBmiStats(latestCVDList, patientMap),

                bloodPressureTotal = latestCVDList.size,
                bloodPressureStats = getBloodPressureStats(latestCVDList, patientMap),

                smokingTotal = latestCVDList.size,
                smokingStats = getSmokingStats(latestCVDList, patientMap),

                bloodSugarFastingTotal = fastingVitalsList.size,
                bloodSugarFastingStats = getBloodSugarStats(
                    fastingVitalsList,
                    patientMap,
                    BloodSugarType.FASTING
                ),

                bloodSugarRandomTotal = randomVitalsList.size,
                bloodSugarRandomStats = getBloodSugarStats(
                    randomVitalsList,
                    patientMap,
                    BloodSugarType.RANDOM
                ),

                cholesterolTotal = latestCholesterolCVDList.size,
                cholesterolStats = getCholesterolStats(latestCholesterolCVDList, patientMap),

                cvdRiskTotal = latestCVDList.size,
                cvdRiskStats = getCvdRiskStats(latestCVDList, patientMap)
            )

            facilityState = newState
        }
    }

    private inline fun <T, E> buildStats(
        source: List<T>,
        total: Int,
        categories: List<E>,
        crossinline categorySelector: (T) -> E?,
        crossinline patientIdSelector: (T) -> String,
        crossinline label: (E) -> String,
        crossinline color: (E) -> Color,
        patientMap: Map<String, PatientResponse>
    ): MutableList<StatRowData> {

        return categories.map { category ->

            val filtered = source.filter { categorySelector(it) == category }

            val patients = filtered.mapNotNull { patientMap[patientIdSelector(it)] }

            val male = patients.count { it.gender == GenderEnum.MALE.value }
            val female = patients.count { it.gender == GenderEnum.FEMALE.value }
            val other = patients.count { it.gender == GenderEnum.OTHER.value }

            val size = patients.size

            StatRowData(
                label = label(category),
                maleCount = male,
                femaleCount = female,
                otherCount = other,
                percentage = if (total == 0) 0 else (size * 100 / total),
                progress = if (total == 0) 0f else size.toFloat() / total,
                progressColor = color(category)
            )
        }.toMutableList()
    }

    private fun getBmiStats(
        cvdList: List<CVDResponse>,
        patientMap: Map<String, PatientResponse>
    ): List<StatRowData> {
        return buildStats(
            source = cvdList,
            total = cvdList.size,
            categories = BmiCategory.entries,
            categorySelector = { cvd -> BmiCategory.entries.find { cvd.bmi in it.min..it.max } },
            patientIdSelector = { it.patientId },
            label = { it.label },
            color = { it.color },
            patientMap = patientMap
        )
    }

    private fun getBloodPressureStats(
        cvdList: List<CVDResponse>,
        patientMap: Map<String, PatientResponse>
    ): List<StatRowData> {
        return buildStats(
            source = cvdList,
            total = cvdList.size,
            categories = BpCategory.entries,
            categorySelector = { BpCategory.from(it.bpSystolic, it.bpDiastolic) },
            patientIdSelector = { it.patientId },
            label = { it.label },
            color = { it.color },
            patientMap = patientMap
        )
    }

    private fun getSmokingStats(
        cvdList: List<CVDResponse>,
        patientMap: Map<String, PatientResponse>
    ): List<StatRowData> {
        return buildStats(
            source = cvdList,
            total = cvdList.size,
            categories = YesNoEnum.entries,
            categorySelector = { YesNoEnum.entries.find { e -> e.code == it.smoker } },
            patientIdSelector = { it.patientId },
            label = { it.display },
            color = { it.color },
            patientMap = patientMap
        )
    }

    private fun getBloodSugarStats(
        vitalsList: List<VitalResponse>,
        patientMap: Map<String, PatientResponse>,
        type: BloodSugarType
    ): List<StatRowData> {
        return buildStats(
            source = vitalsList,
            total = vitalsList.size,
            categories = BloodSugarCategory.entries,
            categorySelector = {
                BloodSugarCategory.from(
                    it.bloodGlucose!!.value,
                    it.bloodGlucose.unit,
                    type
                )
            },
            patientIdSelector = { it.patientId },
            label = { it.label },
            color = { it.color },
            patientMap = patientMap
        )
    }

    private fun getCholesterolStats(
        cvdList: List<CVDResponse>,
        patientMap: Map<String, PatientResponse>
    ): List<StatRowData> {
        return buildStats(
            source = cvdList,
            total = cvdList.size,
            categories = CholesterolCategory.entries,
            categorySelector = {
                CholesterolCategory.from(it.cholesterol!!, it.cholesterolUnit!!)
            },
            patientIdSelector = { it.patientId },
            label = { it.label },
            color = { it.color },
            patientMap = patientMap
        )
    }

    private fun getCvdRiskStats(
        cvdList: List<CVDResponse>,
        patientMap: Map<String, PatientResponse>
    ): List<StatRowData> {
        return buildStats(
            source = cvdList,
            total = cvdList.size,
            categories = CvdRiskCategory.entries,
            categorySelector = { cvd ->
                CvdRiskCategory.entries.find { it.matches(cvd.risk) }
            },
            patientIdSelector = { it.patientId },
            label = { it.label },
            color = { it.color },
            patientMap = patientMap
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
        selectedFacility?.code?.let { getDataOfFacility(it) }
    }
}
