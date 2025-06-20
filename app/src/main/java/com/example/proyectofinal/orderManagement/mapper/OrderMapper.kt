package com.example.proyectofinal.orderManagement.mapper

import com.example.proyectofinal.orderManagement.data.dto.OrderDeliveredDto
import com.example.proyectofinal.orderManagement.data.dto.OrderDto
import com.example.proyectofinal.orderManagement.data.dto.OrderParagraphDto
import com.example.proyectofinal.orderManagement.data.entity.OrderEntity
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagement.domain.model.OrderParagraph
import com.example.proyectofinal.orderManagement.domain.model.OrderStudent
import kotlinx.serialization.json.Json

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


fun OrderDelivered.toEntity(studentId: String): OrderEntity {
    return OrderMapper.toEntity(this)
}

fun OrderEntity.toTaskGroup(): OrderDelivered {
    return OrderMapper.toDomain(this)
}



object OrderMapper {
    
    private val json = Json { 
        ignoreUnknownKeys = true 
        encodeDefaults = true
    }
    
    fun toEntity(orderDelivered: OrderDelivered): OrderEntity {
        return OrderEntity(
            id = orderDelivered.id,
            title = orderDelivered.title,
            studentId = orderDelivered.studentId,
            status = orderDelivered.status,
            isFavorite = orderDelivered.isFavorite,
            ordersJson = json.encodeToString(orderDelivered.orders),
            orderParagraphsJson = json.encodeToString(orderDelivered.orderParagraphs)
        )
    }
    
    fun toDomain(orderEntity: OrderEntity): OrderDelivered {
        return OrderDelivered(
            id = orderEntity.id,
            title = orderEntity.title,
            studentId = orderEntity.studentId,
            isFavorite = orderEntity.isFavorite,
            status = orderEntity.status,
            orders = json.decodeFromString<List<OrderStudent>>(orderEntity.ordersJson),
            orderParagraphs = json.decodeFromString<List<OrderParagraph>>(orderEntity.orderParagraphsJson)
        )
    }
    
    fun toDomainList(entities: List<OrderEntity>): List<OrderDelivered> {
        return entities.map { toDomain(it) }
    }
    
    fun toEntityList(orders: List<OrderDelivered>): List<OrderEntity> {
        return orders.map { toEntity(it) }
    }
}
