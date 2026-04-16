package com.heartcare.agni.data.local.model.report

data class ServerStatDto(
        val categoryName: String,
        val maleCount: Int,
        val femaleCount: Int,
        val severityLevel: Int // 0 = Normal, 1 = Warning, 2 = Critical
    )