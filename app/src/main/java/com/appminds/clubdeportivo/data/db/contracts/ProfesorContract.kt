package com.appminds.clubdeportivo.data.db.contracts

object ProfesorContract {
    const val TABLE_NAME: String = "profesor"

    object Columns {
        const val ID = "id"
        const val NOMBRE = "nombre"
        const val APELLIDO = "apellido"
        const val DNI = "dni"
        const val TELEFONO = "telefono"
        const val DOMICILIO = "domicilio"
        const val ES_SUPLENTE = "es_suplente"
    }

    const val CREATE_TABLE: String = """
        CREATE TABLE $TABLE_NAME (
            ${Columns.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${Columns.NOMBRE} TEXT NOT NULL,
            ${Columns.APELLIDO} TEXT NOT NULL,
            ${Columns.DNI} TEXT NOT NULL UNIQUE,
            ${Columns.TELEFONO} TEXT NOT NULL,
            ${Columns.DOMICILIO} TEXT NOT NULL,
            ${Columns.ES_SUPLENTE} INT NOT NULL DEFAULT 0
        );
    """
}