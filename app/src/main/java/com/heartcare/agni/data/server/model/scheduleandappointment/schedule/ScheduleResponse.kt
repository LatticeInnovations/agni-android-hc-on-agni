package com.heartcare.agni.data.server.model.scheduleandappointment.schedule

import android.os.Parcelable
import androidx.annotation.Keep
import com.heartcare.agni.data.server.model.scheduleandappointment.Slot
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ScheduleResponse(
    val uuid: String,
    val scheduleId: String?,
    val planningHorizon: Slot,
    val bookedSlots: Int?,
    val roleId: String?,
    val active: Boolean?,
    val practitionerId: String?,
    val hospitalId: String?,
    val hospitalFhirId: String?,
    val hospitalName: String?,
    val hospitalCode: String?,
    val campaignId: String? = null
) : Parcelable
