package com.heartcare.agni.data.local.roomdb.entities.historymedication

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.heartcare.agni.data.local.roomdb.entities.appointment.AppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.campaign.CampaignAppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientEntity
import java.util.Date

@Keep
@Entity(
    indices = [Index("patientId"), Index("appointmentId"), Index("campaignAppointmentId")],
    primaryKeys = ["uuid"],
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"]
        ),
        ForeignKey(
            entity = AppointmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["appointmentId"]
        ),ForeignKey(
            entity = CampaignAppointmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["campaignAppointmentId"]
        )
    ]
)
data class HistoryMedicationEntity(
    val uuid: String,
    val fhirId: String?,
    val adherence: String?,
    val appUpdatedDate: Date,
    val appointmentId: String?,
    val campaignAppointmentId: String?,
    val hasSideEffect: Boolean,
    val medicinePrescribed: List<String>,
    val medicinePrescribedOthers: String?,
    val patientId: String,
    val practitionerId: String?,
    val practitionerName: String?,
    val sideEffects: String?,
    val campaignId: String? ,
)
