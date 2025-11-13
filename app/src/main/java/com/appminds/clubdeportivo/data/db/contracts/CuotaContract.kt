package com.appminds.clubdeportivo.data.db.contracts

object CuotaContract {

    const val TABLE_NAME = "cuota"

    object Columns {

        const val ID = "id"

        const val CLIENTE_ID = "cliente_id"

        const val FECHA_VENCIMIENTO = "fechaVencimiento"

        const val FECHA_PAGO = "fechaPago"

        const val MONTO = "monto"

        const val FORMA_PAGO = "formaPago"

        const val PROMOCION = "promocion"

        const val ESTADO = "estado"

    }

    const val CREATE_TABLE: String = """
        CREATE TABLE $TABLE_NAME (
            ${Columns.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${Columns.CLIENTE_ID} INTEGER NOT NULL,
            ${Columns.FECHA_VENCIMIENTO} INTEGER NOT NULL,
            ${Columns.FECHA_PAGO} INTEGER NOT NULL,
            ${Columns.MONTO} REAL NOT NULL,
            ${Columns.FORMA_PAGO} TEXT NOT NULL,
            ${Columns.PROMOCION} TEXT,
            ${Columns.ESTADO} TEXT NOT NULL DEFAULT 'pagada'
        );
    """
}