package com.example.receipt_splitter.receipt.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ReceiptEntity::class, OrderEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao
}