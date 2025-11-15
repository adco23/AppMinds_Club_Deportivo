package com.appminds.clubdeportivo.data.db.contracts

object PagoActividadContract {

    const val TABLE_NAME = "pagoactividad"

    object Columns {
        const val ID = "id"
        const val ID_ACTIVIDAD = "idActividad"
        const val ID_CLIENTE = "idCliente"
        const val FECHA_PAGO = "fechaPago"
        const val FORMA_PAGO = "formaPago"
    }

    const val CREATE_TABLE: String = """
        CREATE TABLE $TABLE_NAME (
            ${Columns.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${Columns.ID_ACTIVIDAD} INTEGER NOT NULL,
            ${Columns.ID_CLIENTE} INTEGER NOT NULL,
            ${Columns.FECHA_PAGO} INTEGER NOT NULL,
            ${Columns.FORMA_PAGO} TEXT NOT NULL
        );
    """
}