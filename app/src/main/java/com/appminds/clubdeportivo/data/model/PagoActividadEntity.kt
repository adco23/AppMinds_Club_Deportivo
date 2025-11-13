package com.appminds.clubdeportivo.data.model

data class PagoActividadEntity(
    // El ID se autogenerará en la BD
    val id: Int = 0,

    // Clave Foránea a la tabla de actividades
    val idActividad: Int,

    // Clave Foránea a la tabla de clientes
    val idCliente: Int,

    // Fecha del pago (timestamp)
    val fechaPago: Long,

    // Forma en que se realizó el pago
    val formaPago: String
)