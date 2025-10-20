package com.appminds.clubdeportivo.data.model

data class UserEntity(
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val clave: String,
    val activo: Boolean
)