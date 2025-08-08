package com.heartcare.agni.data.local.enums

enum class SaltFrequencyEnum (val code: Int, val display: String) {
    NEVER(1, "Never"),
    RARELY(2, "Rarely"),
    SOMETIMES(3, "Sometimes"),
    OFTEN(4, "Often"),
    ALWAYS(5, "Always"),
    DO_NOT_KNOW(6, "Don't know");

    companion object {
        fun listOfSaltFrequency() = entries.map { it.display }
        fun saltFrequencyCodeFromDisplay(display: String) = entries.firstOrNull { it.display == display }?.code
        fun saltFrequencyDisplayFromCode(code: Int) = entries.firstOrNull { it.code == code }?.display
    }
}