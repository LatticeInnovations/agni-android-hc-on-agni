package com.heartcare.agni.data.server.model.risk

import androidx.annotation.Keep
import java.util.Date

@Keep
data class RiskFactorResponse(
    val uuid: String,
    val fhirId: String?,
    val patientId: String,
    val appointmentId: String,
    val appUpdatedDate: Date,
    val practitionerId: String?,
    val practitionerName: String?,
    val tobacco: TobaccoResponse?,
    val alcohol: AlcoholResponse?,
    val fruitsVegetables: FruitsVegetablesResponse?,
    val physicalActivity: PhysicalActivityResponse?,
    val salt: SaltResponse?,
    val fatAndOil: FatAndOilResponse?,
    val sugar: SugarResponse?,
    val mealsOutsideHome: MealsOutsideHomeResponse?
)