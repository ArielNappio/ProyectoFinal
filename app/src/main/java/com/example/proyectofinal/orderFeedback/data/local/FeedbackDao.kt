package com.example.proyectofinal.orderFeedback.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinal.orderFeedback.data.entity.FeedbackEntity

@Dao
interface FeedbackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackEntity)

    @Query("SELECT * FROM feedback WHERE orderId = :orderId LIMIT 1")
    suspend fun getFeedback(orderId: Int): FeedbackEntity?
}