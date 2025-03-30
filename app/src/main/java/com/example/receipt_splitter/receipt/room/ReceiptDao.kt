package com.example.receipt_splitter.receipt.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: ReceiptEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Transaction
    @Query("SELECT * FROM receipt_data")
    fun getAllReceiptsWithOrders(): Flow<List<ReceiptWithOrders>>

    @Query("DELETE FROM receipt_data WHERE id = :receiptId")
    suspend fun deleteReceipt(receiptId: Long)
}