package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.heartcare.agni.data.local.roomdb.entities.nationalId.NationalIdEntity

@Dao
interface NationalIdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNationalIdRecord(vararg nationalIdEntity: NationalIdEntity): List<Long>

    @Transaction
    @Query("DELETE FROM NationalIdEntity")
    fun deleteAllNationalIdRecord()

    @Query("SELECT * FROM NationalIdEntity WHERE nationalId=:nationalId")
    fun getNationalIdData(nationalId: String): List<NationalIdEntity>
}