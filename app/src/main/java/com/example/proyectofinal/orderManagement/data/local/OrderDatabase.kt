package com.example.proyectofinal.orderManagement.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyectofinal.orderManagement.data.entity.OrderEntity

@Database(entities = [OrderEntity::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class OrderDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Recrear la tabla orders
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `orders` (
                `id` TEXT NOT NULL PRIMARY KEY,
                `title` TEXT NOT NULL,
                `studentId` TEXT NOT NULL,
                `content` TEXT NOT NULL
            )
        """)
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Eliminar la tabla vieja
        database.execSQL("DROP TABLE IF EXISTS `orders`")

        // Crear la nueva tabla con la columna isFavorite
        database.execSQL("""
            CREATE TABLE `orders` (
                `id` TEXT NOT NULL PRIMARY KEY,
                `title` TEXT NOT NULL,
                `studentId` TEXT NOT NULL,
                `status` TEXT NOT NULL,
                `isFavorite` INTEGER NOT NULL,
                `ordersJson` TEXT NOT NULL,
                `orderParagraphsJson` TEXT NOT NULL
            )
        """)
    }
}



