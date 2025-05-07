package com.example.receipt_splitter.receipt.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    //INSERT&UPDATE
    @Upsert
    suspend fun insertReceipt(receipt: ReceiptEntity): Long

    @Upsert
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Upsert
    suspend fun insertOrder(order: OrderEntity): Long

    //GET
//    @Transaction
//    @Query("SELECT * FROM receipt_data")
//    fun getAllReceiptsWithOrdersFlow(): Flow<List<ReceiptWithOrdersEntity>>

//    @Transaction
//    @Query("SELECT * FROM receipt_data WHERE id = :receiptId")
//    fun getReceiptWithOrdersByIdFlow(receiptId: Long): Flow<ReceiptWithOrdersEntity?>

    @Transaction
    @Query("SELECT * FROM receipt_data")
    fun getAllReceiptsFlow(): Flow<List<ReceiptEntity>>

    @Transaction
    @Query("SELECT * FROM receipt_data WHERE id = :receiptId")
    fun getReceiptByIdFlow(receiptId: Long): Flow<ReceiptEntity?>

    @Transaction
    @Query("SELECT * FROM receipt_data WHERE id = :receiptId")
    suspend fun getReceiptById(receiptId: Long): ReceiptEntity?

    @Transaction
    @Query("SELECT * FROM order_data WHERE receipt_id = :receiptId")
    fun getOrdersByReceiptIdFlow(receiptId: Long): Flow<List<OrderEntity>>

    @Transaction
    @Query("SELECT * FROM order_data WHERE receipt_id = :receiptId")
    suspend fun getOrdersByReceiptId(receiptId: Long): List<OrderEntity>

//    @Transaction
//    @Query("SELECT * FROM receipt_data WHERE id = :receiptId")
//    suspend fun getReceiptWithOrdersById(receiptId: Long): ReceiptWithOrdersEntity?

    //DELETE
    @Query("DELETE FROM receipt_data WHERE id = :receiptId")
    suspend fun deleteReceipt(receiptId: Long)

    @Query("DELETE FROM order_data WHERE id = :orderId")
    suspend fun deleteOrder(orderId: Long)
}