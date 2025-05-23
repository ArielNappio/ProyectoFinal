package com.example.proyectofinal.student.data.model

data class Task(
    val id: Int,
    val name: String,
    val description: String,
    val isFavorite: Boolean,
    val lastRead: String,
    val pageCount: Int,
    val hasComments: Boolean
)