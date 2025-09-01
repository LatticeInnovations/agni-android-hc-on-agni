package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.heartcare.agni.data.local.roomdb.entities.healthfacility.HealthFacilityEntity

@Dao
interface HealthFacilityDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthFacility(vararg healthFacilityEntity: HealthFacilityEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM HealthFacilityEntity")
    suspend fun getHealthFacility(): List<HealthFacilityEntity>
}