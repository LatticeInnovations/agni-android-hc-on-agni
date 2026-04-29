package com.heartcare.agni.data.local.roomdb.entities.allergy

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.heartcare.agni.data.local.roomdb.entities.appointment.AppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientEntity
import java.util.Date

@Keep
@Entity(
    indices = [Index("patientId"), Index("appointmentId"), Index("campaignId"), Index("campaignAppointmentId")],
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
            entity = com.heartcare.agni.data.local.roomdb.entities.campaign.CampaignAppointmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["campaignAppointmentId"]
        )
    ]
)
data class AllergyEntity (
    val uuid: String,
    val fhirId: String?,
    val allergy: String?,
    val appUpdatedDate: Date,
    val appointmentId: String?,
    val campaignId: String?,
    val campaignAppointmentId: String?,
    val patientId: String,
    val practitionerId: String,
    val practitionerName: String
)