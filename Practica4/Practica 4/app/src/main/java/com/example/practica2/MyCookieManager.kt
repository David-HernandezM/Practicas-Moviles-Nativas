package com.example.practica2

import android.app.Application
import java.net.CookieHandler
import java.net.CookieManager

class MyCookieManager : Application() {
    override fun onCreate() {
        super.onCreate()
        val cookieManager = CookieManager()
        CookieHandler.setDefault(cookieManager)
    }
}

