package com.appminds.clubdeportivo.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.appminds.clubdeportivo.data.db.DatabaseHelper
import com.appminds.clubdeportivo.data.db.contracts.ProfesorAttendanceContract
import com.appminds.clubdeportivo.data.db.contracts.ProfesorContract
import com.appminds.clubdeportivo.data.model.ProfesorAttendanceEntity
import com.appminds.clubdeportivo.data.model.ProfesorEntity
import com.appminds.clubdeportivo.models.enums.AttendanceStatus

class ProfesorDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insert(profesor: ProfesorEntity): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ProfesorContract.Columns.NOMBRE, profesor.firstname)
            put(ProfesorContract.Columns.APELLIDO, profesor.lastname)
            put(ProfesorContract.Columns.DNI, profesor.dni)
            put(ProfesorContract.Columns.TELEFONO, profesor.phone)
            put(ProfesorContract.Columns.DOMICILIO, profesor.address)
            put(ProfesorContract.Columns.ACTIVIDAD_ID, profesor.activity)
            put(ProfesorContract.Columns.ES_SUPLENTE, if(profesor.isSubstitute) 1 else 0 )
        }

        return db.insert(ProfesorContract.TABLE_NAME, null, values)
    }

    fun isNotEmpty(): Boolean {
        val db = dbHelper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.query(
                ProfesorContract.TABLE_NAME,
                arrayOf("COUNT(*)"),
                null,
                null,
                null,
                null,
                null,
                "1"
            )

            if (cursor.moveToFirst()) {
                val count = cursor.getInt(0)
                return count > 0
            }

            return false

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            cursor?.close()
            db.close()
        }

    }

    fun getAll(): List<ProfesorEntity> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            ProfesorContract.TABLE_NAME,
            null, null, null, null, null, null
        )

        val list = mutableListOf<ProfesorEntity>()
        with(cursor) {
            while (moveToNext()) {
                list.add( extractFromCursor(cursor) )
            }
        }

        cursor.close()
        return list
    }

    fun deleteById(profesorId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(
            ProfesorContract.TABLE_NAME,
            "${ProfesorContract.Columns.ID} = ?",
            arrayOf(profesorId.toString()))

        if (rowsDeleted > 0) {
            Log.d("DB_DELETE", "Registro eliminado correctamente.")
            return true
        } else {
            Log.e("DB_DELETE", "Error al eliminar registro con ID: $profesorId.")
            return false
        }
    }

    fun getByDNI(dni: String): ProfesorEntity? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            ProfesorContract.TABLE_NAME,
            null,
            "${ProfesorContract.Columns.DNI} LIKE ?",
            arrayOf("%$dni%"),
            null,
            null,
            null
        )

        var profesor: ProfesorEntity? = null
        if (cursor.moveToFirst()) { profesor = extractFromCursor(cursor) }

        cursor.close()
        db.close()
        return profesor
    }

    fun getByID(id: Int): ProfesorEntity {
        return dbHelper.readableDatabase.use { db ->
            val cursor = db.query(        ProfesorContract.TABLE_NAME,
                null,
                "${ProfesorContract.Columns.ID} LIKE ?",
                arrayOf(id.toString()),
                null,
                null,
                null
            )

            cursor.use {
                if (it.moveToFirst()) {
                    extractFromCursor(it)
                } else {
                    null
                }
            } as ProfesorEntity
        }
    }

    private fun extractFromCursor(cursor: Cursor): ProfesorEntity {
        return ProfesorEntity(
            cursor.getInt(cursor.getColumnIndexOrThrow(ProfesorContract.Columns.ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(ProfesorContract.Columns.NOMBRE)),
            cursor.getString(cursor.getColumnIndexOrThrow(ProfesorContract.Columns.APELLIDO)),
            cursor.getString(cursor.getColumnIndexOrThrow(ProfesorContract.Columns.DNI)),
            cursor.getString(cursor.getColumnIndexOrThrow(ProfesorContract.Columns.DOMICILIO)),
            cursor.getString(cursor.getColumnIndexOrThrow(ProfesorContract.Columns.TELEFONO)),
            cursor.getInt(cursor.getColumnIndexOrThrow(ProfesorContract.Columns.ES_SUPLENTE)) == 1,
            cursor.getInt(cursor.getColumnIndexOrThrow(ProfesorContract.Columns.ACTIVIDAD_ID))
        )
    }
}