package com.example.proyectofinal.order.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Int,
    val name: String,
    val description: String,
    val state: String,
    val dateCreation: String,
    val dateLimit: String,
    val userId: Int
)