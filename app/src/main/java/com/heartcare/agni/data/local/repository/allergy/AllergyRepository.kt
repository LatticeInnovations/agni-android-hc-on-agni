package com.heartcare.agni.data.local.repository.allergy

import com.heartcare.agni.data.server.model.allergy.AllergyResponse

interface AllergyRepository {
    suspend fun insertAllergy(vararg allergyResponse: AllergyResponse): List<Long>
    suspend fun getAllergyRecordsByAppointmentIds(vararg appointmentIds: String): List<AllergyResponse>
    suspend fun getLatestAllergyForCampaign(patientId: String, campaignId: String): AllergyResponse?
}