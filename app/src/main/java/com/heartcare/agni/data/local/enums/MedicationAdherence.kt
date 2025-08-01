package com.heartcare.agni.data.local.enums

enum class MedicationAdherence(val code: String, val display: String) {
    TAKING_AS_PRESCRIBED("1", "Taking medication as prescribed"),
    DISCONTINUED_POOR_COMPLIANCE("2", "Discontinued due to poor compliance"),
    FORGETS_SOMETIMES("3", "Forget to take medication sometimes"),
    DISCONTINUED_STOCK_OUT("4", "Discontinued due to pharmacy stock-out");

    companion object {
        fun getAdherenceList() = MedicationAdherence.entries.map { it.display }
        fun getAdherenceCode(display: String) = MedicationAdherence.entries.firstOrNull { it.display == display }?.code
        fun getAdherenceDisplay(code: String) = MedicationAdherence.entries.first { it.code == code }.display
    }
}
