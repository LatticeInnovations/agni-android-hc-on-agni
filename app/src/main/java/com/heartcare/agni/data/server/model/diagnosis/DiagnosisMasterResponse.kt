package com.heartcare.agni.data.server.model.diagnosis

import androidx.annotation.Keep

@Keep
data class DiagnosisMasterResponse(
    val diagnosisId: Int,
    val code: String,
    val display: String
)