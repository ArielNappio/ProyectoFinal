package com.example.proyectofinal.orderManagment.mapper

import com.example.proyectofinal.orderManagment.data.dto.OrderResponseDto
import com.example.proyectofinal.student.domain.model.Task

fun OrderResponseDto.toTask(): Task {
    return Task(
        id = order.id,
        name = order.name,
        description = order.description,
        isFavorite = false, // default o cargado desde otro lado
        lastRead = order.creationDate,
        pageCount = order.rangePage?.split("-")?.let {
            if (it.size == 2) it[1].toInt() - it[0].toInt() + 1 else 0
        } ?: 0,
        hasComments = false, // según otra lógica o API
        paragraphs = paragraphTexts
    )
}
