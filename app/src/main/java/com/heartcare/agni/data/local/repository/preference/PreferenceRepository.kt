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

    /** Last Sync Symptoms And Diagnosis */
    fun setLastSyncSymDiag(long: Long)
    fun getLastSyncSymDiag(): Long

    /** Last Sync Manufacturer Record */
    fun setLastSyncManufacturerRecord(long: Long)
    fun getLastSyncManufacturerRecord(): Long

    /** Last Level Data */
    fun setLastSyncLevelRecord(long: Long)
    fun getLastSyncLevelRecord(): Long

    /** Last Sync Prior Dx */
    fun setLastSyncPriorDx(long: Long)
    fun getLastSyncPriorDx(): Long

    /** Last Sync History Medication */
    fun setLastSyncHistoryMedication(long: Long)
    fun getLastSyncHistoryMedication(): Long

    /** Last Sync Family History */
    fun setLastSyncFamilyHistory(long: Long)
    fun getLastSyncFamilyHistory(): Long

    /** Last Sync Allergy */
    fun setLastSyncAllergy(long: Long)
    fun getLastSyncAllergy(): Long

    /** Last Sync Risk Factors */
    fun setLastSyncRiskFactors(long: Long)
    fun getLastSyncRiskFactors(): Long

    /** Last Sync Tobacco Cessation */
    fun setLastSyncTobaccoCessation(long: Long)
    fun getLastSyncTobaccoCessation(): Long

    /** Last Intervention Master Sync Date */
    fun setLastInterventionMasterSyncDate(long: Long)
    fun getLastInterventionMasterSyncDate(): Long

    /** Last Intervention Sync Date */
    fun setLastSyncIntervention(long: Long)
    fun getLastSyncIntervention(): Long

    /** Last Examination Master Sync Date */
    fun setLastExaminationMasterSyncDate(long: Long)
    fun getLastExaminationMasterSyncDate(): Long

    /** Last Examination Sync Date */
    fun setLastSyncExamination(long: Long)
    fun getLastSyncExamination(): Long

    /** User Data */
    fun setUserFhirId(userFhirId: String)
    fun getUserFhirId(): String
    fun setUserName(userName: String)
    fun getUserName(): String
    fun setUserMobile(userMobile: Long)
    fun getUserMobile(): Long
    fun setUserEmail(userEmail: String)
    fun getUserEmail(): String
    fun setUserRoleId(userRoleId: String)
    fun getUserRoleId(): String
    fun setUserRole(userRole: String)
    fun getUserRole(): String
    fun setOrganizationFhirId(organizationFhirId: String)
    fun getOrganizationFhirId(): String
    fun setOrganization(organization: String)
    fun getOrganization(): String

    /** Authentication Token */
    fun setAuthenticationToken(authToken: String)
    fun getAuthenticationToken(): String

    /** RoomDB EncryptionKey */
    fun setRoomDBEncryptionKey(encryptionKey: String)
    fun getRoomDBEncryptionKey(): String

    /** Reset Authentication Token */
    fun resetAuthenticationToken()

    /** Clear preferences */
    fun clearPreferences()
}