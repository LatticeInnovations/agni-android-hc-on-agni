package com.heartcare.agni.data.server.model.campaign

import androidx.annotation.Keep
@Keep
data class ScreeningSiteMasterResponse(
    val id: String,
    val name: String,
    val location: String,
    val areaCouncil: String,
    val serviceMode: String,
    val fromDate: String,
    val toDate: String,
    val teamLead: String,
    val status: String
)
