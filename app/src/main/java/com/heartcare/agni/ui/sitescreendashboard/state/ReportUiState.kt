package com.heartcare.agni.ui.sitescreendashboard.state

import com.heartcare.agni.data.local.model.report.StatRowData

data class ReportUiState(
    val totalScreened: Int = 0,
    val totalMale: Int = 0,
    val totalFemale: Int = 0,
    val totalOther: Int = 0,
    val ageGroups: List<Pair<String, String>> = emptyList(),
    val bmiTotal: Int = 0,
    val bmiStats: List<StatRowData> = emptyList(),
    val bloodPressureTotal: Int = 0,
    val bloodPressureStats: List<StatRowData> = emptyList(),
    val smokingTotal: Int = 0,
    val smokingStats: List<StatRowData> = emptyList(),
    val bloodSugarFastingTotal: Int = 0,
    val bloodSugarFastingStats: List<StatRowData> = emptyList(),
    val bloodSugarRandomTotal: Int = 0,
    val bloodSugarRandomStats: List<StatRowData> = emptyList(),
    val cholesterolTotal: Int = 0,
    val cholesterolStats: List<StatRowData> = emptyList(),
    val cvdRiskTotal: Int = 0,
    val cvdRiskStats: List<StatRowData> = emptyList()
)