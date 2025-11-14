package com.appminds.clubdeportivo.data.db.contracts

object ProfesorAttendanceContract {
    const val TABLE_NAME: String = "profesor_asistencia"

    object Columns {
        const val ID = "id"
        const val ID_PROFESOR = "id_profesor"
        const val FECHA = "fecha"
        const val ESTADO = "estado"
    }

    const val CREATE_TABLE: String = """
        CREATE TABLE $TABLE_NAME (
            ${Columns.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${Columns.ID_PROFESOR} INTEGER NOT NULL,
            ${Columns.FECHA} TEXT NOT NULL,
            ${Columns.ESTADO} TEXT NOT NULL,
            UNIQUE(${Columns.ID_PROFESOR}, ${Columns.FECHA}),
            FOREIGN KEY(${Columns.ID_PROFESOR}) REFERENCES profesor(${Columns.ID}) ON DELETE CASCADE
        );
    """
}