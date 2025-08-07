package com.heartcare.agni.ui.historyandtests.risk.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.server.model.patient.PatientResponse

class AddRiskFactorViewModel: BaseViewModel() {
    var patient by mutableStateOf<PatientResponse?>(null)
    var isLaunched by mutableStateOf(false)

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
}