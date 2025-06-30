package com.iliatokarev.receipt_splitter_app.receipt.data.room

import androidx.room.Room
import com.iliatokarev.receipt_splitter_app.receipt.data.room.folder.FolderAdapter
import com.iliatokarev.receipt_splitter_app.receipt.data.room.folder.FolderAdapterInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.room.folder.FolderDbRepository
import com.iliatokarev.receipt_splitter_app.receipt.data.room.folder.FolderDbRepositoryInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt.ReceiptAdapter
import com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt.ReceiptAdapterInterface
import com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt.ReceiptDbRepository
import com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt.ReceiptDbRepositoryInterface
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val roomDbModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            ReceiptSplitterDatabase::class.java,
            "receipt_database"
        ).build()
    }

    single { get<ReceiptSplitterDatabase>().receiptDao() }
    single { get<ReceiptSplitterDatabase>().folderDao() }


    factoryOf(::ReceiptAdapter) { bind<ReceiptAdapterInterface>() }
    factoryOf(::ReceiptDbRepository) { bind<ReceiptDbRepositoryInterface>() }

    factoryOf(::FolderAdapter) { bind<FolderAdapterInterface>() }
    factoryOf(::FolderDbRepository) { bind<FolderDbRepositoryInterface>() }
}