package com.heartcare.agni.utils.pdf

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient

object PDFHelper {
    fun generatePdf(context: Context, html: String, fileName: String) {

        val webView = WebView(context)

        webView.settings.javaScriptEnabled = false

        webView.loadDataWithBaseURL(
            null,
            html,
            "text/HTML",
            "UTF-8",
            null
        )

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {

                val printManager =
                    context.getSystemService(Context.PRINT_SERVICE) as PrintManager

                val printAdapter =
                    webView.createPrintDocumentAdapter(fileName)

                val printAttributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()

                printManager.print(
                    fileName,
                    printAdapter,
                    printAttributes
                )
            }
        }
    }
}