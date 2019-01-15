package com.rel.csam.lab

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class App: Application() {

    companion object {
        lateinit var prefs : SharedPreferences
    }

    override fun onCreate() {
        prefs = getSharedPreferences("image", Context.MODE_PRIVATE)
        super.onCreate()
    }
}