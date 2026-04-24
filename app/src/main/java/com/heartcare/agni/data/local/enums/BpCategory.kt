package com.heartcare.agni.data.local.enums

import androidx.compose.ui.graphics.Color
import com.heartcare.agni.ui.theme.HighRiskCircle
import com.heartcare.agni.ui.theme.LowRiskCircle
import com.heartcare.agni.ui.theme.VeryHighRiskCircle2

enum class BpCategory(
    val label: String,
    val color: Color
) {
    NORMAL(
        label = "Normal (< 140/90 mmHg)",
        color = LowRiskCircle
    ),
    HIGH(
        label = "High (140/90 - 159/99 mmHg)",
        color = HighRiskCircle
    ),
    VERY_HIGH(
        label = "Very High (≥ 160/100 mmHg)",
        color = VeryHighRiskCircle2
    );

    companion object {
        fun from(sys: Int, dia: Int): BpCategory {
            return when {
                sys >= 160 || dia >= 100 -> VERY_HIGH
                sys >= 140 || dia >= 90 -> HIGH
                else -> NORMAL
            }
        }
    }
}