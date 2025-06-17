package com.example.proyectofinal.orderManagement.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val title: String,
//    val deliveryDate: String?,
    val studentId: String,
    val content: List<String>
)

