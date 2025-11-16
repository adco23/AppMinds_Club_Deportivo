package com.appminds.clubdeportivo.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.appminds.clubdeportivo.data.db.DatabaseHelper
import com.appminds.clubdeportivo.data.db.contracts.ClientContract
import com.appminds.clubdeportivo.data.model.ClientEntity
import com.appminds.clubdeportivo.models.enums.ClientStatusEnum
import com.appminds.clubdeportivo.models.enums.ClientTypeEnum

class ClientDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insert(client: ClientEntity): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ClientContract.Columns.NOMBRE, client.firstname)
            put(ClientContract.Columns.APELLIDO, client.lastname)
            put(ClientContract.Columns.DNI, client.dni)
            put(ClientContract.Columns.CORREO, client.email)
            put(ClientContract.Columns.TELEFONO, client.phone)
            put(ClientContract.Columns.DOMICILIO, client.address)
            put(ClientContract.Columns.FECHA_ALTA, client.registeredAt)
            put(ClientContract.Columns.FECHA_VENCIMIENTO, client.dueDate)
            put(ClientContract.Columns.APTO_FISICO, client.isPhysicallyFit)
            put(ClientContract.Columns.TIPO_CLIENTE, client.type.toString())
            put(ClientContract.Columns.ESTADO, client.status.toString())
        }

        return db.insert(ClientContract.TABLE_NAME, null, values)
    }

    fun getAll(): List<ClientEntity> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            ClientContract.TABLE_NAME,
            null, null, null, null, null, null
        )

        val list = mutableListOf<ClientEntity>()
        with(cursor) {
            while (moveToNext()) {
                list.add( getCursorValues(cursor) )
            }
        }
        cursor.close()
        return list
    }

    fun getClientByDNI(dni: String): ClientEntity? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            ClientContract.TABLE_NAME,
            null,
            "${ClientContract.Columns.DNI} LIKE ?",
            arrayOf("%$dni%"),
            null,
            null,
            null
        )

        var client: ClientEntity? = null
        if (cursor.moveToFirst()) {
            client = getCursorValues(cursor)
        }
        cursor.close()
        db.close()
        return client
    }

    fun hasRecords(): Boolean {
        val db = dbHelper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.query(
                ClientContract.TABLE_NAME,
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

    fun getClientByID(id: Int): ClientEntity? {
        val db = dbHelper.readableDatabase
        var client: ClientEntity? = null
        val selection = "${ClientContract.Columns.ID} = ?" // Columna ID = ?
        val selectionArgs = arrayOf(id.toString()) // El valor del ID

        // Ejecutar la consulta SQL SELECT
        val cursor: Cursor = db.query(
            ClientContract.TABLE_NAME,
            null, // Devolver todas las columnas
            selection,
            selectionArgs,
            null, null, null
        )

        with(cursor) {
            if (moveToFirst()) {
                client = getCursorValues(cursor)
            }
        }
        cursor.close()
        db.close()
        return client
    }

//    fun getMorososByDate(date: String?): List<ClientEntity> {
//        val db = dbHelper.readableDatabase
//        val cursor: Cursor = db.query(
//            ClientContract.TABLE_NAME,
//            null,
//            "${ClientContract.Columns.FECHA_VENCIMIENTO} = ?",
//            arrayOf(date.toString()),
//            null, null, null, null
//        )
//
//        val list = mutableListOf<ClientEntity>()
//        with(cursor) {
//            while (moveToNext()) {
//                list.add( getCursorValues(cursor) )
//            }
//        }
//        cursor.close()
//        return list
//    }
    fun getMorososByDate(dateIso: String): List<ClientEntity> {
        val db = dbHelper.readableDatabase

        // Consulta segura que convierte milisegundos a fecha ISO
        val cursor: Cursor = db.query(
            ClientContract.TABLE_NAME,
            null,
            "date(${ClientContract.Columns.FECHA_VENCIMIENTO} / 1000, 'unixepoch') = ?",
            arrayOf(dateIso),
            null, null, null, null
        )

        val list = mutableListOf<ClientEntity>()
        with(cursor) {
            while (moveToNext()) {
                try {
                    list.add(getCursorValues(cursor))
                } catch (e: Exception) {
                    e.printStackTrace() // Vemos si algún registro está mal
                }
            }
        }
        cursor.close()
        db.close()
        return list
    }


    private fun getCursorValues(cursor: Cursor): ClientEntity {
        return ClientEntity(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(ClientContract.Columns.ID)),
            firstname = cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.NOMBRE)),
            lastname = cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.APELLIDO)),
            dni = cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.DNI)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.CORREO)),
            phone = cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.TELEFONO)),
            address = cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.DOMICILIO)),
            registeredAt = cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.FECHA_ALTA)),
            dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(ClientContract.Columns.FECHA_VENCIMIENTO)),
            isPhysicallyFit = cursor.getInt(cursor.getColumnIndexOrThrow(ClientContract.Columns.APTO_FISICO)) == 1,
            type = ClientTypeEnum.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.TIPO_CLIENTE))),
            status = ClientStatusEnum.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.ESTADO)))
        )
    }

}