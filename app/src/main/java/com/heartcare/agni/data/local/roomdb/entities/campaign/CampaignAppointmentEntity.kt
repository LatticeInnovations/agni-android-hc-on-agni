package com.heartcare.agni.data.local.roomdb.entities.campaign

import androidx.annotation.Keep
import androidx.room.*
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientEntity
import java.util.Date

@Keep
@Entity(
    indices = [
        Index("patientId"),
        Index("scheduleId", "campaignId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("patientId")
        ),
        ForeignKey(
            entity = CampaignScheduleEntity::class,
            parentColumns = arrayOf("startTime", "campaignId"),
            childColumns = arrayOf("scheduleId", "campaignId")
        )
    ]
)
data class CampaignAppointmentEntity(
    @PrimaryKey val id: String,
    val appointmentFhirId: String?,
    val patientId: String,
    val scheduleId: Date,
    val startTime: Date,
    val endTime: Date,
    val createdOn: Date,
    val status: String,
    val appointmentType: String,
    val inProgressTime: Date?,
    val roleId: String?,
    val slotId: String?,
    val practitionerId: String?,
    val hospitalFhirId: String?,
    val hospitalId: String?,
    val hospitalName: String?,
    val hospitalCode: String?,
    val campaignId: String
)
