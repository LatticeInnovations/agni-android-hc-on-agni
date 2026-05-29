package com.heartcare.agni.data.local.repository.preference

import com.heartcare.agni.data.server.model.authentication.LoginResponse

interface PreferenceRepository {

    /** User Details */
    fun setUserDetails(loginResponse: LoginResponse)
    fun getUserDetails(): LoginResponse?

    /** Access Token */
    fun setAccessToken(token: String)
    fun getAccessToken(): String

    /** Refresh Token */
    fun setRefreshToken(token: String)
    fun getRefreshToken(): String

    /** M-Pin */
    fun setPin(pin: String)
    fun getPin(): String

    /** Last Sync Status */
    fun setSyncStatus(status: String)
    fun getSyncStatus(): String

    /** Last Sync Time Overall */
    fun setLastSyncTime(long: Long)
    fun getLastSyncTime(): Long

    /** Last Sync Patient */
    fun setLastSyncPatient(long: Long)
    fun getLastSyncPatient(): Long

    /** Last Sync Prescription */
    fun setLastSyncPrescription(long: Long)
    fun getLastSyncPrescription(): Long

    /** Last Medication Sync Date */
    fun setLastMedicationSyncDate(long: Long)
    fun getLastMedicationSyncDate(): Long

    /** Last Medicine Dosage Instruction Sync Date */
    fun setLastMedicineDosageInstructionSyncDate(long: Long)
    fun getLastMedicineDosageInstructionSyncDate(): Long

    /** Last Sync Schedule */
    fun setLastSyncSchedule(long: Long)
    fun getLastSyncSchedule(): Long

    /** Last Sync Appointment */
    fun setLastSyncAppointment(long: Long)
    fun getLastSyncAppointment(): Long

    /** Last Sync CVD */
    fun setLastSyncCVD(long: Long)
    fun getLastSyncCVD(): Long

    /** Last Sync Vital */
    fun setLastSyncVital(long: Long)
    fun getLastSyncVital(): Long
    /** Last Sync Campaign Vital */
    fun setLastSyncCampaignVital(long: Long)
    fun getLastSyncCampaignVital(): Long

    /** Last Sync Campaign Schedule */
    fun setLastSyncCampaignSchedule(long: Long)
    fun getLastSyncCampaignSchedule(): Long

    /** Last Sync Campaign Appointment */
    fun setLastSyncCampaignAppointment(long: Long)
    fun getLastSyncCampaignAppointment(): Long

    /** Last Sync Campaign CVD */
    fun setLastSyncCampaignCVD(long: Long)
    fun getLastSyncCampaignCVD(): Long

    /** Last Sync Diagnosis */
    fun setLastSyncDiagnosis(long: Long)
    fun getLastSyncDiagnosis(): Long
    fun setLastSyncCampaignDiagnosis(long: Long)
    fun getLastSyncCampaignDiagnosis(): Long

    /** Last Level Data */
    fun setLastSyncLevelRecord(long: Long)
    fun getLastSyncLevelRecord(): Long

    /** Last Sync Prior Dx */
    fun setLastSyncPriorDx(long: Long)
    fun getLastSyncPriorDx(): Long

    /** Last Sync Campaign Prior Dx */
    fun setLastSyncCampaignPriorDx(long: Long)
    fun getLastSyncCampaignPriorDx(): Long

    /** Last Sync History Medication */
    fun setLastSyncHistoryMedication(long: Long)
    fun getLastSyncHistoryMedication(): Long

    /** Last Sync Campaign History Medication */
    fun setLastSyncCampaignHistoryMedication(long: Long)
    fun getLastSyncCampaignHistoryMedication(): Long

    /** Last Sync Family History */
    fun setLastSyncFamilyHistory(long: Long)
    fun getLastSyncFamilyHistory(): Long

    /** Last Sync Campaign Family History */
    fun setLastSyncCampaignFamilyHistory(long: Long)
    fun getLastSyncCampaignFamilyHistory(): Long

    /** Last Sync Allergy */
    fun setLastSyncAllergy(lastSync: Long)
    fun getLastSyncAllergy(): Long

    fun setLastSyncCampaignAllergy(lastSync: Long)
    fun getLastSyncCampaignAllergy(): Long

    /** Last Sync Risk Factors */
    fun setLastSyncRiskFactors(long: Long)
    fun getLastSyncRiskFactors(): Long

    fun setLastSyncCampaignRiskFactors(long: Long)
    fun getLastSyncCampaignRiskFactors(): Long

    /** Last Sync Tobacco Cessation */
    fun setLastSyncTobaccoCessation(long: Long)
    fun getLastSyncTobaccoCessation(): Long

    fun setLastSyncCampaignTobaccoCessation(time: Long)
    fun getLastSyncCampaignTobaccoCessation(): Long

    /** Last Intervention Master Sync Date */
    fun setLastInterventionMasterSyncDate(long: Long)
    fun getLastInterventionMasterSyncDate(): Long

    /** Last Intervention Sync Date */
    fun setLastSyncIntervention(long: Long)
    fun getLastSyncIntervention(): Long
    fun setLastSyncCampaignIntervention(long: Long)
    fun getLastSyncCampaignIntervention(): Long

    /** Last Examination Master Sync Date */
    fun setLastExaminationMasterSyncDate(long: Long)
    fun getLastExaminationMasterSyncDate(): Long

    /** Last Examination Sync Date */
    fun setLastSyncExamination(long: Long)
    fun getLastSyncExamination(): Long
    fun setLastSyncCampaignExamination(long: Long)
    fun getLastSyncCampaignExamination(): Long

    /** Last Diagnosis Master Sync Date */
    fun setLastDiagnosisMasterSyncDate(long: Long)
    fun getLastDiagnosisMasterSyncDate(): Long

    /** Last Health Facility Sync Date */
    fun setLastSyncHealthFacility(long: Long)
    fun getLastSyncHealthFacility(): Long

    /** Last Referral Sync Date */
    fun setLastSyncReferral(long: Long)
    fun getLastSyncReferral(): Long

    /** Last Screening Site Master Sync Date */
    fun setLastSyncScreeningSiteMaster(long: Long)
    fun getLastSyncScreeningSiteMaster(): Long

    /** Screening Site Seeding Status */
    fun setScreeningSiteSeeded(isSeeded: Boolean)
    fun isScreeningSiteSeeded(): Boolean

    /** Last ReportToken Sync Date */
    fun setLastSyncReportToken(long: Long)
    fun getLastSyncReportToken(): Long

    /** Last National Id Sync Date */
    fun setLastSyncNationalId(long: Long)
    fun getLastSyncNationalId(): Long

    /** Last National Id Server Sync Date */
    fun setServerSyncNationalId(long: Long)
    fun getServerSyncNationalId(): Long

    /** RoomDB EncryptionKey */
    fun setRoomDBEncryptionKey(encryptionKey: String)
    fun getRoomDBEncryptionKey(): String

    /** Clear preferences */
    fun clearPreferences()
}