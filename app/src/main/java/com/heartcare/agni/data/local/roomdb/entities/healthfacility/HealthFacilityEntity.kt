package com.heartcare.agni.data.local.roomdb.entities.healthfacility

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.heartcare.agni.data.local.roomdb.entities.levels.LevelEntity

@Keep
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = LevelEntity::class,
            parentColumns = ["fhirId"],
            childColumns = ["islandId"]
        )
    ]
)
data class HealthFacilityEntity(
    @PrimaryKey
    val healthFacilityId: String,
    val code: String,
    val heartcareId: String?,
    val islandId: String,
    val name: String
)
