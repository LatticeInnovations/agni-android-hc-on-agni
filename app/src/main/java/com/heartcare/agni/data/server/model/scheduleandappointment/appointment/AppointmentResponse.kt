package com.heartcare.agni.data.server.model.scheduleandappointment.appointment

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.heartcare.agni.data.server.model.scheduleandappointment.Slot
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class AppointmentResponse(
    val appointmentId: String?,
    val uuid: String,
    @SerializedName("patientId")
    val patientFhirId: String,
    val scheduleId: String,
    val slot: Slot,
    val createdOn: Date,
    val status: String,
    val appointmentType: String,
    @SerializedName("generatedOn")
    val inProgressTime: Date?,
    val roleId: String?,
    val slotId: String?,
    val practitionerId: String?,
    val hospitalFhirId: String?,
    val hospitalId: String?,
    val hospitalName: String?,
    val hospitalCode: String?,
    val appUpdatedDate: Date?
) : Parcelable
