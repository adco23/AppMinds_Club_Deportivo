package com.appminds.clubdeportivo.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.appminds.clubdeportivo.data.db.DatabaseHelper
import com.appminds.clubdeportivo.data.db.contracts.ProfesorContract
import com.appminds.clubdeportivo.data.model.ProfesorEntity

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
                list.add(
                    ProfesorEntity(
                        getInt(getColumnIndexOrThrow(ProfesorContract.Columns.ID)),
                        getString(getColumnIndexOrThrow(ProfesorContract.Columns.NOMBRE)),
                        getString(getColumnIndexOrThrow(ProfesorContract.Columns.APELLIDO)),
                        getString(getColumnIndexOrThrow(ProfesorContract.Columns.DNI)),
                        getString(getColumnIndexOrThrow(ProfesorContract.Columns.DOMICILIO)),
                        getString(getColumnIndexOrThrow(ProfesorContract.Columns.TELEFONO)),
                        getInt(getColumnIndexOrThrow(ProfesorContract.Columns.ES_SUPLENTE)) == 1,
                    )
                )
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
            Log.e("DB_DELETE", "Erro al eliminar registro con ID: $profesorId.")
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
        if (cursor.moveToFirst()) {
            profesor = ProfesorEntity(
                cursor.getInt(cursor.getColumnIndexOrThrow(ProfesorContract.Columns.ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(ProfesorContract.Columns.NOMBRE)),
                cursor.getString(cursor.getColumnIndexOrThrow(ProfesorContract.Columns.APELLIDO)),
                cursor.getString(cursor.getColumnIndexOrThrow(ProfesorContract.Columns.DNI)),
                cursor.getString(cursor.getColumnIndexOrThrow(ProfesorContract.Columns.DOMICILIO)),
                cursor.getString(cursor.getColumnIndexOrThrow(ProfesorContract.Columns.TELEFONO)),
                cursor.getInt(cursor.getColumnIndexOrThrow(ProfesorContract.Columns.ES_SUPLENTE)) == 1,
            )
        }

        cursor.close()
        db.close()
        return profesor
    }
}