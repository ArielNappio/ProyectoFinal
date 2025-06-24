package com.example.proyectofinal.orderManagement.data.repository

import com.example.proyectofinal.orderManagement.data.entity.LastReadPageEntity
import com.example.proyectofinal.orderManagement.data.local.LastReadPageDao

class LastReadRepository(private val dao: LastReadPageDao) {

    suspend fun getLastReadPage(orderId: Int): Int {
        return dao.getLastRead(orderId)?.page ?: 0
    }

    suspend fun saveLastReadPage(orderId: Int, page: Int) {
        dao.saveLastRead(LastReadPageEntity(orderId, page))
    }
}