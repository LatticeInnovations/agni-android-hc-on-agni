package com.heartcare.agni.data.local.enums

import androidx.compose.ui.graphics.Color
import com.heartcare.agni.ui.theme.LowRiskCircle
import com.heartcare.agni.ui.theme.VeryHighRiskCircle2

enum class YesNoEnum(val display: String, val code: Int, val boolean: Boolean, val color: Color) {
    YES("Yes", 1, true, VeryHighRiskCircle2),
    NO("No", 0, false, LowRiskCircle);

    companion object {
        fun listOfDisplay() = entries.map { it.display }
        fun codeFromDisplay(display: String) = entries.first { it.display == display }.code
        fun booleanFromDisplay(display: String) = entries.firstOrNull { it.display == display }?.boolean
        fun displayFromCode(code: Int) = entries.first { it.code == code }.display
    }
}