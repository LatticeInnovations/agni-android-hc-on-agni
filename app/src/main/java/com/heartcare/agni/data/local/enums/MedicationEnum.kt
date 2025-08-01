package com.heartcare.agni.data.local.enums

enum class MedicationEnum(val code: String, val display: String) {
    HYPERTENSION("38341003", "Hypertension"),
    DIABETES_ORAL("73211009", "Diabetes: oral pill"),
    DIABETES_INSULIN("44054006", "Diabetes: insulin"),
    HYPERCHOLESTEROLEMIA("13644009", "Hypercholesterolaemia: statin"),
    HIGH_CVD_RISK_ASPIRIN("49601007", "High CVD risk or prophylactic use: aspirin"),
    PREV_CVD_ASPIRIN("131531000119103", "Previous CVD event: aspirin"),
    PREV_CVD_ANTICOAGULANT("711150003", "Previous CVD event: anticoagulant"),
    ASTHMA("195967001", "Asthma"),
    COPD("13645005", "Chronic obstructive pulmonary disease (COPD)"),
    CKD("709044004", "Chronic Kidney Disease"),
    CANCER("363346000", "Cancer"),
    CHRONIC_PAIN("82423001", "Chronic pain"),
    TRADITIONAL_REMEDIES("421563008", "Traditional remedies"),
    TUBERCULOSIS("56717001", "Tuberculosis"),
    AIDS("62479008", "AIDS"),
    OTHERS("74964007", "Others"),
    SIDE_EFFECTS("side_effects", "Side effects");

    companion object {
        fun getMedicationList(): List<String> = MedicationEnum.entries.map { it.display }
        fun getMedicationFromCode(code: String): String = MedicationEnum.entries.first { it.code == code }.display
        fun getCodeFromMedication(display: String): String = MedicationEnum.entries.first { it.display == display }.code
    }
}