package com.heartcare.agni.data.local.roomdb.entities.diagnosis

import android.os.Parcelable
import androidx.annotation.Keep
import com.heartcare.agni.data.server.model.diagnosis.DiagnosisItem
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class DiagnosisLocal(
    val diagnosisUuid: String,
    val appointmentId: String,
    val diagnosisFhirId: String?,
    val createdOn: Date,
    val diagnosis: List<DiagnosisItem>,
    val symptoms: List<DiagnosisItem>,
    val practitionerName: String,
    val patientId: String,
    val progressNote: String?
) : Parcelable
