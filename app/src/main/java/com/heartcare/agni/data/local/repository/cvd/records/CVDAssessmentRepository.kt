package com.heartcare.agni.data.local.repository.cvd.records

import com.heartcare.agni.data.server.model.cvd.CVDResponse

interface CVDAssessmentRepository {
    suspend fun insertCVDRecord(vararg cvdResponse: CVDResponse): List<Long>
    suspend fun getCVDRecord(patientId: String): List<CVDResponse>
    suspend fun getCVDRecordByAppointmentIds(vararg appointmentIds: String): List<CVDResponse>
    suspend fun getTodayCVDRecord(patientId: String, startTime: Long, endTime: Long, campaignId: String?): CVDResponse?
    suspend fun getCVDRecordByScreeningDate(patientId: String, startTime: Long, endTime: Long, campaignId: String?): CVDResponse?
    suspend fun getLatestCVDForCampaign(patientId: String, campaignId: String): CVDResponse?
    suspend fun updateCVDRecord(cvdResponse: CVDResponse): Int
}