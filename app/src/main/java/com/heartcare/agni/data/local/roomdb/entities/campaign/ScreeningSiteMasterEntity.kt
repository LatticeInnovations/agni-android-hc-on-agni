package com.heartcare.agni.data.local.roomdb.entities.campaign

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class ScreeningSiteMasterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val location: String,
    val areaCouncil: String,
    val serviceMode: String,
    val fromDate: String,
    val toDate: String,
    val teamLead: String,
    val status: String
)
