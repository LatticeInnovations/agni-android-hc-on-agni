package com.heartcare.agni.utils.pdf.reports

import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.buildTable
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportFooterSection
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportHeaderSection
import com.heartcare.agni.utils.pdf.reports.CommonPDFComponents.reportPdfCss

object ClinicalActionReportPDF {

    fun getClinicalActionReportHTML(): String {
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
                ${reportHeaderSection(title = "Clinical Action Report")}
                ${getTierOneInterventionSection()}
                ${getTierTwoInterventionSection()}
                ${getTierThreeInterventionSection()}
                ${reportFooterSection(title = "Clinical Action Report")}
            </body>
            </html>
        """.trimIndent()
    }

    private fun getTierOneInterventionSection(): String {
        val description = """
            <div class="section-block">
                <div class="section-title">Tier 1: Urgent Intervention: 6</div>
    
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
            rows = listOf(
                listOf("Sarah Naupa", "56", "Male", "34%"),
                listOf("James O’Hara", "29", "Male", "42%"),
                listOf("Mia Chen", "41", "Female", "28%"),
                listOf("Liam Rodriguez", "37", "Male", "50%"),
                listOf("Ava Patel", "33", "Female", "39%"),
                listOf("Ethan Kim", "45", "Male", "31%")
            )
        )

        return """
            $description
            $table
        """.trimIndent()
    }

    private fun getTierTwoInterventionSection(): String {
        val description = """
            <div class="section-block">
                <div class="section-title">Tier 2: Lifestyle Modification: 4</div>
    
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
            rows = listOf(
                listOf("Liam Ortega", "29", "Female", "14%"),
                listOf("Maya Chen", "41", "Female", "16%"),
                listOf("Olivia Reyes", "63", "Female", "18%"),
                listOf("Noah Kim", "22", "Male", "19%")
            )
        )

        return """
            $description
            $table
        """.trimIndent()
    }

    private fun getTierThreeInterventionSection(): String {
        val description = """
            <div class="section-block">
                <div class="section-title">Tier 3: Primary Prevention: 4</div>
    
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
            rows = listOf(
                listOf("Liam Ortega", "29", "Female", "4%"),
                listOf("Maya Chen", "41", "Female", "2%"),
                listOf("Olivia Reyes", "63", "Female", "3%"),
                listOf("Noah Kim", "22", "Male", "1%")
            )
        )

        return """
            $description
            $table
        """.trimIndent()
    }
}