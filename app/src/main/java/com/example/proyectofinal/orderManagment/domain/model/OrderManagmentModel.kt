package com.example.proyectofinal.orderManagment.domain.model


import com.example.proyectofinal.order.domain.model.Order
import com.example.proyectofinal.student.domain.model.Task

data class OrderManagmentModel(
    val order: Order,
    val paragraphTexts: List<String>,
)

data class TaskGroup(
    val title: String,
    val tasks: List<Task>
)