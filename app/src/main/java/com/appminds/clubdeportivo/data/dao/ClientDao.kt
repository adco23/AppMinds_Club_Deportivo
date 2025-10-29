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
                list.add(
                    ClientEntity(
                        id = getInt(getColumnIndexOrThrow(ClientContract.Columns.ID)),
                        firstname = getString(getColumnIndexOrThrow(ClientContract.Columns.NOMBRE)),
                        lastname = getString(getColumnIndexOrThrow(ClientContract.Columns.APELLIDO)),
                        dni = getString(getColumnIndexOrThrow(ClientContract.Columns.DNI)),
                        email = getString(getColumnIndexOrThrow(ClientContract.Columns.CORREO)),
                        phone = getString(getColumnIndexOrThrow(ClientContract.Columns.TELEFONO)),
                        address = getString(getColumnIndexOrThrow(ClientContract.Columns.DOMICILIO)),
                        registeredAt = getString(getColumnIndexOrThrow(ClientContract.Columns.FECHA_ALTA)),
                        isPhysicallyFit = getInt(getColumnIndexOrThrow(ClientContract.Columns.APTO_FISICO)) == 1,
                        type = ClientTypeEnum.valueOf(getString(getColumnIndexOrThrow(ClientContract.Columns.TIPO_CLIENTE))),
                        status = ClientStatusEnum.valueOf(getString(getColumnIndexOrThrow(ClientContract.Columns.ESTADO)))
                    )
                )
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
            client = ClientEntity(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ClientContract.Columns.ID)),
                firstname = cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.NOMBRE)),
                lastname = cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.APELLIDO)),
                dni = cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.DNI)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.CORREO)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.TELEFONO)),
                address = cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.DOMICILIO)),
                registeredAt = cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.FECHA_ALTA)),
                isPhysicallyFit = cursor.getInt(cursor.getColumnIndexOrThrow(ClientContract.Columns.APTO_FISICO)) == 1,
                type = ClientTypeEnum.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.TIPO_CLIENTE))),
                status = ClientStatusEnum.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ClientContract.Columns.ESTADO)))
            )
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
}