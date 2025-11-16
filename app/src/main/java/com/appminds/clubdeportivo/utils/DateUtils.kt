package com.appminds.clubdeportivo.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//Convierte un Timestamp (Long en milisegundos) a un String de fecha legible.

fun formatTimestampToDateString(timestamp: Long, format: String = "dd/MM/yyyy"): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(date)
}

fun convertDateToTimestamp(dateString: String): Long? {
    // Formato día/mes/año estándar
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return try {
        dateFormat.parse(dateString)?.time
    } catch (e: Exception) {
        null
    }
}