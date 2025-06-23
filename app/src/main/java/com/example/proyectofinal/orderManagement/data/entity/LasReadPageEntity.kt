package com.example.proyectofinal.orderManagement.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_read_pages")
data class LastReadPageEntity(
    @PrimaryKey val orderId: Int, // ID del OrderStudent
    val page: Int
)