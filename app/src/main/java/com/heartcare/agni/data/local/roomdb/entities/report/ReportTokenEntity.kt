package com.heartcare.agni.data.local.roomdb.entities.report

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.heartcare.agni.data.local.roomdb.entities.appointment.AppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.campaign.CampaignAppointmentEntity

@Keep
@Entity(
    indices = [
        Index("appointmentId"),
        Index("campaignAppointmentId")
    ],
    foreignKeys = [
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
data class ReportTokenEntity(
    @PrimaryKey
    val token: String,
    val appointmentId: String?,
    val campaignAppointmentId: String?
)