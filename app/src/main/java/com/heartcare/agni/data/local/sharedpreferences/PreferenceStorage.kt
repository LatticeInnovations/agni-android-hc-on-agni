package com.heartcare.agni.data.local.sharedpreferences

interface PreferenceStorage {

    /** User Details */
    var userDetails: String

    /** Access Token */
    var accessToken: String

    /** Refresh Token */
    var refreshToken: String

    /** M-Pin */
    var pin: String

    /** Room DB Encryption Key */
    var roomDBEncryptionKey: String

    /** Last Sync Time */
    var syncStatus: String
    var lastSyncTime: Long
    var lastPatientSyncTime: Long
    var lastPrescriptionSyncTime: Long
    var lastMedicationSyncTime: Long
    var lastMedicineDosageInstructionSyncTime: Long
    var lastScheduleSyncTime: Long
    var lastAppointmentSyncTime: Long
    var lastCVDSyncTime: Long
    var lastVitalSyncTime: Long
    var lastCampaignScheduleSyncTime: Long
    var lastCampaignAppointmentSyncTime: Long
    var lastCampaignCVDSyncTime: Long
    var lastCampaignVitalSyncTime: Long

    var lastDiagnosisSyncTime: Long

    var lastSyncLevelRecord: Long
    var lastSyncPriorDxRecord: Long
    var lastSyncCampaignPriorDxRecord: Long
    var lastSyncHistoryMedicationRecord: Long
    var lastSyncCampaignHistoryMedicationRecord: Long
    var lastSyncFamilyHistoryRecord: Long
    var lastSyncCampaignFamilyHistoryRecord: Long
    var lastSyncAllergyRecord: Long
    var lastSyncCampaignAllergyRecord: Long
    var lastSyncRiskFactorsRecord: Long
    var lastSyncCampaignRiskFactorRecord: Long
    var lastSyncTobaccoCessationRecord: Long
    var lastSyncInterventionMaster: Long
    var lastSyncIntervention: Long
    var lastSyncExaminationMaster: Long
    var lastSyncExamination: Long
    var lastSyncDiagnosisMaster: Long
    var lastSyncHealthFacility: Long
    var lastSyncReferral: Long
    var lastSyncScreeningSiteMaster: Long
    var isScreeningSiteSeeded: Boolean

    fun clear()
}