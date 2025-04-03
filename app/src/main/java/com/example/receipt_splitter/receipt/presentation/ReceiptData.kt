package com.example.receipt_splitter.receipt.presentation

import android.net.Uri
import com.example.receipt_splitter.main.basic.BasicIntent
import com.example.receipt_splitter.main.basic.BasicUiErrorIntent
import com.example.receipt_splitter.main.basic.BasicUiEvent
import com.example.receipt_splitter.main.basic.BasicUiState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ReceiptDataJson(
    @SerialName("restaurant_name")
    val restaurant: String = "no name",
    @SerialName("date_dd_mm-yyyy")
    val date: String = "no date",
    @SerialName("orders")
    val orders: List<OrderDataJson> = emptyList<OrderDataJson>(),
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

@Serializable
class OrderDataJson(
    @SerialName("name")
    val name: String,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("price")
    val price: Float,
)

data class SplitReceiptData(
    val id: Long,
    val restaurant: String,
    val date: String,
    val orders: List<SplitOrderData>,
    val subTotal: Float? = null,
    val total: Float?,
    val tax: Float? = null,
    val discount: Float? = null,
    val tip: Float? = null,
    val tipSum: Float? = null,
)

data class SplitOrderData(
    val id: Long,
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
    class ConvertImagesToReceipt(val listOfImages: List<Uri>) : ReceiptUiEvent
    class AddQuantityToSplitOrderData(val orderId: Long) : ReceiptUiEvent
    class SubtractQuantityToSplitOrderData(val orderId: Long) : ReceiptUiEvent
    object AddNewReceipt : ReceiptUiEvent
    class ReceiptDeletion(val receiptId: Long) : ReceiptUiEvent
    object RetrieveAllReceipts : ReceiptUiEvent
    class OpenSplitReceiptScreen(val splitReceiptData: SplitReceiptData) : ReceiptUiEvent
    object SetShowState : ReceiptUiEvent
}

interface ReceiptIntent : BasicIntent {
    object GoToSplitReceiptScreen : ReceiptIntent
    object GoToChoosePhotoScreen : ReceiptIntent
    object GoToShowReceiptsScreen : ReceiptIntent
}

interface ReceiptUiErrorIntent : BasicUiErrorIntent {
    object ImageIsInappropriate : ReceiptUiErrorIntent
    class ReceiptError(val msg: String) : ReceiptUiErrorIntent
}