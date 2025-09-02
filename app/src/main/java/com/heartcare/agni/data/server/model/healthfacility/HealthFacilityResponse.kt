package com.heartcare.agni.data.server.model.healthfacility

import androidx.annotation.Keep

@Keep
data class HealthFacilityResponse(
    val code: String,
    val healthFacilityId: String,
    val heartcareId: String,
    val islandId: String,
    val name: String
)