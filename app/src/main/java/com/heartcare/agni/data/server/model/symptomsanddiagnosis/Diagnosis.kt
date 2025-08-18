package com.heartcare.agni.data.server.model.symptomsanddiagnosis

import androidx.annotation.Keep

@Keep
data class Diagnosis(
    val diagnosisId: Int,
    val code: String,
    val display: String
)