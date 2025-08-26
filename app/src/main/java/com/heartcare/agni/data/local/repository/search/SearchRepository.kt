package com.heartcare.agni.data.local.repository.search

import androidx.paging.PagingData
import com.heartcare.agni.data.local.enums.SearchTypeEnum
import com.heartcare.agni.data.local.model.pagination.PaginationResponse
import com.heartcare.agni.data.local.model.search.SearchParameters
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.heartcare.agni.data.server.model.intervention.InterventionMasterResponse
import com.heartcare.agni.data.server.model.patient.PatientAddressResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.prescription.medication.MedicationResponse
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.LinkedList

interface SearchRepository {

    /** Patient Search */
    fun searchPatients(
        searchParameters: SearchParameters,
        searchList: List<PatientAndIdentifierEntity>
    ): Flow<PagingData<PaginationResponse<PatientResponse>>>

    fun filteredSearchPatients(
        patientId: String,
        searchParameters: SearchParameters, searchList: List<PatientAndIdentifierEntity>,
        existingMembers: Set<String>
    ): Flow<PagingData<PaginationResponse<PatientResponse>>>

    fun searchPatientsByQuery(
        query: String,
        searchList: List<PatientAndIdentifierEntity>
    ): Flow<PagingData<PaginationResponse<PatientResponse>>>

    /** Medication Search */
    suspend fun searchMedication(query: String): List<MedicationResponse>

    /** Recent Patient Search*/
    suspend fun insertRecentPatientSearch(searchQuery: String, date: Date = Date()): Long
    suspend fun getRecentPatientSearches(): List<String>

    /** Recent Medication Search*/
    suspend fun insertRecentActiveIngredientSearch(searchQuery: String, date: Date = Date()): Long
    suspend fun getRecentActiveIngredientSearches(): List<String>

    /** Recent Intervention Search*/
    suspend fun insertRecentInterventionSearch(searchQuery: String, date: Date = Date()): Long
    suspend fun getRecentInterventionSearches(): List<String>

    /** Recent Test and Examination Search*/
    suspend fun insertRecentTestExaminationSearch(searchQuery: String, date: Date = Date()): Long
    suspend fun getRecentTestExaminationSearches(): List<String>

    /** Get Suggested Members */
    suspend fun getSuggestedMembers(
        patientId: String,
        searchParameters: SearchParameters,
        returnList: (LinkedList<PatientResponse>) -> Unit
    )

    suspend fun getFiveSuggestedMembers(
        patientId: String,
        address: PatientAddressResponse
    ): List<PatientResponse>

    suspend fun getSearchList(): List<PatientAndIdentifierEntity>

    /** Recent Symptoms Search*/
    suspend fun insertRecentSymptomAndDiagnosisSearch(searchQuery: String, searchTypeEnum: SearchTypeEnum, size:Int, date: Date = Date()): Long
    suspend fun getRecentSymptomAndDiagnosisSearches(searchTypeEnum: SearchTypeEnum): List<String>

    suspend fun searchSymptoms(searchQuery: String, gender:String?): List<String>
    suspend fun searchDiagnosis(searchQuery: String): List<String>

    suspend fun searchIntervention(searchQuery: String): List<InterventionMasterResponse>
}