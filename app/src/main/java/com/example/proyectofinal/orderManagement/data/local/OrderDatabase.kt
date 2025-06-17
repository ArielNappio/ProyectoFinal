package com.example.proyectofinal.orderManagement.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.proyectofinal.orderManagement.data.entity.OrderEntity

@Database(entities = [OrderEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class OrderDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
}

