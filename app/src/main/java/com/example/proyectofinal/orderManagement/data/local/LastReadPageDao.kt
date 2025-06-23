package com.example.proyectofinal.orderManagement.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinal.orderManagement.data.entity.LastReadPageEntity

@Dao
interface LastReadPageDao {

    @Query("SELECT * FROM last_read_pages WHERE orderId = :orderId LIMIT 1")
    suspend fun getLastRead(orderId: Int): LastReadPageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLastRead(page: LastReadPageEntity)
}