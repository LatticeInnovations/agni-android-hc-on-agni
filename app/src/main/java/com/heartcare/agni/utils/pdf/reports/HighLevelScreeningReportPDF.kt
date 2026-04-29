package com.heartcare.agni.utils.pdf.reports

import com.heartcare.agni.data.local.model.report.StatRowData
import com.heartcare.agni.ui.sitescreendashboard.state.ReportUiState
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportFooterSection
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportHeaderSection
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportPdfCss

object HighLevelScreeningReportPDF {

    fun getHighLevelScreeningReportHTML(
        metaData: String,
        currentState: ReportUiState,
        footerData: String
    ): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
              <title>High-Level Screening Report</title>
              <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
              ${reportPdfCss()}
            </head>
            <body>
                ${reportHeaderSection(title = "High-Level Screening Report", metaData = metaData, currentState = currentState)}
                ${getAgeGroupDistributionTable(ageGroup = currentState.ageGroups.map { (first, second) -> listOf(first, second) })}
                ${getBMICategoriesTable(
                    total = currentState.bmiTotal,
                    stats = currentState.bmiStats.toTableStats()
                )}
                ${getBloodPressureTable(
                    total = currentState.bloodPressureTotal,
                    stats = currentState.bloodPressureStats.toTableStats()
                )}
                ${getSmokingStatusTable(
                    total = currentState.smokingTotal,
                    stats = currentState.smokingStats.toTableStats()
                )}
                ${getBloodSugarTable(
                    fastingTotal = currentState.bloodSugarFastingTotal,
                    fastingStats = currentState.bloodSugarFastingStats.toTableStats(),
                    randomTotal = currentState.bloodSugarRandomTotal,
                    randomStats = currentState.bloodSugarRandomStats.toTableStats()
                )}
                ${getTotalCholesterolTable(
                    total = currentState.cholesterolTotal,
                    stats = currentState.cholesterolStats.toTableStats()
                )}
                ${getCVDRiskTable(
                    total = currentState.cvdRiskTotal,
                    stats = currentState.cvdRiskStats.toTableStats()
                )}
                ${reportFooterSection(title = "High-Level Screening Report - $footerData")}
            </body>
            </html>
        """.trimIndent()
    }

    private fun getAgeGroupDistributionTable(
        ageGroup: List<List<String>>
    ): String {
        val table = CommonPDFComponents.buildTable(
            headers = listOf("Age Range", "Count"),
            rows = ageGroup,
            columnWeights = listOf("2fr", "1fr")
        )

        return CommonPDFComponents.buildSection("Age Group Distribution", table)
    }

    private fun getBMICategoriesTable(
        total: Int,
        stats: List<List<String>>
    ): String {
        val table = CommonPDFComponents.buildTable(
            headers = listOf("Category", "Percentage", "Count"),
            rows = stats,
            columnWeights = listOf("2fr", "1fr", "1fr"),
            highlightColumns = setOf(1)
        )

        return CommonPDFComponents.buildSection("BMI Categories ($total)", table)
    }

    private fun getBloodPressureTable(
        total: Int,
        stats: List<List<String>>
    ): String {
        val table = CommonPDFComponents.buildTable(
            headers = listOf("Category", "Percentage", "Count"),
            rows = stats,
            columnWeights = listOf("2fr", "1fr", "1fr"),
            highlightColumns = setOf(1)
        )

        return CommonPDFComponents.buildSection("Blood Pressure ($total)", table)
    }

    private fun getSmokingStatusTable(
        total: Int,
        stats: List<List<String>>
    ): String {
        val table = CommonPDFComponents.buildTable(
            headers = listOf("Status", "Percentage", "Count"),
            rows = stats,
            columnWeights = listOf("2fr", "1fr", "1fr"),
            highlightColumns = setOf(1)
        )

        return CommonPDFComponents.buildSection("Smoking Status ($total)", table)
    }

    private fun getBloodSugarTable(
        fastingTotal: Int,
        fastingStats: List<List<String>>,
        randomTotal: Int,
        randomStats: List<List<String>>
    ): String {

        val fastingTable = CommonPDFComponents.buildTable(
            headers = listOf("Category", "Percentage", "Count"),
            rows = fastingStats,
            columnWeights = listOf("2fr", "1fr", "1fr"),
            highlightColumns = setOf(1)
        )

        val randomTable = CommonPDFComponents.buildTable(
            headers = listOf("Category", "Percentage", "Count"),
            rows = randomStats,
            columnWeights = listOf("2fr", "1fr", "1fr"),
            highlightColumns = setOf(1)
        )

        return """
        <div class="section-block">
            <div class="section-title">Blood Sugar (${fastingTotal + randomTotal})</div>

            <div class="sub-section-title">Fasting (${fastingTotal})</div>
            $fastingTable

            <div class="sub-section-title">Random (${randomTotal})</div>
            $randomTable
        </div>
    """.trimIndent()
    }

    private fun getTotalCholesterolTable(
        total: Int,
        stats: List<List<String>>
    ): String {
        val table = CommonPDFComponents.buildTable(
            headers = listOf("Category", "Percentage", "Count"),
            rows = stats,
            columnWeights = listOf("2fr", "1fr", "1fr"),
            highlightColumns = setOf(1)
        )

        return CommonPDFComponents.buildSection("Total Cholesterol ($total)", table)
    }

    private fun getCVDRiskTable(
        total: Int,
        stats: List<List<String>>
    ): String {
        val table = CommonPDFComponents.buildTable(
            headers = listOf("Category", "Percentage", "Count"),
            rows = stats,
            columnWeights = listOf("2fr", "1fr", "1fr"),
            highlightColumns = setOf(1)
        )

        return CommonPDFComponents.buildSection("CVD Risk ($total)", table)
    }

    private fun List<StatRowData>.toTableStats(): List<List<String>> {
        return map {
            listOf(
                it.label,
                "${it.percentage}%",
                "${it.maleCount + it.femaleCount + it.otherCount} " +
                        "(F ${it.femaleCount}, M ${it.maleCount}, O ${it.otherCount})"
            )
        }
    }
}