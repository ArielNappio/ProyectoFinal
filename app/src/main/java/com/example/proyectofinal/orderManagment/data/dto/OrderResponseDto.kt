package com.example.proyectofinal.orderManagment.data.dto

@kotlinx.serialization.Serializable
data class OrderResponseDto(
    val order: OrderDto,
    val paragraphTexts: List<String>
)

@kotlinx.serialization.Serializable
data class OrderDto(
    val id: Int,
    val name: String,
    val subject: String,
    val description: String,
    val authorName: String?,
    val rangePage: String?,
    val isPriority: Boolean,
    val status: String?,
    val creationDate: String,
    val limitDate: String?,
    val createdByUserId: Int?,
    val filePath: String?,
    val voluntarioId: Int?,
    val alumnoId: Int?,
    val revisorId: Int?,
    val delivererId: Int?
)
