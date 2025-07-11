package com.example.proyectofinal.orderManagement.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinal.orderManagement.data.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTaskGroup(orders: List<OrderEntity>)

    @Query("SELECT * FROM orders WHERE studentId = :studentId")
    fun getTasksByStudent(studentId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE studentId = :studentId AND id = :orderId")
    fun getTaskById(studentId: String, orderId: String): Flow<OrderEntity>

    @Query("UPDATE orders SET isFavorite = :isFavorite WHERE id = :orderId")
    suspend fun updateFavoriteStatus(orderId: String, isFavorite: Boolean)

    @Query("SELECT * FROM orders WHERE studentId = :studentId AND id = :orderId LIMIT 1")
    suspend fun getTaskByIdOnce(studentId: String, orderId: String): OrderEntity?

}
