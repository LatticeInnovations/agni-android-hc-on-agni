package com.heartcare.agni.data.local.enums

enum class FamilyHistoryEnum(val code: String, val display: String) {
    HEART_DISEASE("56265001", "Heart attack/ angina/ other heart disease"),
    TIA("266257000", "Transient ischaemic attack (TIA)"),
    DIABETES("73211009", "Diabetes"),
    CHRONIC_KIDNEY_DISEASE("431855005", "Chronic kidney disease");

    companion object {
        fun getFamilyHistoryConditionList() = entries.map { it.display }
        fun familyHistoryDisplayFromCode(code: String) = entries.first { it.code == code }.display
        fun familyHistoryCodeFromDisplay(display: String) = entries.first { it.display == display }.code
    }
}