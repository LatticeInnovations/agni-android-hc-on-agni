package com.heartcare.agni.utils.pdf.reports

import com.heartcare.agni.data.server.model.cvd.CVDResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.ui.sitescreendashboard.state.ReportUiState
import com.heartcare.agni.utils.converters.responseconverter.StringUtils.capitalizeFirst
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAge
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.buildTable
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportFooterSection
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportHeaderSection
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportPdfCss

object ClinicalActionReportPDF {

    fun getClinicalActionReportHTML(
        metaData: String,
        currentState: ReportUiState,
        footerData: String
    ): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
              <title>Clinical Action Report</title>
              <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
              ${reportPdfCss()}
            </head>
            <body>
                ${reportHeaderSection(title = "Clinical Action Report", metaData, currentState)}
                ${getTierOneInterventionSection(
                    stats = currentState.patientAndCVD
                        .filter { it.second.risk >= 20 }
                        .toClinicalActionRows()
                )}
                ${getTierTwoInterventionSection(
                    stats = currentState.patientAndCVD
                        .filter { it.second.risk in 10..19 }
                        .toClinicalActionRows()
                )}
                ${getTierThreeInterventionSection(
                    stats = currentState.patientAndCVD
                        .filter { it.second.risk < 10 }
                        .toClinicalActionRows()
                )}
                ${reportFooterSection(title = "Clinical Action Report - $footerData")}
            </body>
            </html>
        """.trimIndent()
    }

    private fun getTierOneInterventionSection(
        stats: List<List<String>>
    ): String {
        val description = """
            <div class="section-block">
                <div class="section-title">Tier 1: Urgent Intervention: ${stats.size}</div>
    
                <div class="sub-section-title-regular">CVD risk ≥20% – Requires immediate clinical follow-up and treatment initiation.</div>
    
                <ul style="margin-top: 8px; padding-left: 18px;">
                    <li>Clinical Action: Immediate initiation of intensive blood pressure-lowering medications and lipid-modifying therapy (statins).</li>
                    <li>Lifestyle Protocol: Aggressive counseling on tobacco cessation, heart-healthy dietary changes, and increased physical activity.</li>
                    <li>Monitoring: High-frequency clinical follow-ups to monitor treatment adherence and target attainment.</li>
                </ul>
            </div>
        """.trimIndent()

        val table = buildTable(
            headers = listOf("Name", "Age", "Gender", "Risk %"),
            rows = stats
                .ifEmpty { listOf(listOf("--", "--", "--", "--")) }
        )

        return """
            $description
            $table
        """.trimIndent()
    }

    private fun getTierTwoInterventionSection(
        stats: List<List<String>>
    ): String {
        val description = """
            <div class="section-block">
                <div class="section-title">Tier 2: Lifestyle Modification: ${stats.size}</div>
    
                <div class="sub-section-title-regular">
                    CVD risk 10–19% – Counseling on diet, exercise, smoking cessation.
                </div>
    
                <ul style="margin-top: 8px; padding-left: 18px;">
                    <li>Clinical Action: Primary focus is on non-pharmacological lifestyle modification.</li>
                    <li>Medication Trigger: Pharmacological treatment is secondary, to be considered if lifestyle changes fail to reduce risk factors over a 3–6 month window, or if BP remains ≥140/90 mmHg.</li>
                    <li>Core Advice: Specific emphasis on sodium reduction, weight management, and increasing physical activity.</li>
                </ul>
            </div>
        """.trimIndent()

        val table = buildTable(
            headers = listOf("Name", "Age", "Gender", "Risk %"),
            rows = stats
                .ifEmpty { listOf(listOf("--", "--", "--", "--")) }
        )

        return """
            $description
            $table
        """.trimIndent()
    }

    private fun getTierThreeInterventionSection(
        stats: List<List<String>>
    ): String {
        val description = """
            <div class="section-block">
                <div class="section-title">Tier 3: Primary Prevention: ${stats.size}</div>
    
                <div class="sub-section-title-regular">
                    CVD risk &lt;10% – Monitor and reinforce healthy behaviors.
                </div>
    
                <ul style="margin-top: 8px; padding-left: 18px;">
                    <li>Clinical Action: Routine pharmacological treatment is explicitly not recommended.</li>
                    <li>Maintenance Advice: Focus on maintaining current health to prevent risk progression. Guidelines include 150 minutes of moderate aerobic exercise weekly and a diet high in fiber and unrefined carbohydrates.</li>
                    <li>Follow-up: Automated system scheduling for risk re-assessment every 2 to 5 years.</li>
                </ul>
            </div>
        """.trimIndent()

        val table = buildTable(
            headers = listOf("Name", "Age", "Gender", "Risk %"),
            rows = stats
                .ifEmpty { listOf(listOf("--", "--", "--", "--")) }
        )

        return """
            $description
            $table
        """.trimIndent()
    }

    private fun List<Pair<PatientResponse, CVDResponse>>.toClinicalActionRows(): List<List<String>> {
        return this
            .map { (patient, cvd) ->
                listOf(
                    "${patient.firstName} ${patient.lastName}",
                    "${patient.birthDate.toTimeInMilli().toAge()}",
                    patient.gender.capitalizeFirst(),
                    "${cvd.risk}%"
                )
            }
    }
}