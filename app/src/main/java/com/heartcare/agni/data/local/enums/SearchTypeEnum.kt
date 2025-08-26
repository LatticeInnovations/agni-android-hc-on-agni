package com.heartcare.agni.data.local.enums

enum class SearchTypeEnum(val value: String) {

    PATIENT("Patient"),
    ACTIVE_INGREDIENT("Active Ingredient"),
    SYMPTOM("Symptom"),
    DIAGNOSIS("Diagnosis"),
    INTERVENTIONS("Interventions"),
    TEST_EXAMINATION("Test and Examination");

    companion object {
        fun fromString(value: String) = entries.first { it.value == value }
    }
}