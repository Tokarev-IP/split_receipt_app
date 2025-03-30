package com.example.receipt_splitter.main

import android.app.Application
import com.example.receipt_splitter.login.di.signInModule
import com.example.receipt_splitter.main.di.mainModule
import com.example.receipt_splitter.receipt.di.receiptModule
import com.example.receipt_splitter.receipt.room.receiptDbModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(mainModule, signInModule, receiptModule, receiptDbModule())
        }
    }

}