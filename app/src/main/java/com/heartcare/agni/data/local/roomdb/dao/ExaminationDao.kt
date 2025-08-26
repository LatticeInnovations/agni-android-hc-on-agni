package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.heartcare.agni.data.local.roomdb.entities.examination.ExaminationMasterEntity

@Dao
interface ExaminationDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExaminationMaster(vararg examinationMasterEntity: ExaminationMasterEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM ExaminationMasterEntity WHERE status=\"active\"")
    suspend fun getExaminationMaster(): List<ExaminationMasterEntity>
}