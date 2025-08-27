package com.heartcare.agni.data.local.roomdb.entities.examination

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class ExaminationMasterEntity(
    @PrimaryKey
    val fhirId: String,
    val code: String,
    val name: String,
    val secondaryName: String?,
    val status: String
)
