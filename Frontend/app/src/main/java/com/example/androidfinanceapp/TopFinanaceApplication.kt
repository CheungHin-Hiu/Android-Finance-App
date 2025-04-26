package com.example.androidfinanceapp

import android.app.Application
import com.example.androidfinanceapp.data.AppContainer
import com.example.androidfinanceapp.data.DefaultAppContainer

class TopFinanceApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}