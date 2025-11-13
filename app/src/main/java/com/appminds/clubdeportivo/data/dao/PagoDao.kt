package com.appminds.clubdeportivo.data.dao

import android.content.ContentValues
import android.content.Context
import com.appminds.clubdeportivo.data.db.DatabaseHelper
import com.appminds.clubdeportivo.data.db.contracts.CuotaContract
import com.appminds.clubdeportivo.data.db.contracts.PagoActividadContract
import com.appminds.clubdeportivo.data.db.contracts.SocioContract
import com.appminds.clubdeportivo.data.model.CuotaEntity
import com.appminds.clubdeportivo.data.model.PagoActividadEntity

class PagoDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    /**
     * Registra un pago de cuota y actualiza el estado del socio en una transacción.
     * Si el INSERT falla o el UPDATE falla, toda la operación se revierte (Rollback).
     * @param cuota La entidad de la cuota a pagar.
     * @param fechaNuevaVencimiento La fecha que se debe establecer en el socio (Long).
     * @return True si la transacción fue exitosa.
     */
    fun registrarPagoCuota(cuota: CuotaEntity, fechaNuevaVencimiento: Long): Boolean {
        val db = dbHelper.writableDatabase
        var success = false

        // 1. INICIAR TRANSACCIÓN
        db.beginTransaction()

        try {
            // 2. INSERTAR CUOTA
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
                // 3. ACTUALIZAR ESTADO DEL SOCIO
                val socioValues = ContentValues().apply {
                    put(SocioContract.Columns.ACTIVO, 1) // Estado 'activo'
                    put(SocioContract.Columns.VENCIMIENTO, fechaNuevaVencimiento)
                }

                val updatedRows = db.update(
                    SocioContract.TABLE_NAME,
                    socioValues,
                    "${SocioContract.Columns.CLIENTE_ID} = ?",
                    arrayOf(cuota.clienteId.toString())
                )

                if (updatedRows > 0) {
                    db.setTransactionSuccessful() // Confirma la Transacción (Commit)
                    success = true
                }
            }
        } catch (e: Exception) {
            // Log.e("PagoDao", "Error en transacción de cuota: ${e.message}")
            success = false // Falla
        } finally {
            // 4. FINALIZAR TRANSACCIÓN (hace Rollback si setTransactionSuccessful no fue llamado)
            db.endTransaction()
            db.close()
        }
        return success
    }

    /**
     * Registra un pago simple de actividad.
     * @return True si la inserción fue exitosa.
     */
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