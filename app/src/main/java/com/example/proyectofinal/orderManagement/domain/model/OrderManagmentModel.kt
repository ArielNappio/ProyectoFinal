package com.example.proyectofinal.orderManagement.domain.model

data class OrderDelivered(
    val studentId: String,
    val id: String,
    val status: String,
    val title: String,
    val orders: List<OrderStudent>,
    val orderParagraphs: List<OrderParagraph>,
)

data class OrderStudent(
    val id: Int,
    val name: String,
    val description: String,
    val isFavorite: Boolean = false,
    val lastRead: String = "0",
    val pageCount: Int = 0,
    val hasComments: Boolean = false,
    val subject: String,
    val authorName: String?,
    val rangePage: String?,
    val status: String?,
    val voluntarioId: String?,
    val alumnoId: String?
)

data class OrderParagraph(
    val orderId: Int,
    val paragraphText: String,
    val pageNumber: Int,
)