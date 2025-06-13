package com.iliatokarev.receipt_splitter_app.receipt.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt.OrderEntity
import com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt.ReceiptDao
import com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt.ReceiptEntity

@Database(
    entities = [ReceiptEntity::class, OrderEntity::class],
    version = 1
)
abstract class ReceiptSplitterDatabase : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao
}