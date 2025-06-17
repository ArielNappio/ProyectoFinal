package com.example.proyectofinal.orderManagement.mapper

import com.example.proyectofinal.orderManagement.data.dto.OrderDeliveredDto
import com.example.proyectofinal.orderManagement.data.dto.OrderDto
import com.example.proyectofinal.orderManagement.data.dto.OrderParagraphDto
import com.example.proyectofinal.orderManagement.data.entity.OrderEntity
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagement.domain.model.OrderParagraph
import com.example.proyectofinal.orderManagement.domain.model.OrderStudent

fun OrderDeliveredDto.toDomain(): OrderDelivered = OrderDelivered(
    studentId = studentId,
    id = id.toString(),
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
        pageCount = rangePage.split("-").let {
            if (it.size == 2) it[1].toInt() - it[0].toInt() + 1 else 0
        },
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


fun OrderDelivered.toEntity(studentId: String) : OrderEntity{
    return OrderEntity(
        id = title,
        title = title,
        studentId = studentId,
        content = TODO()
    )
}

fun OrderEntity.toTaskGroup() : OrderDelivered {
    return OrderDelivered(
        studentId = studentId,
        id = id,
        status = TODO(),
        title = title,
        orders = TODO(),
        orderParagraphs = TODO()
    )
}

