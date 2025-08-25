package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.heartcare.agni.data.local.roomdb.entities.intervention.InterventionMasterEntity

@Dao
interface InterventionDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterventionMaster(vararg interventionMasterEntity: InterventionMasterEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM InterventionMasterEntity")
    suspend fun getInterventionsMasterList(): List<InterventionMasterEntity>
}