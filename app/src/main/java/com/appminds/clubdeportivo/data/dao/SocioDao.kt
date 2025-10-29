package com.appminds.clubdeportivo.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.appminds.clubdeportivo.data.db.DatabaseHelper
import com.appminds.clubdeportivo.data.db.contracts.SocioContract
import com.appminds.clubdeportivo.data.model.SocioEntity

class SocioDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insert(socio: SocioEntity): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(SocioContract.Columns.CLIENTE_ID, socio.clienteId)
            put(SocioContract.Columns.VENCIMIENTO, socio.vencimiento)
            put(SocioContract.Columns.ACTIVO, if (socio.activo) 1 else 0)
        }

        return db.insert(SocioContract.TABLE_NAME, null, values)
    }

    fun getAll(): List<SocioEntity> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            SocioContract.TABLE_NAME,
            null, null, null, null, null, null
        )

        val list = mutableListOf<SocioEntity>()
        with(cursor) {
            while (moveToNext()) {
                list.add(
                    SocioEntity(
                        id = getInt(getColumnIndexOrThrow(SocioContract.Columns.ID)),
                        clienteId = getInt(getColumnIndexOrThrow(SocioContract.Columns.CLIENTE_ID)),
                        vencimiento = getString(getColumnIndexOrThrow(SocioContract.Columns.VENCIMIENTO)),
                        activo = getInt(getColumnIndexOrThrow(SocioContract.Columns.ACTIVO)) == 1
                    )
                )
            }
        }
        cursor.close()
        return list
    }
}