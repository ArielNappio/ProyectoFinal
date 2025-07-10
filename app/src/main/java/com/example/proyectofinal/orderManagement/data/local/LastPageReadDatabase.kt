package com.example.proyectofinal.orderManagement.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyectofinal.orderManagement.data.entity.LastReadPageEntity

@Database(
    entities = [LastReadPageEntity::class],
    version = 4,
    exportSchema = false
)
abstract class LastPageReadDatabase: RoomDatabase() {
    abstract fun lastPageReadDao(): LastReadPageDao
}