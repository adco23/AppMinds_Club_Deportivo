package com.appminds.clubdeportivo.models

import java.io.Serializable

data class OverdueClientDto(
    val id: Int? = 0,
    val name: String,
    val dni: String,
    val dueDate: String,
    val status: String
): Serializable
