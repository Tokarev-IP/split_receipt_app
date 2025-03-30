package com.example.receipt_splitter.receipt.room

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

fun receiptDbModule() = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "receipt_database"
        ).build()
    }

    single { get<AppDatabase>().receiptDao() }

    factoryOf(::ReceiptAdapter) { bind<ReceiptAdapterInterface>() }
    factoryOf(::ReceiptDbRepository) { bind<ReceiptDbRepositoryInterface>() }
}