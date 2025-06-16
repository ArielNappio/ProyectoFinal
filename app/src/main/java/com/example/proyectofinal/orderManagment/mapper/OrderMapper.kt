package com.example.proyectofinal.orderManagment.mapper

import com.example.proyectofinal.orderManagment.data.dto.OrderDeliveredDto
import com.example.proyectofinal.orderManagment.data.dto.OrderDto
import com.example.proyectofinal.orderManagment.data.dto.OrderParagraphDto
import com.example.proyectofinal.orderManagment.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagment.domain.model.OrderParagraph
import com.example.proyectofinal.orderManagment.domain.model.OrderStudent

fun OrderDeliveredDto.toDomain(): OrderDelivered = OrderDelivered(
    studentId = studentId,
    id = id,
    status = status,
    title = title,
    orders = orders.map { it.toDomain() },
    orderParagraphs = orderParagraphs.map { it.toDomain() }
)


fun OrderDto.toDomain(): OrderStudent {
    return OrderStudent(
        id = id,
        name = name,
        description = description,
        isFavorite = false, // o cargar desde otro lado
        lastRead = "0",
        pageCount = rangePage?.split("-")?.let {
            if (it.size == 2) it[1].toInt() - it[0].toInt() + 1 else 0
        } ?: 0,
        hasComments = false, // o lógica adicional
        subject = subject,
        authorName = authorName,
        rangePage = rangePage,
        status = status,
        voluntarioId = voluntarioId,
        alumnoId = alumnoId
    )
}

fun OrderParagraphDto.toDomain(): OrderParagraph = OrderParagraph(
    orderId = orderId,
    paragraphText = paragraphText,
    pageNumber = pageNumber
)

