package com.heartcare.agni.ui.historyandtests.risk.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.enums.FatFrequency.Companion.fatFrequencyCodeFromDisplay
import com.heartcare.agni.data.local.enums.FatFrequency.Companion.fatFrequencyDisplayFromCode
import com.heartcare.agni.data.local.enums.FatType
import com.heartcare.agni.data.local.enums.FatType.Companion.fatTypeCodeFromDisplay
import com.heartcare.agni.data.local.enums.FatType.Companion.fatTypeDisplayFromCode
import com.heartcare.agni.data.local.enums.FruitJuiceFrequency.Companion.fruitJuiceFrequencyCodeFromDisplay
import com.heartcare.agni.data.local.enums.FruitJuiceFrequency.Companion.fruitJuiceFrequencyDisplayFromCode
import com.heartcare.agni.data.local.enums.KnowEnum
import com.heartcare.agni.data.local.enums.SaltAmountEnum.Companion.saltAmountCodeFromDisplay
import com.heartcare.agni.data.local.enums.SaltAmountEnum.Companion.saltAmountDisplayFromCode
import com.heartcare.agni.data.local.enums.SaltFrequencyEnum.Companion.saltFrequencyCodeFromDisplay
import com.heartcare.agni.data.local.enums.SaltFrequencyEnum.Companion.saltFrequencyDisplayFromCode
import com.heartcare.agni.data.local.enums.SoftDrinkFrequency.Companion.softDrinkFrequencyCodeFromDisplay
import com.heartcare.agni.data.local.enums.SoftDrinkFrequency.Companion.softDrinkFrequencyDisplayFromCode
import com.heartcare.agni.data.local.enums.TobaccoProduct
import com.heartcare.agni.data.local.enums.TobaccoProduct.Companion.tobaccoTypeCodeFromDisplay
import com.heartcare.agni.data.local.enums.TobaccoProduct.Companion.tobaccoTypeDisplayFromCode
import com.heartcare.agni.data.local.enums.YesNoEnum
import com.heartcare.agni.data.local.enums.YesNoEnum.Companion.booleanFromDisplay
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.cvd.records.CVDAssessmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.risk.RiskFactorRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.risk.AlcoholResponse
import com.heartcare.agni.data.server.model.risk.FatAndOilResponse
import com.heartcare.agni.data.server.model.risk.FruitsVegetablesResponse
import com.heartcare.agni.data.server.model.risk.MealsOutsideHomeResponse
import com.heartcare.agni.data.server.model.risk.PhysicalActivityResponse
import com.heartcare.agni.data.server.model.risk.RiskFactorResponse
import com.heartcare.agni.data.server.model.risk.SaltResponse
import com.heartcare.agni.data.server.model.risk.SugarResponse
import com.heartcare.agni.data.server.model.risk.TobaccoResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.heartcare.agni.utils.common.Queries.updatePatientLastUpdated
import com.heartcare.agni.utils.converters.responseconverter.NameConverter.getFullName
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.isToday
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import kotlin.text.toInt

