package com.appminds.clubdeportivo.data.model

data class CuotaEntity (

    val id: Int = 0,
    val clienteId: Int,
    val fechaVencimiento: Long,
    val fechaPago: Long,
    val monto: Double,
    val formaPago: String,
    val promocion: String,
    val estado: String = "pagada"
)