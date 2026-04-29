package com.heartcare.agni.data.server.model.risk

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
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
    val mealsOutsideHome: MealsOutsideHomeResponse?,
    val campaignId: String? = null,
    val screeningSiteName: String? = null
) : Parcelable