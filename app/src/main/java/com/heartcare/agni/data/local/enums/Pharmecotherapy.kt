package com.heartcare.agni.data.local.enums

enum class Pharmacotherapy(val code: String, val display: String) {
    YES_NICOTINE_REPLACEMENT_THERAPY("1", "Yes, Nicotine Replacement Therapy"),
    YES_OTHER("2", "Yes, other"),
    NO("3", "No");

    companion object {
        fun pharmacotherapyList() = entries.map { it.display }
        fun pharmacotherapyDisplayFromCode(code: String) = entries.firstOrNull { it.code == code }?.display
        fun pharmacotherapyCodeFromDisplay(display: String) = entries.firstOrNull { it.display == display }?.code
    }
}
