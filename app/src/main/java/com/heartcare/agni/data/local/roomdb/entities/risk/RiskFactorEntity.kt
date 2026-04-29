package com.heartcare.agni.data.local.roomdb.entities.risk

import androidx.annotation.Keep
import androidx.room.*
import com.heartcare.agni.data.local.roomdb.entities.appointment.AppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.campaign.CampaignAppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientEntity
import java.util.*

@Keep
@Entity(
    indices = [
        Index("patientId"),
        Index("appointmentId"),
        Index("campaignAppointmentId"),
        Index("campaignId")
    ],
    primaryKeys = ["uuid"],
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"]
        ),
        ForeignKey(
            entity = AppointmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["appointmentId"]
        ),
        ForeignKey(
            entity = CampaignAppointmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["campaignAppointmentId"]
        )
    ]
)
data class RiskFactorEntity(
    val uuid: String,
    val fhirId: String?,
    val appointmentId: String?,
    val campaignAppointmentId: String?,
    val campaignId: String?,
    val patientId: String,
    val appUpdatedDate: Date,
    val practitionerId: String?,
    val practitionerName: String?,

    @Embedded
    val tobacco: TobaccoEntity?,

    @Embedded
    val alcohol: AlcoholEntity?,

    @Embedded
    val fruitsVegetables: FruitsVegetablesEntity?,

    @Embedded
    val physicalActivity: PhysicalActivityEntity?,

    @Embedded
    val salt: SaltEntity?,

    @Embedded
    val fatAndOil: FatAndOilEntity?,

    @Embedded
    val sugar: SugarEntity?,

    @Embedded
    val mealsOutsideHome: MealsOutsideHomeEntity?
)

@Keep
data class TobaccoEntity(
    val tobaccoUser: Boolean?,
    val tobaccoItemType: Int?,
    val tobaccoOther: String?,
    val consumptionAmount: Int?,
    val consumptionUnit: String?,
    val startAge: Int?,
    val willingToQuit: Boolean?
)

@Keep
data class AlcoholEntity(
    val consumedWithin30Days: Boolean?,
    val alcoholQ1: Int?,
    val alcoholQ2: Int?,
    val alcoholQ3: Int?
)

@Keep
data class FruitsVegetablesEntity(
    val consumptionInWeek: Boolean?,
    val fruitsDays: Int?,
    val fruitServings: Int?,
    val vegetableDays: Int?,
    val vegetableServings: Int?
)

@Keep
data class PhysicalActivityEntity(
    val weeklyEngagement: Boolean?,
    val vigorousDays: Int?,
    val vigorousTime: Int?,
    val moderateDays: Int?,
    val moderateTime: Int?
)

@Keep
data class SaltEntity(
    val saltAmount: Int?,
    val saltAddMeal: Int?,
    val saltAddCooking: Int?,
    val saltProcessedFood: Int?
)

@Keep
data class FatAndOilEntity(
    val oilUsed: Int?,
    val fatFoodFrequency: Int?,
    val otherFatAndOils: String?
)

@Keep
data class SugarEntity(
    val softDrinkFrequency: Int?,
    val juiceFrequency: Int?
)

@Keep
data class MealsOutsideHomeEntity(
    val eatsOut: Boolean?,
    val mealsPerWeek: Int?
)