package com.heartcare.agni.data.local.enums

import androidx.compose.ui.graphics.Color
import com.heartcare.agni.ui.theme.HighRiskCircle
import com.heartcare.agni.ui.theme.LowRiskCircle
import com.heartcare.agni.ui.theme.ModerateRiskCircle
import com.heartcare.agni.ui.theme.VeryHighRiskCircle2

enum class CvdRiskCategory(
    val label: String,
    val min: Int,
    val max: Int,
    val color: Color
) {
    LOW(
        label = "Low (< 10%)",
        min = Int.MIN_VALUE,
        max = 9,
        color = LowRiskCircle
    ),
    MEDIUM(
        label = "Moderate (10% - 19%)",
        min = 10,
        max = 19,
        color = ModerateRiskCircle
    ),
    HIGH(
        label = "High (20% - 29%)",
        min = 20,
        max = 29,
        color = HighRiskCircle
    ),
    VERY_HIGH(
        label = "Very High (≥ 30%)",
        min = 30,
        max = Int.MAX_VALUE,
        color = VeryHighRiskCircle2
    );

    fun matches(risk: Int): Boolean {
        return risk in min..max
    }
}