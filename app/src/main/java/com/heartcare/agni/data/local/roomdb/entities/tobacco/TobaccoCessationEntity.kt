package com.heartcare.agni.data.local.roomdb.entities.tobacco

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.heartcare.agni.data.local.roomdb.entities.appointment.AppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.campaign.CampaignAppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientEntity
import java.util.Date

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
data class TobaccoCessationEntity (
    val uuid: String,
    val fhirId: String?,
    val patientId: String,
    val appointmentId: String?,
    val campaignAppointmentId: String?,
    val campaignId: String?,
    val practitionerId: String,
    val practitionerName: String,
    val appUpdatedDate: Date,
    val tobaccoUse: String?,
    val briefAdvice: Boolean?,
    val assessedStatus: Boolean?,
    val assistQuit: String?,
    val dateOfPlan: Date?,
    val pharmacotherapy: String?,
    val planStatus: String?
)