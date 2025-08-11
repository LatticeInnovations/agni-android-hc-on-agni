package com.heartcare.agni.data.local.enums

enum class YesNoEnum(val display: String, val code: Int, val boolean: Boolean) {
    YES("Yes", 1, true),
    NO("No", 0, false);

    companion object {
        fun listOfDisplay() = entries.map { it.display }
        fun codeFromDisplay(display: String) = entries.first { it.display == display }.code
        fun booleanFromDisplay(display: String) = entries.firstOrNull { it.display == display }?.boolean
        fun displayFromCode(code: Int) = entries.first { it.code == code }.display
    }
}