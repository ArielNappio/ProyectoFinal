package com.example.proyectofinal.mail.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyectofinal.mail.data.entity.MessageEntity

@Database(entities = [MessageEntity::class], version = 1)
abstract class MailDatabase : RoomDatabase(){
    abstract fun messageDao(): MessageDao
}