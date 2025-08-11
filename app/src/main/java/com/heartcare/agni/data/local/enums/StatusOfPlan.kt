package com.heartcare.agni.data.local.enums

enum class StatusOfPlan(val code: String, val display: String) {
    ACTIVE("1", "Active"),
    COMPLETED("2", "Completed"),
    ABANDONED("3", "Abandoned");

    companion object {
        fun statusOfPlanList() = entries.map { it.display }
        fun statusOfPlanDisplayFromCode(code: String) = entries.firstOrNull { it.code == code }?.display
        fun statusOfPlanCodeFromDisplay(display: String) = entries.firstOrNull { it.display == display }?.code
    }
}
