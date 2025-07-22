package com.heartcare.agni.data.local.roomdb.entities.schedule

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Keep
@Entity
data class ScheduleEntity(
    val id: String,
    val scheduleFhirId: String?,
    @PrimaryKey
    val startTime: Date,
    val endTime: Date,
    val bookedSlots: Int,
    val roleId: String,
    val active: Boolean,
    val practitionerId: String,
    val hospitalId: String?,
    val hospitalFhirId: String?,
    val hospitalName: String,
    val hospitalCode: String
)