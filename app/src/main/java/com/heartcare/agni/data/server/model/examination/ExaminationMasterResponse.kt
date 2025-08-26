package com.heartcare.agni.data.server.model.examination

import androidx.annotation.Keep

@Keep
data class ExaminationMasterResponse(
    val fhirId: String,
    val code: String,
    val name: String,
    val secondaryName: String?,
    val status: String
)