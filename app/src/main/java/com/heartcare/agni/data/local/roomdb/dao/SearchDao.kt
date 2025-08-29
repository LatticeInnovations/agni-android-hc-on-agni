package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.heartcare.agni.data.local.enums.SearchTypeEnum
import com.heartcare.agni.data.local.roomdb.entities.examination.ExaminationMasterEntity
import com.heartcare.agni.data.local.roomdb.entities.intervention.InterventionMasterEntity
import com.heartcare.agni.data.local.roomdb.entities.medication.MedicationEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.heartcare.agni.data.local.roomdb.entities.search.SearchHistoryEntity
import com.heartcare.agni.data.local.roomdb.entities.search.SearchEntity
import com.heartcare.agni.data.local.roomdb.entities.diagnosis.DiagnosisMasterEntity

@Dao
interface SearchDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(searchHistoryEntity: SearchHistoryEntity): Long

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM PatientEntity INNER JOIN PatientLastUpdatedEntity ON PatientEntity.id = PatientLastUpdatedEntity.patientId WHERE PatientEntity.isDeleted = 0 OR PatientEntity.isDeleted IS NULL  ORDER BY PatientLastUpdatedEntity.lastUpdated DESC")
    suspend fun getPatientList(): List<PatientAndIdentifierEntity>

    @Transaction
    @Query("SELECT searchQuery FROM SearchHistoryEntity WHERE searchType = :searchTypeEnum ORDER BY date DESC")
    suspend fun getRecentSearches(searchTypeEnum: SearchTypeEnum): List<String>

    @Transaction
    @Query("SELECT id FROM SearchHistoryEntity WHERE searchType = :searchTypeEnum ORDER BY date ASC LIMIT 1")
    suspend fun getOldestRecentSearchId(searchTypeEnum: SearchTypeEnum): Int

    @Transaction
    @Query("DELETE FROM SearchHistoryEntity WHERE id=:id")
    suspend fun deleteRecentSearch(id: Int): Int

    @Transaction
    @Query("SELECT * FROM MedicationEntity WHERE status=\"active\"")
    suspend fun getAllMedication(): List<MedicationEntity>

    @Transaction
    @Query("SELECT * FROM DiagnosisMasterEntity")
    suspend fun getDiagnosisMasterList(): List<DiagnosisMasterEntity>

    @Transaction
    @Query("SELECT * FROM InterventionMasterEntity where status=\"active\"")
    suspend fun getInterventionMasterList(): List<InterventionMasterEntity>

    @Transaction
    @Query("SELECT * FROM ExaminationMasterEntity where status=\"active\"")
    suspend fun getExaminationMasterList(): List<ExaminationMasterEntity>

    // Insert a new search entry or update the existing one
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSearch(diagSearchEntity: SearchEntity): Long

    // Get the most frequent searches (limit to 5)
    @Query("SELECT searchQuery FROM SearchEntity where searchType=:searchTypeEnum ORDER BY searchCount DESC, date DESC LIMIT 5")
    suspend fun getMostFrequentSearches(searchTypeEnum: SearchTypeEnum): List<String>

    // Check if a query already exists
    @Query("SELECT * FROM SearchEntity WHERE searchQuery = :searchQuery")
    suspend fun getSearchByQuery(searchQuery: String): SearchEntity?

    // Delete the oldest record (the one with the lowest `id`)
    @Query("DELETE FROM SearchEntity WHERE id = (SELECT MIN(id) FROM SearchEntity)")
    suspend fun deleteOldestEntry()

    // Get the count of records in the table
    @Query("SELECT COUNT(*) FROM SearchEntity")
    suspend fun getRowCount(): Int

    @Query("UPDATE SearchEntity Set searchCount=:searchCount where searchQuery=:searchQuery")
    suspend fun updateSearch(searchCount: Long, searchQuery: String): Int

}