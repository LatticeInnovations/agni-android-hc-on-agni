package com.heartcare.agni.utils.pdf.reports

import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportFooterSection
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportHeaderSection
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportPdfCss

object HighLevelScreeningReportPDF {

    fun getHighLevelScreeningReportHTML(): String {
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
                ${reportHeaderSection(title = "High-Level Screening Report")}
                ${getAgeGroupDistributionTable()}
                ${getBMICategoriesTable()}
                ${getBloodPressureTable()}
                ${getSmokingStatusTable()}
                ${getBloodSugarTable()}
                ${getTotalCholesterolTable()}
                ${getCVDRiskTable()}
                ${reportFooterSection(title = "High-Level Screening Report")}
            </body>
            </html>
        """.trimIndent()
    }

    private fun getAgeGroupDistributionTable(): String {
        val table = CommonPDFComponents.buildTable(
            headers = listOf("Age Range", "Count"),
            rows = listOf(
                listOf("18-29", "198"),
                listOf("30-44", "412"),
                listOf("45-59", "389"),
                listOf("60+", "248")
            ),
            columnWeights = listOf("2fr", "1fr")
        )

        return CommonPDFComponents.buildSection("Age Group Distribution", table)
    }

    private fun getBMICategoriesTable(): String {
        val table = CommonPDFComponents.buildTable(
            headers = listOf("Category", "Percentage", "Count"),
            rows = listOf(
                listOf("Underweight (≤ 18.5 kg/m²)", "5%", "62"),
                listOf("Normal (18.6 - 24.9 kg/m²)", "43%", "534"),
                listOf("Overweight (25.0 - 29.9 kg/m²)", "32%", "398"),
                listOf("Obese (≥ 30.0 kg/m²)", "20%", "253")
            ),
            columnWeights = listOf("2fr", "1fr", "1fr"),
            highlightColumns = setOf(1)
        )

        return CommonPDFComponents.buildSection("BMI Categories", table)
    }

    private fun getBloodPressureTable(): String {
        val table = CommonPDFComponents.buildTable(
            headers = listOf("Category", "Percentage", "Count"),
            rows = listOf(
                listOf("Normal (< 140/90 mmHg)", "43%", "534 (F 30, M 32)"),
                listOf("High (140/90 - 159/99 mmHg)", "32%", "398 (F 30, M 32)"),
                listOf("Very High (≥ 160/100 mmHg)", "20%", "253 (F 30, M 32)")
            ),
            columnWeights = listOf("2fr", "1fr", "1fr"),
            highlightColumns = setOf(1)
        )

        return CommonPDFComponents.buildSection("Blood Pressure", table)
    }

    private fun getSmokingStatusTable(): String {
        val table = CommonPDFComponents.buildTable(
            headers = listOf("Status", "Percentage", "Count"),
            rows = listOf(
                listOf("Yes", "57%", "253 (F 30, M 32)"),
                listOf("No", "43%", "534 (F 30, M 32)")
            ),
            columnWeights = listOf("2fr", "1fr", "1fr"),
            highlightColumns = setOf(1)
        )

        return CommonPDFComponents.buildSection("Smoking Status", table)
    }

    private fun getBloodSugarTable(): String {

        val fastingTable = CommonPDFComponents.buildTable(
            headers = listOf("Category", "Percentage", "Count"),
            rows = listOf(
                listOf("Normal", "43%", "534 (F 30, M 32)"),
                listOf("Above Normal", "32%", "398 (F 30, M 32)")
            ),
            columnWeights = listOf("2fr", "1fr", "1fr"),
            highlightColumns = setOf(1)
        )

        val randomTable = CommonPDFComponents.buildTable(
            headers = listOf("Category", "Percentage", "Count"),
            rows = listOf(
                listOf("Normal", "43%", "534 (F 30, M 32)"),
                listOf("Above Normal", "32%", "398 (F 30, M 32)")
            ),
            columnWeights = listOf("2fr", "1fr", "1fr"),
            highlightColumns = setOf(1)
        )

        return """
        <div class="section-block">
            <div class="section-title">Blood Sugar</div>

            <div class="sub-section-title">Fasting</div>
            $fastingTable

            <div class="sub-section-title">Random</div>
            $randomTable
        </div>
    """.trimIndent()
    }

    private fun getTotalCholesterolTable(): String {
        val table = CommonPDFComponents.buildTable(
            headers = listOf("Category", "Percentage", "Count"),
            rows = listOf(
                listOf("Normal", "43%", "534 (F 30, M 32)"),
                listOf("Above Normal", "32%", "398 (F 30, M 32)")
            ),
            columnWeights = listOf("2fr", "1fr", "1fr"),
            highlightColumns = setOf(1)
        )

        return CommonPDFComponents.buildSection("Total Cholesterol", table)
    }

    private fun getCVDRiskTable(): String {
        val table = CommonPDFComponents.buildTable(
            headers = listOf("Category", "Percentage", "Count"),
            rows = listOf(
                listOf("Low (<10%)", "43%", "534 (F 30, M 32)"),
                listOf("Moderate (10-20%)", "32%", "398 (F 30, M 32)"),
                listOf("High (>20%)", "20%", "253 (F 30, M 32)")
            ),
            columnWeights = listOf("2fr", "1fr", "1fr"),
            highlightColumns = setOf(1)
        )

        return CommonPDFComponents.buildSection("CVD Risk", table)
    }
}