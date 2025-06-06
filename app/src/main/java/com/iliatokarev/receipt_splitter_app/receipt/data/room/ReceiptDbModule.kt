package com.iliatokarev.receipt_splitter_app.receipt.data.room

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val receiptDbModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            ReceiptAppDatabase::class.java,
            "receipt_database"
        ).build()
    }

    single { get<ReceiptAppDatabase>().receiptDao() }

    factoryOf(::ReceiptAdapter) { bind<ReceiptAdapterInterface>() }
    factoryOf(::ReceiptDbRepository) { bind<ReceiptDbRepositoryInterface>() }
}