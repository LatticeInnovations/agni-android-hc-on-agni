package com.heartcare.agni.data.local.enums

import androidx.compose.ui.graphics.Color
import com.heartcare.agni.ui.theme.HighRiskCircle
import com.heartcare.agni.ui.theme.LowRiskCircle
import com.heartcare.agni.ui.theme.VeryHighRiskCircle2

enum class BmiCategory(
    val label: String,
    val min: Double,
    val max: Double,
    val color: Color
) {
    UNDERWEIGHT(
        label = "Underweight (≤ 18.5 kg/m²)",
        min = Double.MIN_VALUE,
        max = 18.5,
        color = HighRiskCircle
    ),
    NORMAL(
        label = "Normal (18.6 - 24.9 kg/m²)",
        min = 18.6,
        max = 24.9,
        color = LowRiskCircle
    ),
    OVERWEIGHT(
        label = "Overweight (25.0 - 29.9 kg/m²)",
        min = 25.0,
        max = 29.9,
        color = HighRiskCircle
    ),
    OBESE(
        label = "Obese (≥ 30.0 kg/m²)",
        min = 30.0,
        max = Double.MAX_VALUE,
        color = VeryHighRiskCircle2
    );

    fun matches(bmi: Double): Boolean {
        return bmi in min..max
    }
}