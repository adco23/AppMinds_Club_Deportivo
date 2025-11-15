package com.appminds.clubdeportivo.models

import java.io.Serializable

data class AddActividadDto(
    val name: String,
    val days: String,        // "Lunes, Miércoles y Viernes"
    val startTime: String,   // "15:00"
    val endTime: String,     // "16:00"
    val price: Double,       // no más String
    val capacity: Int        // no más String
) : Serializable