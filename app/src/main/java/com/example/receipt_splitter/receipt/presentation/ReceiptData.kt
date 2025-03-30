package com.example.receipt_splitter.receipt.presentation

import android.net.Uri
import com.example.receipt_splitter.main.basic.BasicIntent
import com.example.receipt_splitter.main.basic.BasicUiErrorIntent
import com.example.receipt_splitter.main.basic.BasicUiEvent
import com.example.receipt_splitter.main.basic.BasicUiState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReceiptData(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("restaurant_name")
    val restaurant: String = "no name",
    @SerialName("date_dd_mm-yyyy")
    val date: String = "no date",
    @SerialName("orders")
    val orders: List<OrderData> = emptyList<OrderData>(),
    @SerialName("sub_total_sum")
    val subTotal: Float? = null,
    @SerialName("total_sum")
    val total: Float? = null,
    @SerialName("tax_in_percent")
    val tax: Float? = null,
    @SerialName("discount_in_percent")
    val discount: Float? = null,
    @SerialName("tip_in_percent")
    val tip: Float? = null,
    @SerialName("tip_sum")
    val tipSum: Float? = null,
)

fun ReceiptData.toSplitReceiptDataList(): List<SplitOrderData> {
    return this.orders.map {
        SplitOrderData(
            name = it.name,
            quantity = it.quantity,
            price = it.price,
        )
    }
}

@Serializable
class OrderData(
    @SerialName("name")
    val name: String,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("price")
    val price: Float,
)

data class SplitOrderData(
    val name: String,
    val selectedQuantity: Int = 0,
    val quantity: Int,
    val price: Float,
)

sealed interface ReceiptNavHostDestinations {
    @Serializable
    object ChoosePhotoScreenNav : ReceiptNavHostDestinations

    @Serializable
    object ShowReceiptsScreenNav : ReceiptNavHostDestinations

    @Serializable
    object SplitReceiptScreenNav : ReceiptNavHostDestinations
}

interface ReceiptUiState : BasicUiState {
    object Loading : ReceiptUiState
    object Show : ReceiptUiState
}

sealed interface ReceiptUiEvent : BasicUiEvent {
    class ConvertReceiptFromImage(val imageUri: Uri) : ReceiptUiEvent
    class AddQuantityToSplitOrderData(val orderName: String): ReceiptUiEvent
    class SubtractQuantityToSplitOrderData(val orderName: String): ReceiptUiEvent
    object AddNewReceipt : ReceiptUiEvent
    class ReceiptDeletion(val receiptId: Long) : ReceiptUiEvent
    object RetrieveAllReceipts : ReceiptUiEvent
    class OpenSplitReceiptScreen(val receiptData: ReceiptData) : ReceiptUiEvent
    object SetShowState : ReceiptUiEvent
}

interface ReceiptIntent : BasicIntent {
    object GoToSplitReceiptScreen : ReceiptIntent
    object GoToChoosePhotoScreen : ReceiptIntent
    object GoToShowReceiptsScreen : ReceiptIntent
}

interface ReceiptUiErrorIntent : BasicUiErrorIntent {
    object ImageIsInappropriate : ReceiptUiErrorIntent
    object JsonError : ReceiptUiErrorIntent
    class ReceiptError(val msg: String) : ReceiptUiErrorIntent
}