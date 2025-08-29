package com.heartcare.agni.data.local.repository.preference

import com.google.gson.Gson
import com.heartcare.agni.data.local.sharedpreferences.PreferenceStorage
import com.heartcare.agni.data.server.model.authentication.LoginResponse
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(private val preferenceStorage: PreferenceStorage) :
    PreferenceRepository {
    override fun setUserDetails(loginResponse: LoginResponse) {
        val gson = Gson()
        preferenceStorage.userDetails = gson.toJson(loginResponse)
    }

    override fun getUserDetails(): LoginResponse? {
        val gson = Gson()
        return gson.fromJson(preferenceStorage.userDetails, LoginResponse::class.java)
    }

    override fun setAccessToken(token: String) {
        preferenceStorage.accessToken = token
    }

    override fun getAccessToken(): String {
        return preferenceStorage.accessToken
    }

    override fun setRefreshToken(token: String) {
        preferenceStorage.refreshToken = token
    }

    override fun getRefreshToken(): String {
        return preferenceStorage.refreshToken
    }

    override fun setPin(pin: String) {
        preferenceStorage.pin = pin
    }

    override fun getPin(): String {
        return preferenceStorage.pin
    }

    override fun setSyncStatus(status: String) {
        preferenceStorage.syncStatus = status
    }

    override fun getSyncStatus() = preferenceStorage.syncStatus
    override fun setLastSyncTime(long: Long) {
        preferenceStorage.lastSyncTime = long
    }

    override fun getLastSyncTime() = preferenceStorage.lastSyncTime

    override fun setLastSyncPatient(long: Long) {
        preferenceStorage.lastPatientSyncTime = long
    }

    override fun getLastSyncPatient() = preferenceStorage.lastPatientSyncTime

    override fun setLastSyncPrescription(long: Long) {
        preferenceStorage.lastPrescriptionSyncTime = long
    }

    override fun getLastSyncPrescription() = preferenceStorage.lastPrescriptionSyncTime

    override fun setLastMedicationSyncDate(long: Long) {
        preferenceStorage.lastMedicationSyncTime = long
    }

    override fun getLastMedicationSyncDate(): Long = preferenceStorage.lastMedicationSyncTime

    override fun setLastMedicineDosageInstructionSyncDate(long: Long) {
        preferenceStorage.lastMedicineDosageInstructionSyncTime = long
    }

    override fun setLastSyncSchedule(long: Long) {
        preferenceStorage.lastScheduleSyncTime = long
    }

    override fun getLastSyncSchedule() = preferenceStorage.lastScheduleSyncTime

    override fun setLastSyncAppointment(long: Long) {
        preferenceStorage.lastAppointmentSyncTime = long
    }

    override fun getLastSyncAppointment() = preferenceStorage.lastAppointmentSyncTime

    override fun setLastSyncCVD(long: Long) {
        preferenceStorage.lastCVDSyncTime = long
    }

    override fun getLastSyncCVD() = preferenceStorage.lastCVDSyncTime

    override fun setLastSyncVital(long: Long) {
        preferenceStorage.lastVitalSyncTime = long

    }
    override fun getLastSyncVital() = preferenceStorage.lastVitalSyncTime

    override fun setLastSyncDiagnosis(long: Long) {
        preferenceStorage.lastDiagnosisSyncTime = long
    }

    override fun getLastSyncDiagnosis() = preferenceStorage.lastDiagnosisSyncTime

    override fun setLastSyncLevelRecord(long: Long) {
        preferenceStorage.lastSyncLevelRecord = long
    }

    override fun getLastSyncLevelRecord() = preferenceStorage.lastSyncLevelRecord

    override fun setLastSyncPriorDx(long: Long) {
        preferenceStorage.lastSyncPriorDxRecord = long
    }

    override fun getLastSyncPriorDx(): Long = preferenceStorage.lastSyncPriorDxRecord

    override fun setLastSyncHistoryMedication(long: Long) {
        preferenceStorage.lastSyncHistoryMedicationRecord = long
    }

    override fun getLastSyncHistoryMedication(): Long = preferenceStorage.lastSyncHistoryMedicationRecord

    override fun setLastSyncFamilyHistory(long: Long) {
        preferenceStorage.lastSyncFamilyHistoryRecord = long
    }

    override fun getLastSyncFamilyHistory(): Long = preferenceStorage.lastSyncFamilyHistoryRecord

    override fun setLastSyncAllergy(long: Long) {
        preferenceStorage.lastSyncAllergyRecord = long
    }

    override fun getLastSyncAllergy(): Long = preferenceStorage.lastSyncAllergyRecord

    override fun setLastSyncRiskFactors(long: Long) {
        preferenceStorage.lastSyncRiskFactorsRecord = long
    }

    override fun getLastSyncRiskFactors(): Long = preferenceStorage.lastSyncRiskFactorsRecord

    override fun setLastSyncTobaccoCessation(long: Long) {
        preferenceStorage.lastSyncTobaccoCessationRecord = long
    }

    override fun getLastSyncTobaccoCessation(): Long = preferenceStorage.lastSyncTobaccoCessationRecord

    override fun setLastInterventionMasterSyncDate(long: Long) {
        preferenceStorage.lastSyncInterventionMaster = long
    }

    override fun getLastInterventionMasterSyncDate(): Long = preferenceStorage.lastSyncInterventionMaster

    override fun setLastSyncIntervention(long: Long) {
        preferenceStorage.lastSyncIntervention = long
    }

    override fun getLastSyncIntervention(): Long = preferenceStorage.lastSyncIntervention

    override fun setLastExaminationMasterSyncDate(long: Long) {
        preferenceStorage.lastSyncExaminationMaster = long
    }

    override fun getLastExaminationMasterSyncDate(): Long = preferenceStorage.lastSyncExaminationMaster

    override fun setLastSyncExamination(long: Long) {
        preferenceStorage.lastSyncExamination = long
    }

    override fun getLastSyncExamination(): Long = preferenceStorage.lastSyncExamination

    override fun getLastMedicineDosageInstructionSyncDate() =
        preferenceStorage.lastMedicineDosageInstructionSyncTime

    override fun setRoomDBEncryptionKey(encryptionKey: String) {
        preferenceStorage.roomDBEncryptionKey = encryptionKey
    }

    override fun getRoomDBEncryptionKey() = preferenceStorage.roomDBEncryptionKey

    override fun clearPreferences() {
        val roomDBEncryptionKey = preferenceStorage.roomDBEncryptionKey
        preferenceStorage.clear()
        preferenceStorage.roomDBEncryptionKey = roomDBEncryptionKey
    }
}