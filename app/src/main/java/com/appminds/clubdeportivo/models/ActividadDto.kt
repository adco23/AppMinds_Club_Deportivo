package com.appminds.clubdeportivo.models

import java.io.Serializable
data class ActividadDto(
    val name: String,
    val days: String,
    val time: String,
    val price: String,
    val capacity: String
) : Serializable
