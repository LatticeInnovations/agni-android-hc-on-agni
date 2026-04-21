package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.heartcare.agni.data.local.roomdb.entities.campaign.ScreeningSiteMasterEntity

@Dao
interface ScreeningSiteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreeningSiteMaster(vararg screeningSiteMasterEntity: ScreeningSiteMasterEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM ScreeningSiteMasterEntity")
    suspend fun getScreeningSiteMaster(): List<ScreeningSiteMasterEntity>

}
