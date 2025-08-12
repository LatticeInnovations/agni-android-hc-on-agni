package com.heartcare.agni.data.server.model.tobacco

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class TobaccoCessationResponse(
    val uuid: String,
    val fhirId: String?,
    val patientId: String,
    val appointmentId: String,
    val practitionerId: String?,
    val practitionerName: String?,
    val appUpdatedDate: Date,
    val tobaccoUse: String?,
    val briefAdvice: Boolean?,
    val assessedStatus: Boolean?,
    val assistQuit: String?,
    val dateOfPlan: Date?,
    val pharmacotherapy: String?,
    val planStatus: String?
): Parcelable