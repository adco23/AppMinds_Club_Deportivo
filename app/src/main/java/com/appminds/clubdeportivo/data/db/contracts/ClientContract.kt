package com.appminds.clubdeportivo.data.db.contracts

object ClientContract {
    const val TABLE_NAME = "cliente"

    object Columns {
        const val ID = "id"
        const val NOMBRE = "nombre"
        const val APELLIDO = "apellido"
        const val DNI = "dni"
        const val CORREO = "correo"
        const val TELEFONO = "telefono"
        const val DOMICILIO = "domicilio"
        const val FECHA_ALTA = "fecha_alta"
        const val FECHA_VENCIMIENTO = "fecha_vencimiento"
        const val APTO_FISICO = "apto_fisico"
        const val TIPO_CLIENTE = "tipo_cliente"
        const val ESTADO = "estado"
    }

    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            ${Columns.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${Columns.NOMBRE} TEXT NOT NULL,
            ${Columns.APELLIDO} TEXT NOT NULL,
            ${Columns.DNI} TEXT NOT NULL UNIQUE,
            ${Columns.CORREO} TEXT NOT NULL,
            ${Columns.TELEFONO} TEXT NOT NULL,
            ${Columns.DOMICILIO} TEXT NOT NULL,
            ${Columns.FECHA_ALTA} TEXT NOT NULL,
            ${Columns.FECHA_VENCIMIENTO} INTEGER NOT NULL DEFAULT 0,
            ${Columns.APTO_FISICO} INTEGER NOT NULL DEFAULT 0,
            ${Columns.TIPO_CLIENTE} TEXT NOT NULL,
            ${Columns.ESTADO} TEXT NOT NULL
        );
    """
}