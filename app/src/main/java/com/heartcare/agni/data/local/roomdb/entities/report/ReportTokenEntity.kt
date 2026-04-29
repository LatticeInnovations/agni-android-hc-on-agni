package com.heartcare.agni.data.local.roomdb.entities.report

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.heartcare.agni.data.local.roomdb.entities.appointment.AppointmentEntity

@Keep
@Entity(
    indices = [Index("appointmentId")],
    primaryKeys = ["appointmentId"],
    foreignKeys = [
        ForeignKey(
            entity = AppointmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["appointmentId"]
        )
    ]
)
data class ReportTokenEntity (
    val appointmentId: String,
    val token: String
)
