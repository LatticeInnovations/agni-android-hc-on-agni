package com.heartcare.agni.utils.search

import com.heartcare.agni.data.local.enums.RiskCategoryEnum.Companion.getRiskRange
import com.heartcare.agni.data.local.model.search.SearchParameters
import com.heartcare.agni.data.local.roomdb.entities.intervention.InterventionMasterEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.heartcare.agni.data.local.roomdb.entities.symptomsanddiagnosis.DiagnosisEntity
import com.heartcare.agni.data.server.model.intervention.InterventionMasterResponse
import com.heartcare.agni.data.server.model.prescription.medication.MedicationResponse
import com.heartcare.agni.utils.constants.IdentificationConstants.HOSPITAL_ID
import com.heartcare.agni.utils.constants.IdentificationConstants.NATIONAL_ID
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAge
import com.heartcare.agni.utils.converters.responseconverter.toInterventionMasterResponse
import me.xdrop.fuzzywuzzy.FuzzySearch

object Search {

    internal fun getFuzzySearchList(
        totalList: List<PatientAndIdentifierEntity>,
        searchParameters: SearchParameters,
        matchingRatio: Int
    ): List<PatientAndIdentifierEntity> {
        var finalList = totalList.toMutableList()
        searchParameters.run {
            if (!gender.isNullOrBlank()) {
                finalList = finalList.filter {
                    gender == it.patientEntity.gender
                }.toMutableList()
            }
            if (!provinceId.isNullOrBlank()) {
                finalList = finalList.filter {
                    it.patientEntity.permanentAddress.province == provinceId
                }.toMutableList()
            }
            if (!areaCouncilId.isNullOrBlank()) {
                finalList = finalList.filter {
                    it.patientEntity.permanentAddress.areaCouncil == areaCouncilId
                }.toMutableList()
            }
            if (minAge != null && maxAge != null) {
                finalList = finalList.filter {
                    (minAge <= it.patientEntity.birthDate.toAge()) && (it.patientEntity.birthDate.toAge() <= maxAge)
                }.toMutableList()
            }
            if (!riskCategory.isNullOrEmpty()) {
                val combinedRiskSet = riskCategory
                    .flatMap { getRiskRange(it) }
                    .toSet()

                finalList = finalList.filter {
                    val latestCvd = it.cvdList.maxByOrNull { cvd -> cvd.createdOn }
                    latestCvd?.risk in combinedRiskSet
                }.toMutableList()
            }
            if (!name.isNullOrBlank()) {
                finalList = finalList.filter {
                    val fullName =
                        "${it.patientEntity.firstName}${it.patientEntity.lastName}"
                    FuzzySearch.weightedRatio(
                        name.replace(" ", "").trim(),
                        fullName
                    ) > matchingRatio
                }.toMutableList()
            }
            if (!fhirId.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        fhirId,
                        it.patientEntity.fhirId ?: ""
                    ) > matchingRatio
                }.toMutableList()
            }
            if (!heartcareId.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        heartcareId,
                        it.patientEntity.heartcareId ?: ""
                    ) > matchingRatio
                }.toMutableList()
            }
            if (!hospitalId.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        hospitalId,
                        it.identifiers.firstOrNull { id -> id.identifierType == HOSPITAL_ID }?.identifierNumber
                            ?: ""
                    ) > matchingRatio
                }.toMutableList()
            }
            if (!nationalId.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        nationalId,
                        it.identifiers.firstOrNull { id -> id.identifierType == NATIONAL_ID }?.identifierNumber
                            ?: ""
                    ) > matchingRatio
                }.toMutableList()
            }
        }
        return finalList
    }

    internal fun getFuzzySearchListByQuery(
        totalList: List<PatientAndIdentifierEntity>,
        query: String,
        matchingRatio: Int
    ): List<PatientAndIdentifierEntity> {
        var finalList = totalList.toMutableList()
        finalList = finalList.filter {
            val fullName =
                "${it.patientEntity.firstName}${it.patientEntity.lastName}"
            FuzzySearch.weightedRatio(
                query.replace(" ", "").trim().lowercase(),
                fullName.lowercase()
            ) > matchingRatio
                    || FuzzySearch.weightedRatio(
                query.trim().lowercase(),
                it.patientEntity.heartcareId?.lowercase() ?: ""
            ) > matchingRatio
        }.toMutableList()
        return finalList
    }

    internal fun getFuzzySearchMedication(
        queryActiveIngredient: String,
        medicationList: List<MedicationResponse>,
        matchingRatio: Int
    ): List<MedicationResponse> {
        return medicationList.filter { medication ->
            FuzzySearch.partialRatio(queryActiveIngredient, medication.activeIngredient) > matchingRatio
        }
    }

    internal fun getFuzzySearchDiagnosisList(
        searchQuery: String,
        diagnosisList: List<DiagnosisEntity>,
        matchingRatio: Int
    ): List<String> {
        return diagnosisList.filter { diagnosis ->
            FuzzySearch.weightedRatio(searchQuery.lowercase(), diagnosis.display.lowercase()) > matchingRatio
                    || FuzzySearch.weightedRatio(searchQuery.lowercase(), diagnosis.code.lowercase()) > matchingRatio
        }.map { "${it.code}, ${it.display}" }
    }

    internal fun getFuzzySearchSymptomsList(
        searchQuery: String,
        symptomsList: List<String>,
        matchingRatio: Int
    ): List<String> {
        return symptomsList.filter { symptoms ->
            FuzzySearch.partialRatio(searchQuery, symptoms) > matchingRatio
        }
    }

    internal fun getFuzzySearchInterventionList(
        searchQuery: String,
        interventionList: List<InterventionMasterEntity>,
        matchingRatio: Int
    ): List<InterventionMasterResponse> {
        return interventionList.filter { intervention ->
            FuzzySearch.partialRatio(searchQuery.lowercase(), intervention.name.lowercase()) > matchingRatio
                    || FuzzySearch.partialRatio(searchQuery.lowercase(), intervention.code.lowercase()) > matchingRatio
        }.map { it.toInterventionMasterResponse() }
    }
}
