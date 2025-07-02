package com.example.proyectofinal.mail.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.proyectofinal.mail.data.entity.MessageEntity

@Database(entities = [MessageEntity::class], version = 10)
@TypeConverters(Converters::class)
abstract class MailDatabase : RoomDatabase(){
    abstract fun messageDao(): MessageDao
}