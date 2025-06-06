package com.iliatokarev.receipt_splitter_app.receipt.domain.usecases

import com.iliatokarev.receipt_splitter_app.main.basic.BasicFunResponse
import com.iliatokarev.receipt_splitter_app.receipt.data.room.ReceiptDbRepositoryInterface
import com.iliatokarev.receipt_splitter_app.receipt.presentation.OrderData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptUiMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext

class EditReceiptUseCase(
    private val receiptDbRepositoryInterface: ReceiptDbRepositoryInterface
) : EditReceiptUseCaseInterface {

    override suspend fun getReceiptDataFlow(receiptId: Long): Flow<ReceiptData?> {
        return receiptDbRepositoryInterface.getReceiptDataByIdFlow(id = receiptId)
            .catch { emit(null) }
    }

    override suspend fun getOrderDataListFlow(receiptId: Long): Flow<List<OrderData>> {
        return receiptDbRepositoryInterface.getOrdersByReceiptIdFlow(receiptId = receiptId)
            .catch { emit(emptyList()) }
    }

    override suspend fun deleteReceiptData(receiptId: Long): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                receiptDbRepositoryInterface.deleteReceiptData(receiptId = receiptId)
            }
        }.fold(
            onSuccess = { BasicFunResponse.Success },
            onFailure = { e ->
                BasicFunResponse.Error(e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg)
            }
        )
    }

    override suspend fun deleteOrderDataById(id: Long): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                receiptDbRepositoryInterface.deleteOrderDataById(id = id)
            }
        }.fold(
            onSuccess = { BasicFunResponse.Success },
            onFailure = { e ->
                BasicFunResponse.Error(e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg)
            }
        )
    }

    override suspend fun upsertReceiptData(receiptData: ReceiptData): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                receiptDbRepositoryInterface.insertReceiptData(receiptData = receiptData)
            }
        }.fold(
            onSuccess = { BasicFunResponse.Success },
            onFailure = { e ->
                BasicFunResponse.Error(e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg)
            }
        )
    }

    override suspend fun upsertOrderData(orderData: OrderData): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                receiptDbRepositoryInterface.insertOrderData(orderData = orderData)
            }
        }.fold(
            onSuccess = { BasicFunResponse.Success },
            onFailure = { e ->
                BasicFunResponse.Error(e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg)
            }
        )
    }

    override suspend fun insertNewOrderData(orderData: OrderData): BasicFunResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                receiptDbRepositoryInterface.insertNewOrderData(orderData = orderData)
            }
        }.fold(
            onSuccess = { BasicFunResponse.Success },
            onFailure = { e ->
                BasicFunResponse.Error(e.message ?: ReceiptUiMessage.INTERNAL_ERROR.msg)
            }
        )
    }
}

interface EditReceiptUseCaseInterface {
    suspend fun getReceiptDataFlow(receiptId: Long): Flow<ReceiptData?>
    suspend fun getOrderDataListFlow(receiptId: Long): Flow<List<OrderData>>
    suspend fun deleteReceiptData(receiptId: Long): BasicFunResponse
    suspend fun deleteOrderDataById(id: Long): BasicFunResponse
    suspend fun upsertReceiptData(receiptData: ReceiptData): BasicFunResponse
    suspend fun upsertOrderData(orderData: OrderData): BasicFunResponse
    suspend fun insertNewOrderData(orderData: OrderData): BasicFunResponse
}