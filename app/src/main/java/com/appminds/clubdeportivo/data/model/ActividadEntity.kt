package com.appminds.clubdeportivo.data.model
import java.io.Serializable

data class ActividadEntity(
    var id: Int? = null,
    val name: String,
    val days: String,        // "Lun, Mié y Vie"
    val startTime: String,   // "15:00"
    val endTime: String,     // "16:00"
    val price: Double,
    val capacity: Int
) : Serializable