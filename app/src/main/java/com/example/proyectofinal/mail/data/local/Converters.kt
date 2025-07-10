package com.example.proyectofinal.mail.data.local

import androidx.room.TypeConverter
import java.util.Date

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    @JvmStatic
    fun toDate(timeStamp: Long?): Date? {
        return timeStamp?.let { Date(it) }
    }
}
