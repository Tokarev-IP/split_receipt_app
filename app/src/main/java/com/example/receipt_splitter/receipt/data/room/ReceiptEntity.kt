package com.example.receipt_splitter.receipt.data.room

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
    @ColumnInfo(name = "restaurant_name")
    val restaurant: String = "",
    @ColumnInfo(name = "date")
    val date: String = "",
    @ColumnInfo(name = "sub_total_sum")
    val subTotal: Float? = null,
    @ColumnInfo(name = "total_sum")
    val total: Float? = null,
    @ColumnInfo(name = "tax_in_percent")
    val tax: Float? = null,
    @ColumnInfo(name = "discount_in_percent")
    val discount: Float? = null,
    @ColumnInfo(name = "tip_in_percent")
    val tip: Float? = null,
    @ColumnInfo(name = "tip_sum")
    val tipSum: Float? = null
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
    @ColumnInfo(name = "quantity")
    val quantity: Int,
    @ColumnInfo(name = "price")
    val price: Float,
    @ColumnInfo(name = "receipt_id")
    val receiptId: Long
)

data class ReceiptWithOrders(
    @Embedded val receipt: ReceiptEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "receipt_id"
    )
    val orders: List<OrderEntity>,
)