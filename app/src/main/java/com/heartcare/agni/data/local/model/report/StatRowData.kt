package com.heartcare.agni.data.local.model.report

import androidx.compose.ui.graphics.Color


data class StatRowData(
    val label: String,
    val maleCount: Int,
    val femaleCount: Int,
    val otherCount: Int,
    val percentage: Int,
    val progress: Float,
    val progressColor: Color
)