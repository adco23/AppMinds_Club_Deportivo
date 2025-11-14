package com.appminds.clubdeportivo.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.appminds.clubdeportivo.data.db.DatabaseHelper
import com.appminds.clubdeportivo.data.db.contracts.ProfesorAttendanceContract
import com.appminds.clubdeportivo.data.model.ProfesorAttendanceEntity
import com.appminds.clubdeportivo.models.enums.AttendanceStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.mutableListOf

class ProfesorAttendanceDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insert(idProfesor: Int, date: String, status: AttendanceStatus): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ProfesorAttendanceContract.Columns.ID_PROFESOR, idProfesor)
            put(ProfesorAttendanceContract.Columns.FECHA, date)
            put(ProfesorAttendanceContract.Columns.ESTADO, status.toString())
        }

        return db.insert(ProfesorAttendanceContract.TABLE_NAME, null, values)
    }

    fun exist(profesorId: Int, date: String): Boolean {
        return dbHelper.readableDatabase.use { db ->
            val cursor = db.query(
                ProfesorAttendanceContract.TABLE_NAME,
                arrayOf(ProfesorAttendanceContract.Columns.ID),
                "${ProfesorAttendanceContract.Columns.ID_PROFESOR} = ? AND ${ProfesorAttendanceContract.Columns.FECHA} = ?",
                arrayOf(profesorId.toString(), date),
                null,
                null,
                null
            )

            cursor.use { it.count > 0 }
        }
    }

    fun insertForToday(context: Context): Int {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        var inserted = 0

        dbHelper.writableDatabase.use { db ->
            db.beginTransaction()
            try {
                val profesors = ProfesorDao(context).getAll()

                profesors.forEach { profesor ->
                    val values = ContentValues().apply {
                        put(ProfesorAttendanceContract.Columns.ID_PROFESOR, profesor.id)
                        put(ProfesorAttendanceContract.Columns.FECHA, today)
                        put(ProfesorAttendanceContract.Columns.ESTADO, AttendanceStatus.PENDIENTE.toString())
                    }

                    if (db.insert(ProfesorAttendanceContract.TABLE_NAME, null, values) != -1L) {
                        inserted++
                    }
                }

                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }

        return inserted
    }

    fun getByDate(date: String): List<ProfesorAttendanceEntity>? {
        return dbHelper.readableDatabase.use { db ->
            val cursor = db.query(
                ProfesorAttendanceContract.TABLE_NAME,
                null,
                "${ProfesorAttendanceContract.Columns.FECHA} = ?",
                arrayOf(date),
                null,
                null,
                null
            )

            cursor.use { buildListFromCursor(it) }
                .sortedBy { it.status.ordinal }
        }
    }

    private fun buildListFromCursor(cursor: Cursor): List<ProfesorAttendanceEntity> {
        val list = mutableListOf<ProfesorAttendanceEntity>()

        while (cursor.moveToNext()) {
            list.add(
                ProfesorAttendanceEntity(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ProfesorAttendanceContract.Columns.ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(ProfesorAttendanceContract.Columns.ID_PROFESOR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ProfesorAttendanceContract.Columns.FECHA)),
                    AttendanceStatus.valueOf(
                        cursor.getString(cursor.getColumnIndexOrThrow(ProfesorAttendanceContract.Columns.ESTADO))
                    )
                )
            )
        }

        return list
    }

    fun updateStatus(id: Int, status: AttendanceStatus): Int {
        return dbHelper.writableDatabase.use { db ->
            val values = ContentValues().apply {
                put(ProfesorAttendanceContract.Columns.ESTADO, status.toString())
            }

            db.update(
                ProfesorAttendanceContract.TABLE_NAME,
                values,
                "${ProfesorAttendanceContract.Columns.ID} = ?",
                arrayOf(id.toString())
            )
        }
    }

    fun deleteByDate(date: String): Int {
        return dbHelper.writableDatabase.use { db ->
            db.delete(
                ProfesorAttendanceContract.TABLE_NAME,
                "${ProfesorAttendanceContract.Columns.FECHA} = ?",
                arrayOf(date)
            )
        }
    }

    fun getAttendanceStats(startDate: String, endDate: String): Map<Int, AttendanceStats> {
        val stats = mutableMapOf<Int, AttendanceStats>()

        dbHelper.readableDatabase.use { db ->
            val cursor = db.query(
                ProfesorAttendanceContract.TABLE_NAME,
                null,
                "${ProfesorAttendanceContract.Columns.FECHA} BETWEEN ? AND ?",
                arrayOf(startDate, endDate),
                null,
                null,
                null
            )

            cursor.use {
                while (it.moveToNext()) {
                    val profesorId = it.getInt(it.getColumnIndexOrThrow(ProfesorAttendanceContract.Columns.ID_PROFESOR))
                    val status = AttendanceStatus.valueOf(
                        it.getString(it.getColumnIndexOrThrow(ProfesorAttendanceContract.Columns.ESTADO))
                    )

                    val currentStats = stats.getOrDefault(profesorId, AttendanceStats())
                    stats[profesorId] = when (status) {
                        AttendanceStatus.PRESENTE -> currentStats.copy(present = currentStats.present + 1)
                        AttendanceStatus.AUSENTE -> currentStats.copy(absent = currentStats.absent + 1)
                        AttendanceStatus.PENDIENTE -> currentStats.copy(pending = currentStats.pending + 1)
                    }
                }
            }
        }

        return stats
    }

    data class AttendanceStats(
        val present: Int = 0,
        val absent: Int = 0,
        val pending: Int = 0
    ) {
        val total: Int get() = present + absent + pending
        val presentPercentage: Float get() = if (total > 0) (present.toFloat() / total) * 100 else 0f
    }
}