@HiltViewModel
class AddRiskFactorViewModel @Inject constructor(
    private val riskFactorRepository: RiskFactorRepository,
    private val cvdAssessmentRepository: CVDAssessmentRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    val user = preferenceRepository.getUserDetails()!!
    var patient by mutableStateOf<PatientResponse?>(null)
    var isLaunched by mutableStateOf(false)
    var appointmentResponseLocal by mutableStateOf<AppointmentResponseLocal?>(null)

    var todayRiskFactor by mutableStateOf<RiskFactorResponse?>(null)

    var useTobacco by mutableStateOf("")
    var tobaccoType by mutableStateOf("")
    var otherTobacco by mutableStateOf("")
    var otherTobaccoError by mutableStateOf(false)
    var tobaccoQuantity by mutableStateOf("")
    var tobaccoQuantityError by mutableStateOf(false)
    var quantityOptions = listOf("Sticks", "Times")
    var selectedQuantityOption by mutableIntStateOf(0)
    var startAge by mutableStateOf("")
    var startAgeError by mutableStateOf(false)
    var startAgeErrorMsg by mutableStateOf("")
    var willingToQuit by mutableStateOf("")

    var consumedWithin30Days by mutableStateOf("")
    var alcoholQ1 by mutableStateOf("")
    var alcoholQ1Error by mutableStateOf(false)
    var alcoholQ2 by mutableStateOf("")
    var alcoholQ2Error by mutableStateOf(false)
    var alcoholQ3 by mutableStateOf("")
    var alcoholQ3Error by mutableStateOf(false)

    var consumptionInWeek by mutableStateOf("")
    var fruitsDays by mutableStateOf("")
    var fruitsDaysError by mutableStateOf(false)
    var vegetablesDays by mutableStateOf("")
    var vegetablesDaysError by mutableStateOf(false)
    var fruitServings by mutableStateOf("")
    var fruitServingsError by mutableStateOf(false)
    var vegetableServings by mutableStateOf("")
    var vegetableServingsError by mutableStateOf(false)

    var weeklyEngagement by mutableStateOf("")
    var vigorousDays by mutableStateOf("")
    var vigorousDaysError by mutableStateOf(false)
    var moderateDays by mutableStateOf("")
    var moderateDaysError by mutableStateOf(false)
    var vigorousTime by mutableStateOf("")
    var vigorousTimeError by mutableStateOf(false)
    var moderateTime by mutableStateOf("")
    var moderateTimeError by mutableStateOf(false)

    var saltAmount by mutableStateOf("")
    var saltAddCooking by mutableStateOf("")
    var saltAddMeal by mutableStateOf("")
    var saltProcessedFood by mutableStateOf("")

    var oilUsed by mutableStateOf("")
    var fatFoodFrequency by mutableStateOf("")
    var otherFatAndOils by mutableStateOf("")
    var otherFatAndOilsError by mutableStateOf(false)

    var softDrinkFrequency by mutableStateOf("")
    var juiceFrequency by mutableStateOf("")

    var eatsOut by mutableStateOf(KnowEnum.DO_NOT_KNOW.display)
    var mealsPerWeek by mutableStateOf("")
    var mealsPerWeekError by mutableStateOf(false)

    fun resetTobaccoValues() {
        tobaccoType = ""
        otherTobacco = ""
        otherTobaccoError = false
        tobaccoQuantity = ""
        tobaccoQuantityError = false
        selectedQuantityOption = 0
        startAge = ""
        startAgeError = false
        startAgeErrorMsg = ""
        willingToQuit = ""
    }

    fun resetAlcoholValues() {
        alcoholQ1 = ""
        alcoholQ1Error = false
        alcoholQ2 = ""
        alcoholQ2Error = false
        alcoholQ3 = ""
        alcoholQ3Error = false
    }

    fun resetFruitsVegetablesValues() {
        fruitsDays = ""
        fruitsDaysError = false
        vegetablesDays = ""
        vegetablesDaysError = false
        fruitServings = ""
        fruitServingsError = false
        vegetableServings = ""
        vegetableServingsError = false
    }

    fun resetPhysicalActivityValues() {
        vigorousDays = ""
        vigorousDaysError = false
        moderateDays = ""
        moderateDaysError = false
        vigorousTime = ""
        vigorousTimeError = false
        moderateTime = ""
        moderateTimeError = false
    }

    fun isValid(): Boolean {
        return isTobaccoValid()
                && isAlcoholValid()
                && isDietValid()
                && isPhysicalActivityValid()
                && isFatTypeValid()
                && isEatingOutValid()
    }

    private fun isTobaccoValid(): Boolean {
        if (useTobacco != YesNoEnum.YES.display) return true

        return tobaccoType.isNotBlank()
                && (tobaccoType != TobaccoProduct.OTHER.display || otherTobacco.isNotBlank())
                && tobaccoQuantity.isNotBlank()
                && !tobaccoQuantityError
                && startAge.isNotBlank()
                && !startAgeError
                && willingToQuit.isNotBlank()
    }

    private fun isAlcoholValid(): Boolean {
        if (consumedWithin30Days != YesNoEnum.YES.display) return true

        return alcoholQ1.isNotBlank()
                && alcoholQ2.isNotBlank()
                && alcoholQ3.isNotBlank()
                && !alcoholQ1Error
                && !alcoholQ2Error
                && !alcoholQ3Error
    }

    private fun isDietValid(): Boolean {
        if (consumptionInWeek != YesNoEnum.YES.display) return true

        return fruitsDays.isNotBlank()
                && vegetablesDays.isNotBlank()
                && fruitServings.isNotBlank()
                && vegetableServings.isNotBlank()
                && !fruitsDaysError
                && !vegetablesDaysError
                && !fruitServingsError
                && !vegetableServingsError
    }

    private fun isPhysicalActivityValid(): Boolean {
        if (weeklyEngagement != YesNoEnum.YES.display) return true

        return vigorousDays.isNotBlank()
                && vigorousTime.isNotBlank()
                && moderateDays.isNotBlank()
                && moderateTime.isNotBlank()
                && !vigorousDaysError
                && !vigorousTimeError
                && !moderateDaysError
                && !moderateTimeError
    }

    private fun isFatTypeValid(): Boolean {
        return oilUsed != FatType.OTHERS.display || otherFatAndOils.isNotBlank()
    }

    private fun isEatingOutValid(): Boolean {
        return eatsOut != KnowEnum.KNOW.display || (mealsPerWeek.isNotBlank() && !mealsPerWeekError)
    }

    fun getTodayRiskFactor(patientId: String) {
        viewModelScope.launch(ioDispatcher) {
            todayRiskFactor = riskFactorRepository.getRiskFactorRecords(patientId)
                .firstOrNull { isToday(it.appUpdatedDate) }

            todayRiskFactor?.let { rf ->
                mapTobacco(rf.tobacco)
                mapAlcohol(rf.alcohol)
                mapFruitsVegetables(rf.fruitsVegetables)
                mapPhysicalActivity(rf.physicalActivity)
                mapSalt(rf.salt)
                mapFatAndOil(rf.fatAndOil)
                mapSugar(rf.sugar)
                mapMealsOutside(rf.mealsOutsideHome)
            }

            if (todayRiskFactor == null) {
                cvdAssessmentRepository.getCVDRecord(patient!!.id).firstOrNull()?.let { record ->
                    useTobacco = YesNoEnum.displayFromCode(record.smoker)
                }
            }
        }
    }

    private fun mapTobacco(tobacco: TobaccoResponse?) {
        tobacco ?: return
        useTobacco = if (tobacco.tobaccoUser) YesNoEnum.YES.display else YesNoEnum.NO.display
        tobacco.tobaccoItemType?.let { tobaccoType = tobaccoTypeDisplayFromCode(it) }
        otherTobacco = tobacco.tobaccoOther.orEmpty()
        tobaccoQuantity = tobacco.consumptionAmount?.toString().orEmpty()
        selectedQuantityOption = if (tobacco.consumptionUnit.isNullOrBlank()) 0
        else quantityOptions.indexOf(tobacco.consumptionUnit)
        startAge = tobacco.startAge?.toString().orEmpty()
        willingToQuit = when (tobacco.willingToQuit) {
            null -> ""
            true -> YesNoEnum.YES.display
            false -> YesNoEnum.NO.display
        }
    }

    private fun mapAlcohol(alcohol: AlcoholResponse?) {
        alcohol ?: return
        consumedWithin30Days = if (alcohol.consumedWithin30Days) YesNoEnum.YES.display else YesNoEnum.NO.display
        alcoholQ1 = alcohol.alcoholQ1?.toString().orEmpty()
        alcoholQ2 = alcohol.alcoholQ2?.toString().orEmpty()
        alcoholQ3 = alcohol.alcoholQ3?.toString().orEmpty()
    }

    private fun mapFruitsVegetables(fv: FruitsVegetablesResponse?) {
        fv ?: return
        consumptionInWeek = if (fv.consumptionInWeek) YesNoEnum.YES.display else YesNoEnum.NO.display
        fruitsDays = fv.fruitsDays?.toString().orEmpty()
        vegetablesDays = fv.vegetableDays?.toString().orEmpty()
        fruitServings = fv.fruitServings?.toString().orEmpty()
        vegetableServings = fv.vegetableServings?.toString().orEmpty()
    }

    private fun mapPhysicalActivity(pa: PhysicalActivityResponse?) {
        pa ?: return
        weeklyEngagement = if (pa.weeklyEngagement) YesNoEnum.YES.display else YesNoEnum.NO.display
        vigorousDays = pa.vigorousDays?.toString().orEmpty()
        moderateDays = pa.moderateDays?.toString().orEmpty()
        vigorousTime = pa.vigorousTime?.toString().orEmpty()
        moderateTime = pa.moderateTime?.toString().orEmpty()
    }

    private fun mapSalt(salt: SaltResponse?) {
        salt ?: return
        saltAmount = saltAmountDisplayFromCode(salt.saltAmount ?: -1).orEmpty()
        saltAddCooking = saltFrequencyDisplayFromCode(salt.saltAddCooking ?: -1).orEmpty()
        saltAddMeal = saltFrequencyDisplayFromCode(salt.saltAddMeal ?: -1).orEmpty()
        saltProcessedFood = saltFrequencyDisplayFromCode(salt.saltProcessedFood ?: -1).orEmpty()
    }

    private fun mapFatAndOil(fat: FatAndOilResponse?) {
        fat ?: return
        oilUsed = fatTypeDisplayFromCode(fat.oilUsed ?: -1).orEmpty()
        fatFoodFrequency = fatFrequencyDisplayFromCode(fat.fatFoodFrequency ?: -1).orEmpty()
        otherFatAndOils = fat.otherFatAndOils.orEmpty()
    }

    private fun mapSugar(sugar: SugarResponse?) {
        sugar ?: return
        juiceFrequency = fruitJuiceFrequencyDisplayFromCode(sugar.juiceFrequency ?: -1).orEmpty()
        softDrinkFrequency = softDrinkFrequencyDisplayFromCode(sugar.softDrinkFrequency ?: -1).orEmpty()
    }

    private fun mapMealsOutside(meals: MealsOutsideHomeResponse?) {
        meals ?: return
        eatsOut = when (meals.eatsOut) {
            null -> ""
            true -> KnowEnum.KNOW.display
            false -> KnowEnum.DO_NOT_KNOW.display
        }
        mealsPerWeek = meals.mealsPerWeek?.toString().orEmpty()
    }


    private suspend fun getAppointment() {
        appointmentResponseLocal =
            appointmentRepository.getAppointmentListByDate(
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).firstOrNull { appointmentEntity ->
                appointmentEntity.patientId == patient!!.id && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
                        && user.hospitalCode == appointmentEntity.hospitalCode
            }
    }

    private fun getRiskFactorResponse(
        uuid: String = UUIDBuilder.generateUUID(),
        fhirId: String? = null,
        appUpdatedDate: Date = Date()
    ): RiskFactorResponse {
        return RiskFactorResponse(
            uuid = uuid,
            fhirId = fhirId,
            appointmentId = appointmentResponseLocal!!.appointmentId
                ?: appointmentResponseLocal!!.uuid,
            patientId = patient!!.fhirId ?: patient!!.id,
            practitionerId = null,
            practitionerName = null,
            appUpdatedDate = appUpdatedDate,
            tobacco = getTobaccoResponse(),
            alcohol = getAlcoholResponse(),
            fruitsVegetables = getFruitAndVegetableResponse(),
            physicalActivity = getPhysicalActivityResponse(),
            salt = getSaltResponse(),
            fatAndOil = getFatAndOilResponse(),
            sugar = getSugarResponse(),
            mealsOutsideHome = getMealsOutsideHome(),
        )
    }

    private fun getTobaccoResponse(): TobaccoResponse? {
        return if (useTobacco.isBlank()) null
        else TobaccoResponse(
            tobaccoUser = booleanFromDisplay(useTobacco)!!,
            tobaccoItemType = tobaccoTypeCodeFromDisplay(tobaccoType),
            tobaccoOther = otherTobacco.ifBlank { null },
            consumptionAmount = tobaccoQuantity.ifBlank { null }?.toInt(),
            consumptionUnit = if (tobaccoQuantity.isBlank()) null else quantityOptions[selectedQuantityOption],
            startAge = startAge.ifBlank { null }?.toInt(),
            willingToQuit = booleanFromDisplay(willingToQuit)
        )
    }

    private fun getAlcoholResponse(): AlcoholResponse? {
        return if (consumedWithin30Days.isBlank()) null
        else AlcoholResponse(
            consumedWithin30Days = booleanFromDisplay(consumedWithin30Days)!!,
            alcoholQ1 = alcoholQ1.ifBlank { null }?.toInt(),
            alcoholQ2 = alcoholQ2.ifBlank { null }?.toInt(),
            alcoholQ3 = alcoholQ3.ifBlank { null }?.toInt()
        )
    }

    private fun getFruitAndVegetableResponse(): FruitsVegetablesResponse? {
        return if (consumptionInWeek.isBlank()) null
        else FruitsVegetablesResponse(
            consumptionInWeek = booleanFromDisplay(consumptionInWeek)!!,
            fruitServings = fruitServings.ifBlank { null }?.toInt(),
            fruitsDays = fruitsDays.ifBlank { null }?.toInt(),
            vegetableDays = vegetablesDays.ifBlank { null }?.toInt(),
            vegetableServings = vegetableServings.ifBlank { null }?.toInt()
        )
    }

    private fun getPhysicalActivityResponse(): PhysicalActivityResponse? {
        return if (weeklyEngagement.isBlank()) null
        else PhysicalActivityResponse(
            weeklyEngagement = booleanFromDisplay(weeklyEngagement)!!,
            moderateDays = moderateDays.ifBlank { null }?.toInt(),
            moderateTime = moderateTime.ifBlank { null }?.toInt(),
            vigorousDays = vigorousDays.ifBlank { null }?.toInt(),
            vigorousTime = vigorousTime.ifBlank { null }?.toInt()
        )
    }

    private fun getSaltResponse(): SaltResponse? {
        return if (saltAddCooking.isBlank()
            && saltAddMeal.isBlank()
            && saltAmount.isBlank()
            && saltProcessedFood.isBlank()
        ) null
        else SaltResponse(
            saltAmount = saltAmountCodeFromDisplay(saltAmount),
            saltAddCooking = saltFrequencyCodeFromDisplay(saltAddCooking),
            saltAddMeal = saltFrequencyCodeFromDisplay(saltAddMeal),
            saltProcessedFood = saltFrequencyCodeFromDisplay(saltProcessedFood)
        )
    }

    private fun getFatAndOilResponse(): FatAndOilResponse? {
        return if (oilUsed.isBlank() && fatFoodFrequency.isBlank()) null
        else FatAndOilResponse(
            oilUsed = fatTypeCodeFromDisplay(oilUsed),
            fatFoodFrequency = fatFrequencyCodeFromDisplay(fatFoodFrequency),
            otherFatAndOils = otherFatAndOils.ifBlank { null }
        )
    }

    private fun getSugarResponse(): SugarResponse? {
        return if (juiceFrequency.isBlank() && softDrinkFrequency.isBlank()) null
        else SugarResponse(
            juiceFrequency = fruitJuiceFrequencyCodeFromDisplay(juiceFrequency),
            softDrinkFrequency = softDrinkFrequencyCodeFromDisplay(softDrinkFrequency)
        )
    }

    private fun getMealsOutsideHome(): MealsOutsideHomeResponse? {
        return if (eatsOut.isBlank()) null
        else MealsOutsideHomeResponse(
            eatsOut = if (eatsOut.isBlank()) null else eatsOut == KnowEnum.KNOW.display,
            mealsPerWeek = mealsPerWeek.ifBlank { null }?.toInt()
        )
    }

    fun addRiskFactor(
        added: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            getAppointment()
            var uuid = UUIDBuilder.generateUUID()
            var fhirId: String? = null
            todayRiskFactor?.let {
                uuid = it.uuid
                fhirId = it.fhirId
            }
            val riskFactorResponse = getRiskFactorResponse(uuid)
            riskFactorRepository.insertRiskFactor(
                riskFactorResponse.copy(
                    appointmentId = appointmentResponseLocal!!.uuid,
                    patientId = patient!!.id,
                    practitionerName = getFullName(
                        user.firstName,
                        user.lastName
                    ),
                    practitionerId = user.fhirId,
                    fhirId = fhirId
                )
            )
            genericRepository.insertRiskFactorRecord(riskFactorResponse)
            checkAndUpdateAppointmentStatusToInProgress(
                inProgressTime = riskFactorResponse.appUpdatedDate,
                patient = patient!!,
                appointmentResponseLocal = appointmentResponseLocal!!,
                appointmentRepository = appointmentRepository,
                scheduleRepository = scheduleRepository,
                genericRepository = genericRepository,
                preferenceRepository = preferenceRepository
            )
            updatePatientLastUpdated(
                patient!!.id,
                patientLastUpdatedRepository,
                genericRepository
            )
            added()
        }
    }
}