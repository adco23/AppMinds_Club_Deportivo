package com.appminds.clubdeportivo.data.dao

import android.R
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.appminds.clubdeportivo.data.db.DatabaseHelper
import com.appminds.clubdeportivo.data.db.contracts.UserContract
import com.appminds.clubdeportivo.data.model.UserEntity

/**
 * DAO para la tabla de Usuarios
 */
class UserDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insert(user: UserEntity): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UserContract.Columns.NOMBRE, user.nombre)
            put(UserContract.Columns.CORREO, user.correo)
            put(UserContract.Columns.CLAVE, user.clave)
            put(UserContract.Columns.ACTIVO, if (user.activo) 1 else 0)
        }

        return db.insert(UserContract.TABLE_NAME, null, values)
    }

    fun getAll(): List<UserEntity> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            UserContract.TABLE_NAME,
            null, null, null, null, null, null
        )

        val list = mutableListOf<UserEntity>()
        with(cursor) {
            while (moveToNext()) {
                list.add(
                    UserEntity(
                        id = getInt(getColumnIndexOrThrow(UserContract.Columns.ID)),
                        nombre = getString(getColumnIndexOrThrow(UserContract.Columns.NOMBRE)),
                        correo = getString(getColumnIndexOrThrow(UserContract.Columns.CORREO)),
                        clave = getString(getColumnIndexOrThrow(UserContract.Columns.CLAVE)),
                        activo = getInt(getColumnIndexOrThrow(UserContract.Columns.ACTIVO)) == 1
                    )
                )
            }
        }
        cursor.close()
        return list
    }

    fun getByEmail(email: String): UserEntity? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            UserContract.TABLE_NAME,
            null, "${UserContract.Columns.CORREO} = ?", arrayOf(email), null, null, null
        )

        var user: UserEntity? = null
        if (cursor.moveToFirst()) {
            user = UserEntity(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(UserContract.Columns.ID)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(UserContract.Columns.NOMBRE)),
                correo = cursor.getString(cursor.getColumnIndexOrThrow(UserContract.Columns.CORREO)),
                clave = cursor.getString(cursor.getColumnIndexOrThrow(UserContract.Columns.CLAVE)),
                activo = cursor.getInt(cursor.getColumnIndexOrThrow(UserContract.Columns.ACTIVO)) == 1
            )
        }

        cursor.close()
        db.close()
        return user
    }
}