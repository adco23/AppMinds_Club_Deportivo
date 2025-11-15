package com.appminds.clubdeportivo.data.db.contracts

object ActividadContract {
    const val TABLE_NAME: String = "actividad"

    object Columns {
        const val ID = "id"
        const val NOMBRE = "nombre"
        const val DIAS = "dias"                // varios días: "Lun, Mié y Vie"
        const val HORA_INICIO = "hora_inicio"  // HH:mm
        const val HORA_FIN = "hora_fin"        // HH:mm
        const val PRECIO = "precio"           // REAL
        const val CUPO = "cupo"               // INTEGER
    }

    // TEXT para días y horas, REAL para precio, INTEGER para cupo
    const val CREATE_TABLE: String = """
        CREATE TABLE $TABLE_NAME (
            ${Columns.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${Columns.NOMBRE} TEXT NOT NULL,
            ${Columns.DIAS} TEXT NOT NULL,
            ${Columns.HORA_INICIO} TEXT NOT NULL,
            ${Columns.HORA_FIN} TEXT NOT NULL,
            ${Columns.PRECIO} REAL NOT NULL,
            ${Columns.CUPO} INTEGER NOT NULL
        );
    """
}
