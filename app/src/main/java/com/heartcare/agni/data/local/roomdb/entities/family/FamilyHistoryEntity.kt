package com.heartcare.agni.data.local.roomdb.entities.family

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.heartcare.agni.data.local.roomdb.entities.appointment.AppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientEntity
import java.util.Date

@Keep
@Entity(
    indices = [Index("patientId"), Index("appointmentId")],
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
        )
    ]
)
data class FamilyHistoryEntity(
    val uuid: String,
    val fhirId: String?,
    val patientId: String,
    val appointmentId: String,
    val appUpdatedDate: Date,
    val familyDiseases: List<String>,
    val occurrenceAgeData: String?,
    val practitionerId: String,
    val practitionerName: String
)