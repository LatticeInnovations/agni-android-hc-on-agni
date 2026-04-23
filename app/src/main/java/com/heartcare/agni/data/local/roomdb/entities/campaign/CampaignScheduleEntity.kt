package com.heartcare.agni.data.local.roomdb.entities.campaign

import androidx.annotation.Keep
import androidx.room.Entity
import java.util.Date

@Keep
@Entity(
    primaryKeys = ["startTime", "campaignId"]
)
data class CampaignScheduleEntity(
    val id: String,
    val scheduleFhirId: String?,
    val startTime: Date,
    val endTime: Date,
    val bookedSlots: Int,
    val roleId: String?,
    val active: Boolean,
    val practitionerId: String?,
    val hospitalId: String?,
    val hospitalFhirId: String?,
    val hospitalName: String?,
    val hospitalCode: String?,
    val campaignId: String
)
