package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heartcare.agni.data.local.roomdb.entities.priordx.PriorDxEntity

@Dao
interface PriorDxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPriorDxRecord(vararg priorDxEntity: PriorDxEntity): List<Long>

    @Query("SELECT * FROM PriorDxEntity WHERE patientId=:patientId ORDER BY createdOn DESC")
    fun getPriorDxRecords(patientId: String): List<PriorDxEntity>
}