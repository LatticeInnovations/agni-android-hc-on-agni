package com.heartcare.agni.data.local.enums

import androidx.compose.ui.graphics.Color
import com.heartcare.agni.ui.theme.HighRiskCircle
import com.heartcare.agni.ui.theme.LowRiskCircle

enum class CholesterolCategory(
    val label: String,
    val color: Color
) {
    NORMAL(
        label = "Normal",
        color = LowRiskCircle
    ),
    ABOVE_NORMAL(
        label = "Above Normal",
        color = HighRiskCircle
    );

    companion object {
        fun from(value: Double, unit: String): CholesterolCategory {
            val mmol = when (unit.lowercase()) {
                "mg/dl" -> value * 0.0259
                else -> value
            }

            return if (mmol >= 5.2) ABOVE_NORMAL else NORMAL
        }
    }
}