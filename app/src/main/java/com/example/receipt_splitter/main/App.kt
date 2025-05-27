package com.example.receipt_splitter.main

import android.app.Application
import com.example.receipt_splitter.login.di.signInModule
import com.example.receipt_splitter.main.di.mainModule
import com.example.receipt_splitter.receipt.di.receiptModule
import com.example.receipt_splitter.receipt.data.room.receiptDbModule
import com.example.receipt_splitter.settings.settingsModule
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(mainModule, signInModule, receiptModule, receiptDbModule, settingsModule)
        }

        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
        )

//        Firebase.initialize(context = this)
//        Firebase.appCheck.installAppCheckProviderFactory(
//            PlayIntegrityAppCheckProviderFactory.getInstance(),
//        )
    }

}