package com.heartcare.agni.utils.pdf.reports

import com.heartcare.agni.ui.sitescreendashboard.state.ReportUiState
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.buildSection
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.buildTable
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportFooterSection
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportHeaderSection
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportPdfCss

object RiskRoasterReportPDF {
    fun getRiskRoasterReportHTML(
        metaData: String,
        currentState: ReportUiState
    ): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
              <title>Risk Roaster Report</title>
              <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
              ${reportPdfCss()}
            </head>
            <body>
                ${reportHeaderSection(title = "Risk Roaster Report", metaData, currentState)}
                ${getHighRiskPatientsSection()}
                ${getModerateRiskPatientsSection()}
                ${reportFooterSection(title = "Risk Roaster Report")}
            </body>
            </html>
        """.trimIndent()
    }

    private fun getHighRiskPatientsSection(): String {
        val table = buildTable(
            headers = listOf("Name", "Age", "Gender", "Risk %", "Mobile", "Address"),
            rows = listOf(
                listOf("Sarah Naupa", "56", "Male", "34%", "555-0123", "Shefa, Port Villa, Erakor"),
                listOf("James O’Hara", "29", "Male", "42%", "555-0123", "Shefa, Port Villa, Erakor"),
                listOf("Mia Chen", "41", "Female", "28%", "555-0123", "Shefa, Port Villa, Erakor"),
                listOf("Liam Rodriguez", "37", "Male", "50%", "555-0123", "Shefa, Port Villa, Erakor"),
                listOf("Ava Patel", "33", "Female", "39%", "555-0123", "Shefa, Port Villa, Erakor"),
                listOf("Ethan Kim", "45", "Male", "31%", "555-0123", "Shefa, Port Villa, Erakor")
            ),
            columnWeights = listOf("1.2fr", "0.8fr", "1fr", "0.8fr", "1.2fr", "2fr"),
                    tableClass = "left-last-col"
        )

        return buildSection(
            "High & Very High Risk (≥20%): 6",
            table
        )
    }

    private fun getModerateRiskPatientsSection(): String {
        val table = buildTable(
            headers = listOf("Name", "Age", "Gender", "Risk %", "Mobile", "Address"),
            rows = listOf(
                listOf("Liam Ortega", "29", "Female", "14%", "555-0123", "Shefa, Port Villa, Erakor"),
                listOf("Maya Chen", "41", "Female", "16%", "555-0123", "Shefa, Port Villa, Erakor"),
                listOf("Olivia Reyes", "63", "Female", "18%", "555-0123", "Shefa, Port Villa, Erakor"),
                listOf("Noah Kim", "22", "Male", "19%", "555-0123", "Shefa, Port Villa, Erakor")
            ),
            columnWeights = listOf("1.2fr", "0.8fr", "1fr", "0.8fr", "1.2fr", "2fr"),
            tableClass = "left-last-col"
        )

        return buildSection(
            "Moderate Risk (10% to < 20%): 4",
            table
        )
    }
}