package com.appminds.clubdeportivo.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.appminds.clubdeportivo.data.db.DatabaseHelper
import com.appminds.clubdeportivo.data.db.contracts.ActividadContract
import com.appminds.clubdeportivo.data.db.contracts.ProfesorAttendanceContract
import com.appminds.clubdeportivo.data.model.ActividadEntity

class ActividadDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insert(actividad: ActividadEntity): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(ActividadContract.Columns.NOMBRE, actividad.name)
            put(ActividadContract.Columns.DIAS, actividad.days)
            put(ActividadContract.Columns.HORA_INICIO, actividad.startTime)
            put(ActividadContract.Columns.HORA_FIN, actividad.endTime)
            put(ActividadContract.Columns.PRECIO, actividad.price)
            put(ActividadContract.Columns.CUPO, actividad.capacity)
        }

        val id = db.insert(ActividadContract.TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun getAll(): List<ActividadEntity> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            ActividadContract.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${ActividadContract.Columns.NOMBRE} ASC"
        )

        val list = mutableListOf<ActividadEntity>()
        with(cursor) {
            while (moveToNext()) {
                list.add(
                    ActividadEntity(
                        id = getInt(getColumnIndexOrThrow(ActividadContract.Columns.ID)),
                        name = getString(getColumnIndexOrThrow(ActividadContract.Columns.NOMBRE)),
                        days = getString(getColumnIndexOrThrow(ActividadContract.Columns.DIAS)),
                        startTime = getString(getColumnIndexOrThrow(ActividadContract.Columns.HORA_INICIO)),
                        endTime = getString(getColumnIndexOrThrow(ActividadContract.Columns.HORA_FIN)),
                        price = getDouble(getColumnIndexOrThrow(ActividadContract.Columns.PRECIO)),
                        capacity = getInt(getColumnIndexOrThrow(ActividadContract.Columns.CUPO))
                    )
                )
            }
        }

        cursor.close()
        db.close()
        return list
    }

    fun existById(id: Int?): Boolean {
        return dbHelper.readableDatabase.use { db ->
            val cursor = db.query(
                ActividadContract.TABLE_NAME,
                arrayOf(ActividadContract.Columns.ID),
                "${ProfesorAttendanceContract.Columns.ID} = ? ",
                arrayOf(id.toString()),
                null,
                null,
                null
            )

            cursor.use { it.count > 0 }
        }
    }

    fun getById(id: Int?): ActividadEntity? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            ActividadContract.TABLE_NAME,
            null,
            "${ActividadContract.Columns.ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var actividad: ActividadEntity? = null
        if (cursor.moveToFirst()) {
            actividad = ActividadEntity(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ActividadContract.Columns.ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(ActividadContract.Columns.NOMBRE)),
                days = cursor.getString(cursor.getColumnIndexOrThrow(ActividadContract.Columns.DIAS)),
                startTime = cursor.getString(cursor.getColumnIndexOrThrow(ActividadContract.Columns.HORA_INICIO)),
                endTime = cursor.getString(cursor.getColumnIndexOrThrow(ActividadContract.Columns.HORA_FIN)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(ActividadContract.Columns.PRECIO)),
                capacity = cursor.getInt(cursor.getColumnIndexOrThrow(ActividadContract.Columns.CUPO))
            )
        }

        cursor.close()
        db.close()
        return actividad
    }

    fun update(actividad: ActividadEntity): Int {
        requireNotNull(actividad.id) { "Actividad.id no puede ser null para actualizar" }

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ActividadContract.Columns.NOMBRE, actividad.name)
            put(ActividadContract.Columns.DIAS, actividad.days)
            put(ActividadContract.Columns.HORA_INICIO, actividad.startTime)
            put(ActividadContract.Columns.HORA_FIN, actividad.endTime)
            put(ActividadContract.Columns.PRECIO, actividad.price)
            put(ActividadContract.Columns.CUPO, actividad.capacity)
        }

        val rowsUpdated = db.update(
            ActividadContract.TABLE_NAME,
            values,
            "${ActividadContract.Columns.ID} = ?",
            arrayOf(actividad.id.toString())
        )

        db.close()
        return rowsUpdated
    }

    fun deleteById(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(
            ActividadContract.TABLE_NAME,
            "${ActividadContract.Columns.ID} = ?",
            arrayOf(id.toString())
        )
        db.close()

        return if (rowsDeleted > 0) {
            Log.d("DB_DELETE", "Actividad eliminada correctamente (id=$id).")
            true
        } else {
            Log.e("DB_DELETE", "Error al eliminar actividad con ID: $id.")
            false
        }
    }

    fun getByName(name: String): ActividadEntity? {
        val db = dbHelper.readableDatabase
        var actividad: ActividadEntity? = null

        val cursor = db.query(
            ActividadContract.TABLE_NAME,
            null,
            "${ActividadContract.Columns.NOMBRE} = ?",
            arrayOf(name.trim()),
            null,
            null,
            null,
            "1" // sólo la primera coincidencia
        )

        if (cursor.moveToFirst()) {
            actividad = ActividadEntity(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ActividadContract.Columns.ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(ActividadContract.Columns.NOMBRE)),
                days = cursor.getString(cursor.getColumnIndexOrThrow(ActividadContract.Columns.DIAS)),
                startTime = cursor.getString(cursor.getColumnIndexOrThrow(ActividadContract.Columns.HORA_INICIO)),
                endTime = cursor.getString(cursor.getColumnIndexOrThrow(ActividadContract.Columns.HORA_FIN)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(ActividadContract.Columns.PRECIO)),
                capacity = cursor.getInt(cursor.getColumnIndexOrThrow(ActividadContract.Columns.CUPO))
            )
        }

        cursor.close()
        db.close()
        return actividad
    }
}
