package com.example.proyectofinal.orderManagment.data.dto

@kotlinx.serialization.Serializable
data class OrderDeliveredDto(
    val studentId: String,
    val id: Int,
    val status: String,
    val title: String,
    val orders: List<OrderDto>,
    val orderParagraphs: List<OrderParagraphDto>,
)

@kotlinx.serialization.Serializable
data class OrderDto(
    val id: Int,
    val name: String,
    val subject: String,
    val description: String,
    val authorName: String,
    val rangePage: String,
    val status: String,
    val voluntarioId: String?,
    val alumnoId: String,
)

@kotlinx.serialization.Serializable
data class OrderParagraphDto(
    val orderId: Int,
    val paragraphText: String,
    val pageNumber: Int,
)
