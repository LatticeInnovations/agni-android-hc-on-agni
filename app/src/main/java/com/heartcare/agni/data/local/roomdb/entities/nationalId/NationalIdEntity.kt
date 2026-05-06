package com.heartcare.agni.data.local.roomdb.entities.nationalId

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Keep
@Entity
data class NationalIdEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nationalId: String,
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val dob: Date,
    val gender: String,
    val updatedAt: Long
)