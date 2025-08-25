package com.heartcare.agni.data.server.model.intervention

import androidx.annotation.Keep

@Keep
data class InterventionMasterResponse(
    val code: String,
    val fhirId: String,
    val name: String,
    val secondaryName: String?,
    val status: String
)