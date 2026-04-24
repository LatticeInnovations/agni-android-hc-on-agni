package com.heartcare.agni.data.local.enums

import androidx.compose.ui.graphics.Color
import com.heartcare.agni.ui.theme.HighRiskCircle
import com.heartcare.agni.ui.theme.LowRiskCircle

enum class BloodSugarType(val thresholdMmol: Double) {
    RANDOM(11.1),
    FASTING(6.1)
}

enum class BloodSugarCategory(
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
        fun from(
            value: Double,
            unit: String,
            type: BloodSugarType
        ): BloodSugarCategory {

            val mmol = when (unit.lowercase()) {
                "mg/dl" -> value * 0.0555
                else -> value
            }

            return if (mmol >= type.thresholdMmol) ABOVE_NORMAL else NORMAL
        }
    }
}