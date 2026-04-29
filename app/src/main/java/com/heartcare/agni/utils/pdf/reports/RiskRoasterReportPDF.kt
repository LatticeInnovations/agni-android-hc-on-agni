package com.heartcare.agni.utils.pdf.reports

import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.sitescreendashboard.state.ReportUiState
import com.heartcare.agni.utils.converters.responseconverter.StringUtils.capitalizeFirst
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAge
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.buildSection
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.buildTable
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportFooterSection
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportHeaderSection
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportPdfCss

object RiskRoasterReportPDF {
    fun getRiskRoasterReportHTML(
        metaData: String,
        currentState: ReportUiState,
        footerData: String
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
                ${getHighRiskPatientsSection(
                    stats = currentState.patientAndCVD
                        .filter { it.second.risk >= 20 }
                        .toRiskRoasterRows()
                )}
                ${getModerateRiskPatientsSection(
                    stats = currentState.patientAndCVD
                        .filter { it.second.risk in 10..19 }
                        .toRiskRoasterRows()
                )}
                ${reportFooterSection(title = "Risk Roaster Report - $footerData")}
            </body>
            </html>
        """.trimIndent()
    }

    private fun getHighRiskPatientsSection(
        stats: List<List<String>>
    ): String {
        val table = buildTable(
            headers = listOf("Name", "Age", "Gender", "Risk %", "Mobile", "Address"),
            rows = stats
                .ifEmpty { listOf(listOf("--", "--", "--", "--", "--", "--")) },
            columnWeights = listOf("1.2fr", "0.8fr", "1fr", "0.8fr", "1.2fr", "2fr"),
                    tableClass = "left-last-col"
        )

        return buildSection(
            "High & Very High Risk (≥20%): ${stats.size}",
            table
        )
    }

    private fun getModerateRiskPatientsSection(
        stats: List<List<String>>
    ): String {
        val table = buildTable(
            headers = listOf("Name", "Age", "Gender", "Risk %", "Mobile", "Address"),
            rows = stats
                .ifEmpty { listOf(listOf("--", "--", "--", "--", "--", "--")) },
            columnWeights = listOf("1.2fr", "0.8fr", "1fr", "0.8fr", "1.2fr", "2fr"),
            tableClass = "left-last-col"
        )

        return buildSection(
            "Moderate Risk (10% to < 20%): ${stats.size}",
            table
        )
    }

    private fun List<Pair<PatientResponse, CVDResponse>>.toRiskRoasterRows(): List<List<String>> {
        return this
            .map { (patient, cvd) ->
                listOf(
                    "${patient.firstName} ${patient.lastName}",
                    "${patient.birthDate.toTimeInMilli().toAge()}",
                    patient.gender.capitalizeFirst(),
                    "${cvd.risk}%",
                    patient.mobileNumber ?: "--",
                    "${patient.permanentAddress.province}, " +
                            "${patient.permanentAddress.island}, " +
                            patient.permanentAddress.areaCouncil +
                            if (patient.permanentAddress.village.isNullOrBlank()) ""
                            else {
                                if (patient.permanentAddress.addressLine2.isNullOrBlank()) ", " + patient.permanentAddress.village
                                else ", " + patient.permanentAddress.addressLine2
                        }
                )
            }
    }
}