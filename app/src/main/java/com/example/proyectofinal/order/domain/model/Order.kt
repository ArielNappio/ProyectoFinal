package com.example.proyectofinal.order.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class Order(
    val id: Int,
    val name: String,
    val subject: String,
    val description: String,
    val authorName: String,
    val rangePage: String,
    val isPriority: Boolean,
    val state: String,
    val dateCreation: String,
    val limitDate: String,
    val createdByUserId : String,
    val filePath : String,
    val voluntarioId : String,
    val alumnoId : String,
    val revisorId : String,
    val delivererId : String
)