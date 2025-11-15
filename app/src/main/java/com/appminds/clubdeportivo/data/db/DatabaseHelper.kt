package com.appminds.clubdeportivo.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.appminds.clubdeportivo.data.db.contracts.ClientContract
import com.appminds.clubdeportivo.data.db.contracts.ProfesorAttendanceContract
import com.appminds.clubdeportivo.data.db.contracts.ProfesorContract
import com.appminds.clubdeportivo.data.db.contracts.UserContract
import com.appminds.clubdeportivo.data.db.contracts.ActividadContract
import com.appminds.clubdeportivo.data.db.contracts.CuotaContract
import com.appminds.clubdeportivo.data.db.contracts.PagoActividadContract

class DatabaseHelper (context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private const val DB_NAME = "club_deportivo.db"
        private const val DB_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("DatabaseHelper", "onCreate called")
        try {
            db?.execSQL(UserContract.CREATE_TABLE)
            Log.d("DatabaseHelper", "UserContract table created")

            db?.execSQL(ClientContract.CREATE_TABLE)
            Log.d("DatabaseHelper", "ClientContract table created")

            db?.execSQL(ProfesorContract.CREATE_TABLE)
            Log.d("DatabaseHelper", "ProfesorContract table created")

            db?.execSQL(ProfesorAttendanceContract.CREATE_TABLE)
            Log.d("DatabaseHelper", "ProfesorAttendanceContract table created")
            
            db?.execSQL(ActividadContract.CREATE_TABLE)
            Log.d("DatabaseHelper", "ActividadContract table created")

            db?.execSQL(CuotaContract.CREATE_TABLE)
            Log.d("DatabaseHelper", "CuotaContract table created")

            db?.execSQL(PagoActividadContract.CREATE_TABLE)
            Log.d("DatabaseHelper", "PagoActividadContract table created")

        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error creating tables", e)
        }
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db?.execSQL("DROP TABLE IF EXISTS ${UserContract.TABLE_NAME}")
        db?.execSQL("DROP TABLE IF EXISTS ${ClientContract.TABLE_NAME}")
        db?.execSQL("DROP TABLE IF EXISTS ${ProfesorContract.TABLE_NAME}")
        db?.execSQL("DROP TABLE IF EXISTS ${ProfesorAttendanceContract.TABLE_NAME}")
        db?.execSQL("DROP TABLE IF EXISTS ${ActividadContract.TABLE_NAME}")
        db?.execSQL("DROP TABLE IF EXISTS ${CuotaContract.TABLE_NAME}")
        db?.execSQL("DROP TABLE IF EXISTS ${PagoActividadContract.TABLE_NAME}")

        onCreate(db)
    }
}