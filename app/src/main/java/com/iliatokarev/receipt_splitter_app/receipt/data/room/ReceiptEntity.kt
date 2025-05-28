package com.iliatokarev.receipt_splitter_app.receipt.data.room

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "receipt_data")
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
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "translated_name")
    val translatedName: String? = null,
    @ColumnInfo(name = "quantity")
    val quantity: Int,
    @ColumnInfo(name = "price")
    val price: Float,
    @ColumnInfo(name = "receipt_id")
    val receiptId: Long
)

data class ReceiptWithOrdersEntity(
    @Embedded val receipt: ReceiptEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "receipt_id"
    )
    val orders: List<OrderEntity>,
)