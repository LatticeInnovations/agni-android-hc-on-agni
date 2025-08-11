package com.heartcare.agni.data.local.enums

enum class TobaccoUsage(val code: String, val display: String) {
    YES_EVERY_DAY("1", "Yes, every day"),
    YES_BUT_NOT_EVERY_DAY("2", "Yes, but not every day"),
    NO_I_DO_NOT_USE_TOBACCO("3", "No, I do not use tobacco");

    companion object {
        fun tobaccoUsageList() = entries.map { it.display }
        fun tobaccoUsageDisplayFromCode(code: String) = entries.firstOrNull { it.code == code }?.display
        fun tobaccoUsageCodeFromDisplay(display: String) = entries.firstOrNull { it.display == display }?.code
    }
}