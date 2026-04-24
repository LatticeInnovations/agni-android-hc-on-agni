package com.heartcare.agni.data.local.enums

enum class DateRangeEnum(val label: String) {
    LAST_7_DAYS("Last 7 days"),
    LAST_30_DAYS("Last 30 days"),
    LAST_90_DAYS("Last 90 days"),
    CUSTOM_RANGE("Custom range");

    companion object {
        fun getDateRangeOptions() = entries.map { it.label }
    }
}