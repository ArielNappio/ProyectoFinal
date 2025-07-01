package com.example.proyectofinal.student.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun formatHumanReadableDate(rawDate: String): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    return try {
        val commentDate = formatter.parse(rawDate)
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        val commentCal = Calendar.getInstance().apply { time = commentDate!! }

        return when {
            isSameDay(commentCal, today) -> "hoy"
            isSameDay(commentCal, yesterday) -> "ayer"
            else -> rawDate
        }
    } catch (e: Exception) {
        rawDate // por si hay error, mostramos igual la fecha cruda
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}