package com.appminds.clubdeportivo.data.dao

import android.content.ContentValues
import android.content.Context
import com.appminds.clubdeportivo.data.db.DatabaseHelper
import com.appminds.clubdeportivo.data.db.contracts.CuotaContract
import com.appminds.clubdeportivo.data.db.contracts.PagoActividadContract
import com.appminds.clubdeportivo.data.model.CuotaEntity
import com.appminds.clubdeportivo.data.model.PagoActividadEntity
import com.appminds.clubdeportivo.data.db.contracts.ClientContract

private const val TAG = "PagoDao"
class PagoDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun registrarPagoCuota(cuota: CuotaEntity, fechaNuevaVencimiento: Long): Boolean {
        val db = dbHelper.writableDatabase
        var success = false

        db.beginTransaction()

        try {
            val cuotaValues = ContentValues().apply {
                put(CuotaContract.Columns.CLIENTE_ID, cuota.clienteId)
                put(CuotaContract.Columns.FECHA_VENCIMIENTO, cuota.fechaVencimiento)
                put(CuotaContract.Columns.FECHA_PAGO, cuota.fechaPago)
                put(CuotaContract.Columns.MONTO, cuota.monto)
                put(CuotaContract.Columns.FORMA_PAGO, cuota.formaPago)
                put(CuotaContract.Columns.PROMOCION, cuota.promocion)
                put(CuotaContract.Columns.ESTADO, cuota.estado) // 'pagada'
            }
            val cuotaId = db.insert(CuotaContract.TABLE_NAME, null, cuotaValues)

            if (cuotaId > 0) {
                val clienteValues = ContentValues().apply {
                    put(ClientContract.Columns.ESTADO, "ACTIVO")
                    put(ClientContract.Columns.FECHA_VENCIMIENTO, fechaNuevaVencimiento)
                }

                val updatedRows = db.update(
                    ClientContract.TABLE_NAME, // Apunta a la tabla CLIENTE
                    clienteValues,
                    "${ClientContract.Columns.ID} = ?", // Filtra por el ID del cliente
                    arrayOf(cuota.clienteId.toString())
                )

                if (updatedRows > 0) {
                    db.setTransactionSuccessful() // Commit si ambos pasos fueron exitosos
                    success = true
                }
            }
        } catch (e: Exception) {
            success = false
        } finally {
            db.endTransaction()
            db.close()
        }
        return success
    }

    fun registrarPagoActividad(pagoActividad: PagoActividadEntity): Boolean {
        val db = dbHelper.writableDatabase

        val pagoValues = ContentValues().apply {
            put(PagoActividadContract.Columns.ID_ACTIVIDAD, pagoActividad.idActividad)
            put(PagoActividadContract.Columns.ID_CLIENTE, pagoActividad.idCliente)
            put(PagoActividadContract.Columns.FECHA_PAGO, pagoActividad.fechaPago)
            put(PagoActividadContract.Columns.FORMA_PAGO, pagoActividad.formaPago)
        }

        val newRowId = db.insert(PagoActividadContract.TABLE_NAME, null, pagoValues)
        db.close()
        return newRowId > 0
    }
}