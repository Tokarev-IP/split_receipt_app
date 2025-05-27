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
    @ColumnInfo(name = "translated_restaurant_name")
    val translatedRestaurant: String? = null,
    @ColumnInfo(name = "date")
    val date: String = "",
    @ColumnInfo(name = "total_sum")
    val total: Float = 0.0F,
    @ColumnInfo(name = "additional_tax_in_percent")
    val tax: Float? = null,
    @ColumnInfo(name = "total_discount_in_percent")
    val discount: Float? = null,
    @ColumnInfo(name = "total_tip_in_percent")
    val tip: Float? = null,
    @ColumnInfo(name = "total_tip_sum")
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