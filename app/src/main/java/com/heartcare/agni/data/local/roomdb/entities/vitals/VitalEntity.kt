package com.heartcare.agni.data.local.roomdb.entities.vitals

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.heartcare.agni.data.local.roomdb.entities.campaign.CampaignAppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientEntity
import java.util.Date

@Keep
@Entity(
    indices = [
        Index("fhirId"),
        Index("patientId"),
        Index("appointmentId"),
        Index("campaignAppointmentId")
    ],
    primaryKeys = ["uuid"],
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("patientId")
        ),
        ForeignKey(
            entity = CampaignAppointmentEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("campaignAppointmentId")
        )
    ]
)
data class VitalEntity(
    val uuid: String,
    val fhirId: String?,
    val patientId: String,
    val appointmentId: String?,              // nullable — facility appointment
    val campaignAppointmentId: String?,      // nullable — campaign appointment FK
    val campaignId: String?,                 // nullable — screening site identifier
    val appUpdatedDate: Date,
    val practitionerName: String?,

    @Embedded(prefix = "bloodGlucose_")
    val bloodGlucose: BloodGlucoseMeasurement?,

    val footExamination: String?,
    val eyeExamination: String?,

    @Embedded(prefix = "abdominalCircumference_")
    val abdominalCircumference: Measurement?,

    @Embedded(prefix = "hipCircumference_")
    val hipCircumference: Measurement?,
    val hbA1cPercentage: Double?,

    @Embedded(prefix = "serumCreatinine_")
    val serumCreatinine: Measurement?,

    @Embedded(prefix = "serumPotassium_")
    val serumPotassium: Measurement?,

    val urineProtein: String?,
    val urineKetones: String?,
    val others: String?
)


data class Measurement(
    val value: Double?,
    val unit: String?
)

data class BloodGlucoseMeasurement(
    val value: Double?,
    val unit: String?,
    val type: String?
)