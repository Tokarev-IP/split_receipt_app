package com.iliatokarev.receipt_splitter_app.main

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.initialize
import com.iliatokarev.receipt_splitter_app.login.di.signInModule
import com.iliatokarev.receipt_splitter_app.main.di.mainModule
import com.iliatokarev.receipt_splitter_app.receipt.data.room.roomDbModule
import com.iliatokarev.receipt_splitter_app.receipt.di.receiptSplitterModule
import com.iliatokarev.receipt_splitter_app.settings.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(mainModule, signInModule, receiptSplitterModule, roomDbModule, settingsModule)
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