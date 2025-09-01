package com.heartcare.agni.data.local.roomdb.entities.healthfacility

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity()
data class HealthFacilityEntity(
    @PrimaryKey
    val healthFacilityId: String,
    val code: String,
    val heartcareId: String?,
    val islandId: String,
    val name: String
)
