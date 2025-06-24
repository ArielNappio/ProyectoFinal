package com.example.proyectofinal.orderFeedback.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyectofinal.orderFeedback.data.entity.FeedbackEntity

@Database(
    entities = [FeedbackEntity::class],
    version = 5,
    exportSchema = false
)
abstract class FeedbackDatabase : RoomDatabase(){
    abstract fun feedbackDao(): FeedbackDao
}