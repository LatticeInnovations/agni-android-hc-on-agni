package com.heartcare.agni.data.local.repository.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.heartcare.agni.data.local.enums.SearchTypeEnum
import com.heartcare.agni.data.local.model.pagination.PaginationResponse
import com.heartcare.agni.data.local.model.search.SearchParameters
import com.heartcare.agni.data.local.roomdb.dao.SearchDao
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.heartcare.agni.data.local.roomdb.entities.search.SearchHistoryEntity
import com.heartcare.agni.data.local.roomdb.entities.search.SearchEntity
import com.heartcare.agni.data.server.model.examination.ExaminationMasterResponse
import com.heartcare.agni.data.server.model.intervention.InterventionMasterResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.prescription.medication.MedicationResponse
import com.heartcare.agni.utils.constants.Paging.PAGE_SIZE
import com.heartcare.agni.utils.converters.responseconverter.toMedicationResponse
import com.heartcare.agni.utils.converters.responseconverter.toPatientResponse
import com.heartcare.agni.utils.paging.SearchPagingSource
import com.heartcare.agni.utils.search.Search.getFuzzySearchDiagnosisList
import com.heartcare.agni.utils.search.Search.getFuzzySearchExaminationList
import com.heartcare.agni.utils.search.Search.getFuzzySearchInterventionList
import com.heartcare.agni.utils.search.Search.getFuzzySearchList
import com.heartcare.agni.utils.search.Search.getFuzzySearchListByQuery
import com.heartcare.agni.utils.search.Search.getFuzzySearchMedication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchDao: SearchDao
) : SearchRepository {

    override fun getSearchList(): Flow<List<PatientAndIdentifierEntity>> {
        return searchDao.getPatientList()
    }

    override fun searchPatients(
        searchParameters: SearchParameters,
        searchList: List<PatientAndIdentifierEntity>
    ): Flow<PagingData<PaginationResponse<PatientResponse>>> {
        val fuzzySearchList = getFuzzySearchList(
            searchList,
            searchParameters,
            68
        )
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPagingSource(
                    fuzzySearchList,
                    PAGE_SIZE
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { patientAndIdentifierEntity ->
                PaginationResponse(
                    patientAndIdentifierEntity.toPatientResponse(),
                    fuzzySearchList.size
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun searchPatientsByQuery(
        query: String,
        searchListFlow: Flow<List<PatientAndIdentifierEntity>>
    ): Flow<PagingData<PaginationResponse<PatientResponse>>> {
        return searchListFlow.flatMapLatest { searchList ->
            val fuzzySearchList = getFuzzySearchListByQuery(searchList, query, 68)

            Pager(
                config = PagingConfig(
                    pageSize = PAGE_SIZE,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    SearchPagingSource(
                        fuzzySearchList,
                        PAGE_SIZE
                    )
                }
            ).flow.map { pagingData ->
                pagingData.map { patientAndIdentifierEntity ->
                    PaginationResponse(
                        patientAndIdentifierEntity.toPatientResponse(),
                        fuzzySearchList.size
                    )
                }
            }
        }
    }


    override fun filteredSearchPatients(
        patientId: String,
        searchParameters: SearchParameters,
        searchList: List<PatientAndIdentifierEntity>,
        existingMembers: Set<String>
    ): Flow<PagingData<PaginationResponse<PatientResponse>>> {
        val fuzzySearchList = getFuzzySearchList(
            searchList,
            searchParameters,
            68
        ).filter {
            !existingMembers.contains(it.patientEntity.id)
        }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchPagingSource(
                    fuzzySearchList,
                    PAGE_SIZE
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { patientAndIdentifierEntity ->
                PaginationResponse(
                    patientAndIdentifierEntity.toPatientResponse(),
                    fuzzySearchList.size
                )
            }
        }
    }

    override suspend fun searchMedication(query: String): List<MedicationResponse> {
        return getFuzzySearchMedication(query, searchDao.getAllMedication().map { it.toMedicationResponse() }, 70)
    }

    override suspend fun insertRecentPatientSearch(searchQuery: String, date: Date): Long {
        return searchDao.getRecentSearches(SearchTypeEnum.PATIENT).run {
            if (size == 5) {
                searchDao.getOldestRecentSearchId(SearchTypeEnum.PATIENT).run {
                    searchDao.deleteRecentSearch(this)
                    searchDao.insertRecentSearch(
                        SearchHistoryEntity(
                            searchQuery = searchQuery,
                            date = date,
                            searchType = SearchTypeEnum.PATIENT
                        )
                    )
                }
            } else {
                searchDao.insertRecentSearch(
                    SearchHistoryEntity(
                        searchQuery = searchQuery,
                        date = date,
                        searchType = SearchTypeEnum.PATIENT
                    )
                )
            }
        }
    }

    override suspend fun getRecentPatientSearches(): List<String> {
        return searchDao.getRecentSearches(SearchTypeEnum.PATIENT)
    }

    override suspend fun insertRecentActiveIngredientSearch(searchQuery: String, date: Date): Long {
        return searchDao.getRecentSearches(SearchTypeEnum.ACTIVE_INGREDIENT).run {
            if (size == 5) {
                searchDao.getOldestRecentSearchId(SearchTypeEnum.ACTIVE_INGREDIENT).run {
                    searchDao.deleteRecentSearch(this)
                    searchDao.insertRecentSearch(
                        SearchHistoryEntity(
                            searchQuery = searchQuery,
                            date = date,
                            searchType = SearchTypeEnum.ACTIVE_INGREDIENT
                        )
                    )
                }
            } else {
                searchDao.insertRecentSearch(
                    SearchHistoryEntity(
                        searchQuery = searchQuery,
                        date = date,
                        searchType = SearchTypeEnum.ACTIVE_INGREDIENT
                    )
                )
            }
        }
    }

    override suspend fun getRecentActiveIngredientSearches(): List<String> {
        return searchDao.getRecentSearches(SearchTypeEnum.ACTIVE_INGREDIENT)
    }

    override suspend fun insertRecentInterventionSearch(searchQuery: String, date: Date): Long {
        return searchDao.getRecentSearches(SearchTypeEnum.INTERVENTIONS).run {
            if (size == 5) {
                searchDao.getOldestRecentSearchId(SearchTypeEnum.INTERVENTIONS).run {
                    searchDao.deleteRecentSearch(this)
                    searchDao.insertRecentSearch(
                        SearchHistoryEntity(
                            searchQuery = searchQuery,
                            date = date,
                            searchType = SearchTypeEnum.INTERVENTIONS
                        )
                    )
                }
            } else {
                searchDao.insertRecentSearch(
                    SearchHistoryEntity(
                        searchQuery = searchQuery,
                        date = date,
                        searchType = SearchTypeEnum.INTERVENTIONS
                    )
                )
            }
        }
    }

    override suspend fun getRecentInterventionSearches(): List<String> {
        return searchDao.getRecentSearches(SearchTypeEnum.INTERVENTIONS)
    }

    override suspend fun insertRecentTestExaminationSearch(
        searchQuery: String,
        date: Date
    ): Long {
        return searchDao.getRecentSearches(SearchTypeEnum.TEST_EXAMINATION).run {
            if (size == 5) {
                searchDao.getOldestRecentSearchId(SearchTypeEnum.TEST_EXAMINATION).run {
                    searchDao.deleteRecentSearch(this)
                    searchDao.insertRecentSearch(
                        SearchHistoryEntity(
                            searchQuery = searchQuery,
                            date = date,
                            searchType = SearchTypeEnum.TEST_EXAMINATION
                        )
                    )
                }
            } else {
                searchDao.insertRecentSearch(
                    SearchHistoryEntity(
                        searchQuery = searchQuery,
                        date = date,
                        searchType = SearchTypeEnum.TEST_EXAMINATION
                    )
                )
            }
        }
    }

    override suspend fun getRecentTestExaminationSearches(): List<String> {
        return searchDao.getRecentSearches(SearchTypeEnum.TEST_EXAMINATION)
    }

    override suspend fun insertRecentDiagnosisSearch(
        searchQuery: String, searchTypeEnum: SearchTypeEnum, size: Int, date: Date
    ): Long {
        val diagnosisSearchEntity = searchDao.getSearchByQuery(searchQuery)
        return insertSearch(
            diagnosisSearchEntity = diagnosisSearchEntity,
            searchTypeEnum = searchTypeEnum,
            date = date,
            searchQuery = searchQuery
        )

    }

    private suspend fun insertSearch(
        diagnosisSearchEntity: SearchEntity?,
        searchTypeEnum: SearchTypeEnum,
        date: Date,
        searchQuery: String
    ): Long {
        return if (diagnosisSearchEntity == null) {
            searchDao.insertOrUpdateSearch(
                SearchEntity(
                    searchQuery = searchQuery,
                    date = date,
                    searchCount = 1,
                    searchType = searchTypeEnum
                )
            )
        } else {
            searchDao.updateSearch(
                searchQuery = diagnosisSearchEntity.searchQuery,
                searchCount = diagnosisSearchEntity.searchCount + 1
            ).toLong()
        }

    }

    override suspend fun getRecentDiagnosisSearches(searchTypeEnum: SearchTypeEnum): List<String> {
        return searchDao.getMostFrequentSearches(searchTypeEnum)
    }

    override suspend fun searchDiagnosis(searchQuery: String): List<String> {
        return getFuzzySearchDiagnosisList(searchQuery, searchDao.getDiagnosisMasterList(), 70)
    }

    override suspend fun searchIntervention(searchQuery: String): List<InterventionMasterResponse> {
        return getFuzzySearchInterventionList(searchQuery, searchDao.getInterventionMasterList(), 70)
    }

    override suspend fun searchExamination(searchQuery: String): List<ExaminationMasterResponse> {
        return getFuzzySearchExaminationList(searchQuery, searchDao.getExaminationMasterList(), 70)
    }
}