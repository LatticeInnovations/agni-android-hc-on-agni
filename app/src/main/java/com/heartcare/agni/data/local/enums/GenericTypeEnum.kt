package com.heartcare.agni.data.local.enums

enum class GenericTypeEnum(val number: Int, val value: String) {
    PATIENT(1, "Patient"),
    FHIR_IDS(2, "FHIR_IDS"),
    PRESCRIPTION(3, "Prescription"),
    APPOINTMENT(4, "Appointment"),
    SCHEDULE(5, "Schedule"),
    LAST_UPDATED(6, "LAST_UPDATED"),
    FHIR_IDS_PRESCRIPTION(7, "FHIR_IDS_PRESCRIPTION"),
    CVD(8, "cvd_record"),
    VITAL(9, "Vital"),
    DIAGNOSIS(10, "diagnosis"),
    PRIOR_DX(11, "prior_dx"),
    HISTORY_MEDICATION(12, "history_medication"),
    FAMILY_HISTORY(13, "family_history"),
    ALLERGY(14, "allergy"),
    RISK_FACTOR(15, "risk_factor"),
    TOBACCO_CESSATION(16, "tobacco_cessation"),
    INTERVENTION(17, "intervention"),
    EXAMINATION(18, "examination"),
    REFERRAL(19, "referral"),
    CAMPAIGN_APPOINTMENT(20, "Campaign_Appointment"),
    CAMPAIGN_SCHEDULE(21, "Campaign_Schedule"),
    CAMPAIGN_CVD(22, "Campaign_CVD_Record"),
    CAMPAIGN_VITAL(23, "Campaign_Vital_Record");
}