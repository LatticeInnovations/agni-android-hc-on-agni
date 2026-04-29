package com.heartcare.agni.data.local.roomdb.entities.diagnosis

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.heartcare.agni.data.local.roomdb.entities.campaign.CampaignAppointmentEntity
import com.heartcare.agni.data.server.model.diagnosis.DiagnosisItem
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientEntity
import java.util.Date

@Entity(
    indices = [Index("fhirId"), Index("appointmentId"), Index("patientId"), Index("campaignAppointmentId")],
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("patientId")
        ),
        ForeignKey(
            entity = CampaignAppointmentEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("campaignAppointmentId")
        )
    ]
)
@Keep
data class DiagnosisEntity(
    @PrimaryKey
    val diagnosisUuid: String,
    val appointmentId: String?,
    val campaignAppointmentId: String?,
    val campaignId: String?,
    val fhirId: String?,
    val createdOn: Date,
    val diagnosis: List<DiagnosisItem>,
    val symptoms: List<DiagnosisItem>,
    val practitionerName: String,
    val patientId: String,
    val progressNote: String?,
    val screeningSiteName: String? = null
)