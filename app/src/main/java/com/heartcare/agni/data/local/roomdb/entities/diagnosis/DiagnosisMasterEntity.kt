package com.heartcare.agni.data.local.roomdb.entities.diagnosis

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class DiagnosisMasterEntity(
    @PrimaryKey val id: Int,
    val code: String,
    val display: String
)
