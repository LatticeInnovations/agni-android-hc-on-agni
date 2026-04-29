package com.heartcare.agni.utils.pdf.reports

import com.heartcare.agni.ui.sitescreendashboard.state.ReportUiState
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toMMMMddyyyyHHmm
import java.util.Date

object CommonPDFComponents {
    fun reportPdfCss(): String {
        return """
        <style>
            @page {
                size: A4;
                margin: 20px 6px;
            }
        
            body {
              font-family: 'Roboto', sans-serif;
              background-color: #ffffff;
              margin: 0;
              padding: 20px 40px;
            }
            
            .center {
              text-align: center;
            }
        
            .title {
              font-size: 32px;
              font-weight: 400;
              line-height: 40px;
              letter-spacing: 0;
              margin-bottom: 10px;
              color: #1C1B1F;
            }
        
            .subtitle {
              font-size: 14px;
              font-weight: 400;
              line-height: 20px;
              letter-spacing: 0.25px;
              margin: 4px 0;
              color: #49454F;
            }
        
            .divider {
              height: 2px;
              background-color: #6750A4;
              margin: 12px 0px;
            }
        
            .section-block {
              page-break-inside: avoid;
              break-inside: avoid;
            }
        
            .section-title {
              font-size: 16px;
              font-weight: 500;
              line-height: 24px;
              letter-spacing: 0.15px;
              margin-top: 20px;
              margin-bottom: 6px;
              color: #1C1B1F;
            }
        
            .sub-section-title {
              font-size: 14px;
              font-weight: 500;
              margin: 10px 12px;
              color: #1C1B1F;
            }
        
            .sub-section-title-regular {
              font-size: 14px;
              font-weight: 400;
              color: #1C1B1F;
            }
        
            .card {
              background-color: #F7F2F9;
              border-radius: 10px;
              padding: 20px;
            }
        
            .primary-count {
              font-family: 'Inter', sans-serif;
              font-size: 24px;
              font-weight: 700;
              line-height: 32px;
              color: #6750A4;
            }
        
            .primary-count span {
              font-family: 'Inter', sans-serif;
              font-size: 18px;
              font-weight: 400;
              line-height: 28px;
              color: #1C1B1F;
              margin-left: 4px;
            }
        
            .secondary-label {
              margin-top: 6px;
              font-family: 'Inter', sans-serif;
              font-size: 16px;
              font-weight: 400;
              line-height: 24px;
              color: #1C1B1F;
            }
        
            .secondary-label span {
              font-weight: 600;
              margin-right: 12px;
            }
        
            .table-container {
              border-radius: 10px;
              overflow: hidden;
              border: 1px solid #ddd;
            }
        
            .table-header {
              background-color: #6750A4;
              color: white;
              display: flex;
              justify-content: space-between;
              padding: 14px 16px;
              font-weight: 500;
              font-size: 16px;
            }
        
            .table-row {
              display: flex;
              justify-content: space-between;
              padding: 14px 16px;
              font-size: 16px;
              border-top: 1px solid #e0e0e0;
            }
        
            .table-header,
            .table-row {
              display: grid;
              align-items: center;
            }
            
            .table-header span,
            .table-row span {
              text-align: center;
            }
            
            .table-header span:first-child,
            .table-row span:first-child {
              text-align: left;
            }
            
            .table-header span:last-child,
            .table-row span:last-child {
              text-align: right;
            }
            
            .table-container.left-last-col .table-header span:last-child,
            .table-container.left-last-col .table-row span:last-child {
              text-align: left;
            }
        
            .highlight {
              color: #6750A4;
              font-weight: 500;
            }
        
            .footer-divider {
              height: 1px;
              background-color: #E7E0EC;
              margin: 30px 0 15px;
            }
        
            .footer {
              text-align: center;
              font-family: 'Inter', sans-serif;
              font-size: 14px;
              line-height: 20px;
              font-weight: 400;
              color: #79747E;
            }
            
            ul {
              margin-left: 6px;
            }
            
            ul li {
              font-weight: 400;
              font-size: 14px;
              line-height: 20px;
              letter-spacing: 0.25px;
              color: #49454F;
            }
          </style>
        """.trimIndent()
    }

    fun reportHeaderSection(
        title: String,
        metaData: String,
        currentState: ReportUiState
    ): String {
        return """
          <div class="center">
            <div class="title">$title</div>
            <div class="subtitle">$metaData</div>
          </div>
          <div class="divider"></div>
        
          <div class="section-title">Total Screened</div>
        
          <div class="card">
            <div class="primary-count">
              ${currentState.totalScreened} <span>participants</span>
            </div>
        
            <div class="secondary-label">
              Male: <span>${currentState.totalMale}</span>
              Female: <span>${currentState.totalFemale}</span>
              Other: <span>${currentState.totalOther}</span>
            </div>
          </div>
        """.trimIndent()
    }

    fun buildTable(
        headers: List<String>,
        rows: List<List<String>>,
        columnWeights: List<String> = List(headers.size) { "1fr" },
        highlightColumns: Set<Int> = emptySet(),
        tableClass: String = ""
    ): String {

        val gridTemplate = columnWeights.joinToString(" ")

        val headerHtml = """
            <div class="section-block">
                <div class="table-header" style="grid-template-columns: $gridTemplate;">
                    ${headers.joinToString("") { "<span>$it</span>" }}
                </div>
            </div>
        """

        val rowsHtml = rows.joinToString("") { row ->
            """
                <div class="table-row" style="grid-template-columns: $gridTemplate;">
                    ${
                        row.mapIndexed { index, cell ->
                            val highlightClass = if (index in highlightColumns) "highlight" else ""
                            "<span class=\"$highlightClass\">$cell</span>"
                        }.joinToString("")
                    }
                </div>
            """
        }

        return """
            <div class="table-container $tableClass">
                $headerHtml
                $rowsHtml
            </div>
        """
    }

    fun buildSection(
        title: String,
        content: String
    ): String {
        return """
            <div class="section-block">
                <div class="section-title">$title</div>
                $content
            </div>
        """
    }

    fun reportFooterSection(
        title: String
    ): String {
        return """ 
          <div class="section-block">
              <div class="footer-divider"></div>
            
              <div class="footer">
                <div>$title</div>
                <div>Generated on ${Date().toMMMMddyyyyHHmm()}</div>
              </div>
          </div>
        """.trimIndent()
    }
}