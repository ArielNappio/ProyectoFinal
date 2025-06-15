package com.example.proyectofinal.orderManagment.mapper

import com.example.proyectofinal.orderManagment.data.dto.OrderDeliveredDto
import com.example.proyectofinal.orderManagment.data.dto.OrderDto
import com.example.proyectofinal.orderManagment.domain.model.TaskGroup
import com.example.proyectofinal.student.domain.model.Task

fun OrderDto.toTask(): Task {
    return Task(
        id = id,
        name = name,
        description = description,
        isFavorite = false, // default o cargado desde otro lado
        lastRead = creationDate,
        pageCount = rangePage?.split("-")?.let {
            if (it.size == 2) it[1].toInt() - it[0].toInt() + 1 else 0
        } ?: 0,
        hasComments = false, // según otra lógica o API
    )
}

fun OrderDeliveredDto.toTaskGroup(): TaskGroup {
    return TaskGroup(
        title = this.title,
        tasks = this.orders.map { it.toTask() }
    )
}
