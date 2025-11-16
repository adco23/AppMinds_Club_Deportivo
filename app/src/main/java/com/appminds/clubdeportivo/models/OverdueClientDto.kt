package com.appminds.clubdeportivo.models

import java.io.Serializable

data class OverdueClientDto(
    val name: String,
    val dni: String,
    val dueDate: String,
    val status: String
): Serializable
