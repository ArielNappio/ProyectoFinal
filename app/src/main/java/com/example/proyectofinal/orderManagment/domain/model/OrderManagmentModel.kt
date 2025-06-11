package com.example.proyectofinal.orderManagment.domain.model

import com.example.proyectofinal.order.domain.model.Order

data class OrderManagmentModel(
    val order: Order,
    val paragraphTexts: List<String>,
)