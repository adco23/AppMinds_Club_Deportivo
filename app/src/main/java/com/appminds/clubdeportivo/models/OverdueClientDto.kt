package com.appminds.clubdeportivo.models

import java.io.Serializable
import java.sql.Date

data class OverdueClientDto(
    val name: String,
    val dni: String,
    val dueDate: Long,
    val status: String
): Serializable
