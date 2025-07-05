package com.iliatokarev.receipt_splitter_app.receipt.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iliatokarev.receipt_splitter_app.receipt.data.room.folder.FolderDao
import com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt.ReceiptDao

@Database(
    entities = [ReceiptEntity::class, OrderEntity::class, FolderEntity::class],
    version = 2
)
abstract class ReceiptSplitterDatabase : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao
    abstract fun folderDao(): FolderDao
}