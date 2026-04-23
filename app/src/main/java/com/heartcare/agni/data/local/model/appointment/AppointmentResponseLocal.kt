package com.heartcare.agni.data.local.model.appointment

import android.os.Parcelable
import androidx.annotation.Keep
import com.heartcare.agni.data.local.enums.RecordType
import com.heartcare.agni.data.server.model.scheduleandappointment.Slot
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class AppointmentResponseLocal(
    val appointmentId: String?,
    val uuid: String,
    val patientId: String,
    val scheduleId: Date,
    val slot: Slot,
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
    val campaignId: String? = null,
    val recordType: RecordType? = RecordType.FACILITY
) : Parcelable