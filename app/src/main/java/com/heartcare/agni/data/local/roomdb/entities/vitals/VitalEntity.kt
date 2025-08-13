package com.heartcare.agni.data.local.roomdb.entities.vitals

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientEntity
import java.util.Date

@Keep
@Entity(
    indices = [Index("fhirId"), Index("patientId"), Index("appointmentId")],
    primaryKeys = ["uuid"],
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("patientId")
        )
    ]
)
data class VitalEntity(
    val uuid: String,
    val fhirId: String?,
    val patientId: String,
    val appointmentId: String,
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
    val hbA1cPercentage: Int?,

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