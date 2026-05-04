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
import com.heartcare.agni.data.local.enums.LevelsEnum
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.model.report.StatRowData
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.cvd.records.CVDAssessmentRepository
import com.heartcare.agni.data.local.repository.healthfacility.HealthFacilityRepository
import com.heartcare.agni.data.local.repository.levels.LevelRepository
import com.heartcare.agni.data.local.repository.patient.PatientRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.screeningsite.ScreeningSiteRepository
import com.heartcare.agni.data.local.repository.vital.VitalRepository
import com.heartcare.agni.data.server.model.campaign.ScreeningSiteMasterResponse
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
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toMMMddyyyyDateRange
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val healthFacilityRepository: HealthFacilityRepository,
    private val levelRepository: LevelRepository,
    private val appointmentRepository: AppointmentRepository,
    private val cvdAssessmentRepository: CVDAssessmentRepository,
    private val vitalRepository: VitalRepository,
    private val screeningSiteRepository: ScreeningSiteRepository,
    preferenceRepository: PreferenceRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    val user = preferenceRepository.getUserDetails()!!

    var selectedTabIndex by mutableIntStateOf(0)
    var showDateRangeSheet by mutableStateOf(false)

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

    var campaignOptions: List<ScreeningSiteMasterResponse> by mutableStateOf(emptyList())
    var selectedCampaign: ScreeningSiteMasterResponse? by mutableStateOf(null)

    var facilityOptions : List<LevelResponse> by mutableStateOf(emptyList())
    var selectedFacility : LevelResponse? by mutableStateOf(null)

    var selectedDivisionType by mutableStateOf(LevelsEnum.AREA_COUNCIL.display)
    var divisionOptions: List<LevelResponse> by mutableStateOf(emptyList())
    var selectedDivision: LevelResponse? by mutableStateOf(null)

    var campaignPractitionerName by mutableStateOf("")
    var campaignContact by mutableStateOf("")
    var campaignDateRange by mutableStateOf("")
    var campaignLocation by mutableStateOf("")

    fun getMasterLists() {
        getCampaignList()
        getFacilityAndDivisionList()
    }

    private fun getCampaignList() {
        viewModelScope.launch {
            val campaigns = withContext(ioDispatcher) {
                screeningSiteRepository.getActiveScreeningSites().filter { site ->
                    site.staff.any { it.id == user.fhirId && it.isTeamLead }
                }
            }
            campaignOptions = campaigns
            selectedCampaign = campaigns.firstOrNull()

            getDataOfCampaign()
        }
    }

    private fun getFacilityAndDivisionList() {
        viewModelScope.launch {
            val facilities = withContext(ioDispatcher) {
                healthFacilityRepository.getHealthFacilityInLevelResponse()
            }
            facilityOptions = facilities
            selectedFacility = facilities.find { it.code == user.hospitalCode }
            getDivisionList()
            getDataOfFacility()
        }
    }

    private fun getDivisionList() {
        viewModelScope.launch {
            getDivisionOptions(false)
            val divisionData = withContext(ioDispatcher) {
                val userIslandId = facilityOptions.find { it.code == user.hospitalCode }?.precedingLevelId

                userIslandId?.let {
                    val userIsland = levelRepository.getLevelListByFhirIds(it)[0]

                    levelRepository.getLevelListByFhirIds(userIsland.precedingLevelId!!)[0]
                }
            }
            selectedDivision = divisionData
            getDataOfDivision()
        }
    }

    fun getDivisionOptions(resetSelection: Boolean = true) {
        viewModelScope.launch(ioDispatcher) {
            divisionOptions = levelRepository.getLevels(LevelsEnum.getCodeFromDisplay(selectedDivisionType))
            if (resetSelection) selectedDivision = null
        }
    }

    fun showSummary(): Boolean {
        return when (selectedTabIndex) {
            0 -> selectedCampaign != null
            1 -> selectedFacility != null
            2 -> selectedDivision != null
            else -> false
        }
    }

    fun getDataOfCampaign() {
        val teamLead = selectedCampaign?.staff?.firstOrNull { it.isTeamLead }
        campaignPractitionerName = teamLead?.name ?: ""

        campaignContact = listOfNotNull(
            teamLead?.email?.takeIf { it.isNotBlank() },
            teamLead?.mobile?.takeIf { it.isNotBlank() }
        ).joinToString(", ")

        val start = selectedCampaign?.fromDate?.toTimeInMilli()?.let { Date(it).toMMMddyyyyDateRange() }
        val end = selectedCampaign?.toDate?.toTimeInMilli()?.let { Date(it).toMMMddyyyyDateRange() }

        campaignDateRange = listOfNotNull(start, end).joinToString(" - ")

        campaignLocation = selectedCampaign?.location?.takeIf { it.isNotBlank() }
            ?: selectedCampaign?.areaCouncil?.takeIf { it.isNotBlank() }
                    ?: ""

        viewModelScope.launch(ioDispatcher) {
            val appointments = appointmentRepository.getAppointmentForCampaign(selectedCampaign?.id ?: "")

            screeningSiteState = getReportData(
                rangeType = screeningSiteState.selectedDateRangeLabel,
                startDate = screeningSiteState.dateRangeStart,
                endDate = screeningSiteState.dateRangeEnd,
                appointments = appointments
            )
        }
    }

    fun getDataOfFacility(
        rangeType: String = facilityState.selectedDateRangeLabel,
        startDate: Date = facilityState.dateRangeStart,
        endDate: Date = facilityState.dateRangeEnd
    ) {
        viewModelScope.launch(ioDispatcher) {
            val appointments = appointmentRepository.getAppointmentListByDateRange(
                startOfDay = startDate.time,
                endOfDay = endDate.time
            ).filter { appointmentResponseLocal ->
                (appointmentResponseLocal.status == AppointmentStatusEnum.IN_PROGRESS.value ||
                        appointmentResponseLocal.status == AppointmentStatusEnum.COMPLETED.value)
                        && appointmentResponseLocal.hospitalCode == selectedFacility?.code
            }

            facilityState = getReportData(
                rangeType = rangeType,
                startDate = startDate,
                endDate = endDate,
                appointments = appointments
            )
        }
    }

    fun getDataOfDivision(
        rangeType: String = divisionState.selectedDateRangeLabel,
        startDate: Date = divisionState.dateRangeStart,
        endDate: Date = divisionState.dateRangeEnd
    ) {
        viewModelScope.launch(ioDispatcher) {
            val patientIdsInDivision = patientRepository.getPatientIdsByDivision(
                divisionType = LevelsEnum.getCodeFromDisplay(selectedDivisionType),
                divisionId = selectedDivision?.fhirId ?: ""
            ).toSet()
            val appointments = appointmentRepository.getAppointmentListByDateRange(
                startOfDay = startDate.time,
                endOfDay = endDate.time
            ).filter { appointmentResponseLocal ->
                (appointmentResponseLocal.status == AppointmentStatusEnum.IN_PROGRESS.value ||
                        appointmentResponseLocal.status == AppointmentStatusEnum.COMPLETED.value) &&
                        appointmentResponseLocal.patientId in patientIdsInDivision
            }

            divisionState = getReportData(
                rangeType = rangeType,
                startDate = startDate,
                endDate = endDate,
                appointments = appointments
            )
        }
    }

    private suspend fun getReportData(
        rangeType: String,
        startDate: Date,
        endDate: Date,
        appointments: List<AppointmentResponseLocal>
    ): ReportUiState {
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
            selectedDateRangeLabel = rangeType,
            dateRangeStart = startDate,
            dateRangeEnd = endDate,
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
            cvdRiskStats = getCvdRiskStats(latestCVDList, patientMap),

            patientAndCVD = latestCVDList.map {
                val patient = patientMap[it.patientId]!!
                Pair(
                    patient.copy(
                        permanentAddress = patient.permanentAddress.copy(
                            province = getLevelNames(patient.permanentAddress.province),
                            island = getLevelNames(patient.permanentAddress.island),
                            areaCouncil = getLevelNames(patient.permanentAddress.areaCouncil),
                            village = patient.permanentAddress.village?.let { village ->
                                getLevelNames(village)
                            }
                        )
                    ),
                    it
                )
            }
        )

        return newState
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
        var endDate = Date(Date().toEndOfDay())
        var startDate = Date()
        when (rangeType) {
            DateRangeEnum.LAST_7_DAYS.label -> {
                startDate = Date(Date().plusMinusDays(-7).toTodayStartDate())
            }
            DateRangeEnum.LAST_30_DAYS.label -> {
                startDate = Date(Date().plusMinusDays(-30).toTodayStartDate())
            }
            DateRangeEnum.LAST_90_DAYS.label -> {
                startDate = Date(Date().plusMinusDays(-90).toTodayStartDate())
            }
            DateRangeEnum.CUSTOM_RANGE.label -> {
                startDate = start!!
                endDate = end!!
            }
        }
        when(selectedTabIndex) {
            1 -> getDataOfFacility(rangeType, startDate, endDate)
            2 -> getDataOfDivision(rangeType, startDate, endDate)
        }
    }

    private suspend fun getLevelNames(fhirId: String): String {
        return levelRepository.getLevelNameFromFhirId(fhirId)
    }
}
