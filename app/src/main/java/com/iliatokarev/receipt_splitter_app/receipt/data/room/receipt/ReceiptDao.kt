package com.iliatokarev.receipt_splitter_app.receipt.data.room.receipt

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.iliatokarev.receipt_splitter_app.receipt.data.room.FolderWithReceiptsEntity
import com.iliatokarev.receipt_splitter_app.receipt.data.room.OrderEntity
import com.iliatokarev.receipt_splitter_app.receipt.data.room.ReceiptEntity
import com.iliatokarev.receipt_splitter_app.receipt.data.room.ReceiptWithFolderEntity
import com.iliatokarev.receipt_splitter_app.receipt.data.room.ReceiptWithOrdersEntity
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

    @Transaction
    @Query("SELECT * FROM receipt_data")
    fun getAllReceiptsFlow(): Flow<List<ReceiptEntity>>

    @Transaction
    @Query("SELECT * FROM receipt_data WHERE id = :receiptId")
    fun getReceiptByIdFlow(receiptId: Long): Flow<ReceiptEntity?>

    @Transaction
    @Query("SELECT * FROM receipt_data WHERE folder_id = :folderId")
    fun getReceiptsByFolderIdFlow(folderId: Long): Flow<List<ReceiptEntity>>

    @Transaction
    @Query("SELECT * FROM receipt_data WHERE id = :receiptId")
    suspend fun getReceiptById(receiptId: Long): ReceiptEntity?

    @Transaction
    @Query("SELECT * FROM order_data WHERE receipt_id = :receiptId")
    fun getOrdersByReceiptIdFlow(receiptId: Long): Flow<List<OrderEntity>>

    @Transaction
    @Query("SELECT * FROM order_data WHERE receipt_id = :receiptId")
    suspend fun getOrdersByReceiptId(receiptId: Long): List<OrderEntity>

    @Transaction
    @Query("SELECT * FROM receipt_data WHERE id = :receiptId")
    suspend fun getReceiptWithOrdersById(receiptId: Long): ReceiptWithOrdersEntity?

    @Transaction
    @Query("SELECT * FROM receipt_data")
    fun getReceiptsWithFolderFlow(): Flow<List<ReceiptWithFolderEntity>>

    @Transaction
    @Query("SELECT * FROM folder_data")
    fun getFoldersWithReceiptsFlow(): Flow<List<FolderWithReceiptsEntity>>

    //DELETE
    @Query("DELETE FROM receipt_data WHERE id = :receiptId")
    suspend fun deleteReceipt(receiptId: Long)

    @Query("DELETE FROM order_data WHERE id = :orderId")
    suspend fun deleteOrder(orderId: Long)
}