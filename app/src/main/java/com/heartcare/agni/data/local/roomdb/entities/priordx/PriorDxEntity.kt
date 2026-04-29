package com.heartcare.agni.data.local.roomdb.entities.priordx

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
    indices = [Index("patientId"), Index("appointmentId"),Index("campaignAppointmentId")],
    primaryKeys = ["priorDxUuid"],
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
        ),
        ForeignKey(
            entity = CampaignAppointmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["campaignAppointmentId"]
        )
    ]
)
data class PriorDxEntity (
    val priorDxUuid: String,
    val priorDxFhirId: String?,
    val appointmentId: String?,
    val campaignAppointmentId: String?,
    val cancer: String?,
    val createdOn: Date,
    val hasAids: Boolean,
    val hasAsthma: Boolean,
    val hasCancer: Boolean,
    val hasChronicKidneyDiseases: Boolean,
    val hasChronicObstructivePulmonaryDisease: Boolean,
    val hasCovid: Boolean,
    val hasDiabetes: Boolean,
    val hasHeartDiseases: Boolean,
    val hasHypercholesterolaemia: Boolean,
    val hasHypertension: Boolean,
    val hasOthers: Boolean,
    val hasTransientIschaemicAttack: Boolean,
    val hasTuberculosis: Boolean,
    val others: String?,
    val patientId: String,
    val practitionerId: String?,
    val practitionerName: String?,
    val campaignId: String?
)