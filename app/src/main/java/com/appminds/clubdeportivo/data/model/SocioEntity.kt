package com.appminds.clubdeportivo.data.model

data class SocioEntity(
    val id: Int = 0,
    val clienteId: Int,
    val vencimiento: String,
    val activo: Boolean
)