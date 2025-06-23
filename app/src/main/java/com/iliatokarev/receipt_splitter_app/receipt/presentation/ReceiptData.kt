package com.iliatokarev.receipt_splitter_app.receipt.presentation

import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReceiptDataJson(
    @SerialName("receipt_name")
    val receiptName: String = "no name",
    @SerialName("translated_receipt_name")
    val translatedReceiptName: String? = null,
    @SerialName("date")
    val date: String = "00/00/2025",
    @SerialName("orders")
    val orders: List<OrderDataJson> = emptyList<OrderDataJson>(),
    @SerialName("total_sum")
    val total: Float = 0.0F,
    @SerialName("tax_in_percent")
    val tax: Float? = null,
    @SerialName("discount_in_percent")
    val discount: Float? = null,
    @SerialName("tip_in_percent")
    val tip: Float? = null,
)

@Serializable
data class OrderDataJson(
    @SerialName("name")
    val name: String = "no name",
    @SerialName("translated_name")
    val translatedName: String? = null,
    @SerialName("quantity")
    val quantity: Int = 1,
    @SerialName("price")
    val price: Float = 0.0F,
)

data class ReceiptData(
    val id: Long,
    val receiptName: String = "no name",
    val translatedReceiptName: String? = null,
    val date: String = "no date",
    val total: Float = 0.0F,
    val tax: Float? = null,
    val discount: Float? = null,
    val tip: Float? = null,
    val additionalSumList: List<Pair<String, Float>> = emptyList(),
    val folderId: Long? = null,
    val isShared: Boolean = false,
    val isChecked: Boolean = false,
)

data class OrderData(
    val id: Long,
    val name: String = "no name",
    val translatedName: String? = null,
    val selectedQuantity: Int = 0,
    val quantity: Int = 1,
    val price: Float = 0f,
    val receiptId: Long,
    val consumersList: List<String> = emptyList(),
)

data class OrderDataSplit(
    val name: String = "no name",
    val translatedName: String? = null,
    val price: Float = 0f,
    val consumerNamesList: List<String> = emptyList(),
    val checked: Boolean = false,
    val orderDataId: Long,
)

data class FolderData(
    val id: Long,
    val folderName: String = "no name",
    val consumerNamesList: List<String> = emptyList(),
    val isArchived: Boolean = false,
)

class ReceiptWithOrdersDataSplit(
    val receipt: ReceiptData,
    val orders: List<OrderDataSplit>,
)

class ReceiptWithFolderData(
    val receipt: ReceiptData,
    val folderName: String? = null,
)

class FolderWithReceiptsData(
    val folder: FolderData,
    val amountOfReceipts: Int = 0,
)

@Serializable
class UserAttemptsData(
    @PropertyName("lastAttemptTime")
    val lastAttemptTime: Long = 0L,
    @PropertyName("attempts")
    val attempts: Int = 1,
)

@Serializable
class ReceiptVertexData(
    @PropertyName("deltaTimeBetweenAttempts")
    val deltaTimeBetweenAttempts: Long = 0L,
    @PropertyName("maximumAttemptsForUser")
    val maximumAttemptsForUser: Int = 0,
    @PropertyName("requestText")
    val requestText: String = "",
    @PropertyName("aiModel")
    val aiModel: String = "",
)

object ReceiptUIConstants {
    const val ONE_LINE = 1
    const val ONE_ELEMENT = 1
}