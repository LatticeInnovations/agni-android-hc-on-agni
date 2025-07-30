package com.heartcare.agni.data.server.model.priordx

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.Date

@Keep
data class PriorDxResponse(
    @SerializedName("uuid")
    val priorDxUuid: String,
    val priorDxFhirId: String?,
    val appointmentId: String,
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
    val appUpdatedDate: Date?
)