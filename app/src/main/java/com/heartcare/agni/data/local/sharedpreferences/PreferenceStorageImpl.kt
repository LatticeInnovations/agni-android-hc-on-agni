package com.heartcare.agni.data.local.sharedpreferences

import android.content.SharedPreferences
import androidx.core.content.edit
import com.heartcare.agni.utils.sharedpreference.LongPreference
import com.heartcare.agni.utils.sharedpreference.StringPreference

class PreferenceStorageImpl(private val sharedPreferences: SharedPreferences) : PreferenceStorage {

    override var userDetails by StringPreference(sharedPreferences, PREF_USER_DETAILS, "")
    override var accessToken by StringPreference(sharedPreferences, PREF_ACCESS_TOKEN, "")
    override var refreshToken by StringPreference(sharedPreferences, PREF_REFRESH_TOKEN, "")
    override var pin by StringPreference(sharedPreferences, PREF_PIN, "")
    override var token by StringPreference(sharedPreferences, PREF_TOKEN, "")
    override var userFhirId by StringPreference(sharedPreferences, PREF_USER_FHIR_ID, "")
    override var userName by StringPreference(sharedPreferences, PREF_USER_NAME, "")
    override var userMobile by LongPreference(sharedPreferences, PREF_USER_MOBILE, 0L)
    override var userEmail by StringPreference(sharedPreferences, PREF_USER_EMAIL, "")
    override var userRoleId by StringPreference(sharedPreferences, PREF_USER_ROLE_ID, "")
    override var userRole by StringPreference(sharedPreferences, PREF_USER_ROLE, "")
    override var organizationFhirId by StringPreference(
        sharedPreferences,
        PREF_ORGANIZATION_FHIR_ID,
        ""
    )
    override var organization by StringPreference(sharedPreferences, PREF_ORGANIZATION, "")
    override var roomDBEncryptionKey by StringPreference(
        sharedPreferences,
        PREF_ROOM_ENCRYPTION_KEY,
        ""
    )
    override var syncStatus by StringPreference(
        sharedPreferences,
        PREF_SYNC_STATUS,
        ""
    )
    override var lastSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_SYNC_TIME,
        0L
    )
    override var lastPatientSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_PATIENT_SYNC_TIME,
        0L
    )
    override var lastPrescriptionSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_PRESCRIPTION_SYNC_TIME,
        0L
    )
    override var lastMedicationSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_MEDICATION_SYNC_TIME,
        0L
    )
    override var lastMedicineDosageInstructionSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_MEDICINE_DOSAGE_INSTRUCTION_SYNC_TIME,
        0L
    )
    override var lastScheduleSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_SCHEDULE_SYNC_TIME,
        0L
    )
    override var lastAppointmentSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_APPOINTMENT_SYNC_TIME,
        0L
    )
    override var lastCVDSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_CVD_SYNC_TIME,
        0L
    )
    override var lastVitalSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_VITAL_SYNC_TIME,
        0L
    )
    override var lastSymptomsSyncTime by LongPreference(
        sharedPreferences,
        PREF_LAST_SYM_DIAG_SYNC_TIME,
        0L
    )

    override var lastSyncLevelRecord by LongPreference(
        sharedPreferences,
        PREF_LAST_LEVELS_SYNC_TIME,
        0L
    )
    override var lastSyncPriorDxRecord by LongPreference(
        sharedPreferences,
        PREF_LAST_PRIOR_DX_SYNC_TIME,
        0L
    )
    override var lastSyncHistoryMedicationRecord by LongPreference(
        sharedPreferences,
        PREF_LAST_HISTORY_MEDICATION_SYNC_TIME,
        0L
    )
    override var lastSyncFamilyHistoryRecord by LongPreference(
        sharedPreferences,
        PREF_LAST_FAMILY_HISTORY_SYNC_TIME,
        0L
    )
    override var lastSyncAllergyRecord by LongPreference(
        sharedPreferences,
        PREF_LAST_ALLERGY_SYNC_TIME,
        0L
    )

    override var lastSyncRiskFactorsRecord by LongPreference(
        sharedPreferences,
        PREF_LAST_RISK_FACTORS_SYNC_TIME,
        0L
    )

    override var lastSyncTobaccoCessationRecord by LongPreference(
        sharedPreferences,
        PREF_LAST_TOBACCO_CESSATION_SYNC_TIME,
        0L
    )

    override var lastSyncInterventionMaster by LongPreference(
        sharedPreferences,
        PREF_LAST_INTERVENTION_MASTER_SYNC_TIME,
        0L
    )

    override var lastSyncIntervention by LongPreference(
        sharedPreferences,
        PREF_LAST_INTERVENTION_SYNC_TIME,
        0L
    )

    override var lastSyncExaminationMaster by LongPreference(
        sharedPreferences,
        PREF_LAST_EXAMINATION_MASTER_SYNC_TIME,
        0L
    )

    override var lastSyncExamination by LongPreference(
        sharedPreferences,
        PREF_LAST_EXAMINATION_SYNC_TIME,
        0L
    )

    override fun clear() {
        sharedPreferences.edit {
            clear()
            commit()
        }
    }

    companion object {
        const val PREFS_NAME = "fhir_android"

        const val PREF_USER_DETAILS = "pref_user_details"
        const val PREF_ACCESS_TOKEN = "pref_access_token"
        const val PREF_REFRESH_TOKEN = "pref_refresh_token"
        const val PREF_PIN = "pref_pin"
        const val PREF_TOKEN = "pref_token"

        const val PREF_USER_FHIR_ID = "pref_user_fhir_id"
        const val PREF_USER_NAME = "pref_user_name"
        const val PREF_USER_MOBILE = "pref_user_mobile"
        const val PREF_USER_EMAIL = "pref_user_email"
        const val PREF_USER_ROLE_ID = "pref_user_role_id"
        const val PREF_USER_ROLE = "pref_user_role"
        const val PREF_ORGANIZATION_FHIR_ID = "pref_organization_fhir_id"
        const val PREF_ORGANIZATION = "pref_organization"

        const val PREF_ROOM_ENCRYPTION_KEY = "pref_room_encryption_key"

        const val PREF_SYNC_STATUS = "pref_sync_status"
        const val PREF_LAST_SYNC_TIME = "pref_last_sync_time"
        const val PREF_LAST_PATIENT_SYNC_TIME = "pref_last_patient_sync_time"
        const val PREF_LAST_PRESCRIPTION_SYNC_TIME = "pref_last_prescription_sync_time"
        const val PREF_LAST_MEDICATION_SYNC_TIME = "pref_last_medication_sync_time"
        const val PREF_LAST_MEDICINE_DOSAGE_INSTRUCTION_SYNC_TIME =
            "pref_last_medication_timing_sync_time"
        const val PREF_LAST_SCHEDULE_SYNC_TIME = "pref_last_schedule_sync_time"
        const val PREF_LAST_APPOINTMENT_SYNC_TIME = "pref_last_appointment_sync_time"
        const val PREF_LAST_CVD_SYNC_TIME = "pref_last_cvd_sync_time"
        const val PREF_LAST_VITAL_SYNC_TIME = "pref_last_vital_sync_time"
        const val PREF_LAST_SYM_DIAG_SYNC_TIME = "pref_last_sym_diag_sync_time"
        const val PREF_LAST_LEVELS_SYNC_TIME = "pref_last_levels_sync_time"
        const val PREF_LAST_PRIOR_DX_SYNC_TIME = "pref_last_prior_dx_sync_time"
        const val PREF_LAST_HISTORY_MEDICATION_SYNC_TIME = "pref_last_history_medication_sync_time"
        const val PREF_LAST_FAMILY_HISTORY_SYNC_TIME = "pref_last_family_history_sync_time"
        const val PREF_LAST_ALLERGY_SYNC_TIME = "pref_last_allergy_sync_time"
        const val PREF_LAST_RISK_FACTORS_SYNC_TIME = "pref_last_risk_factors_sync_time"
        const val PREF_LAST_TOBACCO_CESSATION_SYNC_TIME = "pref_last_tobacco_cessation_sync_time"
        const val PREF_LAST_INTERVENTION_MASTER_SYNC_TIME = "pref_last_intervention_master_sync_time"
        const val PREF_LAST_INTERVENTION_SYNC_TIME = "pref_last_intervention_sync_time"
        const val PREF_LAST_EXAMINATION_MASTER_SYNC_TIME = "pref_last_examination_master_sync_time"
        const val PREF_LAST_EXAMINATION_SYNC_TIME = "pref_last_examination_sync_time"
    }
}