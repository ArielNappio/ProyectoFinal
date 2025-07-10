package com.example.proyectofinal.orderManagement.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val title: String,
//    val deliveryDate: String?,
    val studentId: String,
    val status: String,
    val isFavorite: Boolean,
    val ordersJson: String, // JSON string de List<OrderStudent>
    val orderParagraphsJson: String // JSON string de List<OrderParagraph>
)
