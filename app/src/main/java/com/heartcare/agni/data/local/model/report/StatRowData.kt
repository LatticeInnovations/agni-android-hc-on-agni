package com.heartcare.agni.data.local.model.report

import androidx.compose.ui.graphics.Color


data class StatRowData(
    val label: String,
    val valueStr: String,
    val progress: Float,
    val progressColor: Color
)