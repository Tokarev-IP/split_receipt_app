package com.iliatokarev.receipt_splitter_app.receipt.data.room

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "folder_data")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "folder_name")
    val folderName: String,
    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,
    @ColumnInfo(name = "consumer_names")
    val consumerNames: String = "",
)

@Entity(
    tableName = "receipt_data",
    foreignKeys = [ForeignKey(
        entity = FolderEntity::class,
        parentColumns = ["id"],
        childColumns = ["folder_id"],
        onDelete = ForeignKey.SET_NULL,
        )],
    indices = [Index(value = ["folder_id"])]
)
data class ReceiptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "receipt_name")
    val receiptName: String = "no name",
    @ColumnInfo(name = "translated_receipt_name")
    val translatedReceiptName: String? = null,
    @ColumnInfo(name = "date")
    val date: String = "",
    @ColumnInfo(name = "total_sum")
    val total: Float = 0.0F,
    @ColumnInfo(name = "tax_in_percent")
    val tax: Float? = null,
    @ColumnInfo(name = "discount_in_percent")
    val discount: Float? = null,
    @ColumnInfo(name = "tip_in_percent")
    val tip: Float? = null,
    @ColumnInfo(name = "folder_id")
    val folderId: Long? = null,
    @ColumnInfo(name = "is_shared")
    val isShared: Boolean = false,
)

@Entity(
    tableName = "order_data",
    foreignKeys = [ForeignKey(
        entity = ReceiptEntity::class,
        parentColumns = ["id"],
        childColumns = ["receipt_id"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index(value = ["receipt_id"])]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "order_name")
    val orderName: String,
    @ColumnInfo(name = "translated_name")
    val translatedName: String? = null,
    @ColumnInfo(name = "quantity")
    val quantity: Int,
    @ColumnInfo(name = "price")
    val price: Float,
    @ColumnInfo(name = "receipt_id")
    val receiptId: Long,
    @ColumnInfo(name = "consumer_names")
    val consumerNames: String = "",
)

data class ReceiptWithOrdersEntity(
    @Embedded val receipt: ReceiptEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "receipt_id"
    )
    val orders: List<OrderEntity>,
)

data class ReceiptWithFolderEntity(
    @Embedded val receipt: ReceiptEntity,
    @Relation(
        parentColumn = "folder_id",
        entityColumn = "id"
    )
    val folder: FolderEntity? = null,
)

data class FolderWithReceiptsEntity(
    @Embedded val folder: FolderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "folder_id"
    )
    val receipts: List<ReceiptEntity>,
)