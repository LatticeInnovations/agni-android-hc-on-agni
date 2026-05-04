package com.heartcare.agni.data.local.roomdb.entities.appointment

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import com.heartcare.agni.data.local.roomdb.entities.report.ReportTokenEntity

@Keep
data class AppointmentEntityWithToken(
    @Embedded val appointmentEntity: AppointmentEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "appointmentId"
    )
    val reportTokenEntity: ReportTokenEntity?
)