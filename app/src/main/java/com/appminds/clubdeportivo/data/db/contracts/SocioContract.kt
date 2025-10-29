package com.appminds.clubdeportivo.data.db.contracts

object SocioContract {
    const val TABLE_NAME = "socio"

    object Columns {
        const val ID = "id"
        const val CLIENTE_ID = "cliente_id"
        const val VENCIMIENTO = "vencimiento"
        const val ACTIVO = "activo"
    }

    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            ${Columns.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${Columns.CLIENTE_ID} INTEGER NOT NULL,
            ${Columns.VENCIMIENTO} TEXT NOT NULL,
            ${Columns.ACTIVO} INTEGER NOT NULL DEFAULT 1
            CONSTRAINT fk_cliente_id
                FOREIGN KEY (${Columns.CLIENTE_ID})
                REFERENCES ${ClientContract.TABLE_NAME}(${ClientContract.Columns.ID})}
        )
    """
}