package com.example.proyectofinal.orderFeedback.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedback")
data class FeedbackEntity(
    @PrimaryKey val orderId: Int,
    val wasSent: Boolean
)
