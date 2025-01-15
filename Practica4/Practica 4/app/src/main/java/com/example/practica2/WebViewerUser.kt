package com.example.practica2

import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewerUser: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webvieweruser)

        // Obtener la referencia al WebView
        val webView: WebView = findViewById(R.id.webvieweruser)

        // Configuración de WebView
        webView.settings.javaScriptEnabled = true // Habilitar JavaScript si es necesario

        // Definir el WebViewClient para abrir las páginas dentro de la app
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                // Permitir que los enlaces se abran en el mismo WebView
                view?.loadUrl(request?.url.toString())
                return true
            }
        }

        // Cargar la URL de la máquina local del emulador
        webView.loadUrl("http://10.0.2.2:3000")
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        // Si hay historial en el WebView, permitir la navegación hacia atrás
        val webView: WebView = findViewById(R.id.webvieweruser)
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}