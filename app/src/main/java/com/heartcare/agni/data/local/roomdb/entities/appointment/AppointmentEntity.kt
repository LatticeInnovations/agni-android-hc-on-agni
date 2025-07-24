package com.heartcare.agni.data.local.roomdb.entities.appointment

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientEntity
import com.heartcare.agni.data.local.roomdb.entities.schedule.ScheduleEntity
import java.util.Date

@Keep
@Entity(
    indices = [Index("patientId"), Index("scheduleId")],
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("patientId")
        ),
        ForeignKey(
            entity = ScheduleEntity::class,
            parentColumns = arrayOf("startTime", "hospitalCode"),
            childColumns = arrayOf("scheduleId", "hospitalCode")
        )
    ]
)
data class AppointmentEntity(
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
    val hospitalCode: String
)
