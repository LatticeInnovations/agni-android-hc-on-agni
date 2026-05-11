package com.heartcare.agni.data.local.roomdb.entities.campaign

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import com.heartcare.agni.data.local.roomdb.entities.report.ReportTokenEntity

@Keep
data class CampaignAppointmentEntityWithToken(
    @Embedded val campaignAppointmentEntity: CampaignAppointmentEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "campaignAppointmentId"
    )
    val reportTokenEntity: ReportTokenEntity?
)