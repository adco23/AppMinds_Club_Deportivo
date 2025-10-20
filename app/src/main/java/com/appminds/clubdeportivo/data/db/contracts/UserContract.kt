package com.appminds.clubdeportivo.data.db.contracts

object UserContract {
    const val TABLE_NAME = "usuario"

    object Columns {
        const val ID = "id"
        const val NOMBRE = "nombre"
        const val CORREO = "correo"
        const val CLAVE = "clave"
        const val ACTIVO = "activo"
    }

    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            ${Columns.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${Columns.NOMBRE} TEXT NOT NULL,
            ${Columns.CORREO} TEXT NOT NULL,
            ${Columns.CLAVE} TEXT NOT NULL,
            ${Columns.ACTIVO} INTEGER NOT NULL DEFAULT 1
        )
    """
}