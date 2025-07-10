package com.example.proyectofinal.mail.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtilsMail {
    fun formatHumanReadableDate(rawDate: String): String {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val legacyFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val alternateFormat = SimpleDateFormat("EEE dd MMM yyyy HH:mm", Locale.ENGLISH)
        val fullDateTimeFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val normalizedDate = rawDate.replaceFirstChar { it.uppercaseChar() }

        val parsedDate = listOf(isoFormat, legacyFormat, alternateFormat).firstNotNullOfOrNull { format ->
            try {
                format.parse(normalizedDate)
            } catch (_: Exception) {
                null
            }
        } ?: return rawDate

        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        val dateCalendar = Calendar.getInstance().apply { time = parsedDate }

        return when {
            isSameDay(dateCalendar, today) -> "hoy a las ${timeFormat.format(parsedDate)}"
            isSameDay(dateCalendar, yesterday) -> "ayer a las ${timeFormat.format(parsedDate)}"
            else -> fullDateTimeFormat.format(parsedDate)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}