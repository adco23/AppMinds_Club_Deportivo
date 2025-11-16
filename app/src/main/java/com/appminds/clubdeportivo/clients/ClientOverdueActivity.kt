package com.appminds.clubdeportivo.clients

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.adapters.OverdueClientAdapter
import com.appminds.clubdeportivo.data.dao.ClientDao
import com.appminds.clubdeportivo.models.OverdueClientDto
import java.text.SimpleDateFormat
import kotlin.getValue
import java.util.Calendar
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class ClientOverdueActivity : AppCompatActivity() {
    private val clienteDao by lazy { ClientDao(this) }
    private val listMock = listOf(
        OverdueClientDto("Lucía Gómez", "25.678.901", "2025-08-15", "Vencido"),
        OverdueClientDto("Martín Ríos", "30.123.456", "2025-07-10", "Vencido"),
        OverdueClientDto("Sofía Herrera", "28.345.678", "2025-09-01", "Vencido"),
        OverdueClientDto("Carlos Méndez", "26.987.654", "2025-06-30", "Vencido"),
        OverdueClientDto("Valentina López", "29.456.789", "2025-08-01", "Vencido"),
        OverdueClientDto("Federico Torres", "27.112.334", "2025-07-25", "Vencido"),
        OverdueClientDto("Ana Beltrán", "31.998.765", "2025-09-20", "Vencido"),
        OverdueClientDto("Julián Pérez", "24.556.789", "2025-05-15", "Vencido"),
        OverdueClientDto("Camila Duarte", "32.445.123", "2025-08-30", "Vencido"),
        OverdueClientDto("Diego Fernández", "29.876.543", "2025-07-05", "Vencido")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_overdue)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dateIso = "%04d-%02d-%02d".format(year, month, day)

        val clientList = clienteDao.getMorososByDate(dateIso)

        val list: MutableList<OverdueClientDto> = clientList.map { c ->
//            val cal = Calendar.getInstance().apply { timeInMillis = c.dueDate }
//            val formattedDate = "%04d-%02d-%02d".format(
//                cal.get(Calendar.YEAR),
//                cal.get(Calendar.MONTH) + 1,
//                cal.get(Calendar.DAY_OF_MONTH)
//            )

        Log.i("CLIENTE", c.toString())

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
              sdf.timeZone = TimeZone.getTimeZone("America/Argentina/Buenos_Aires")
//              sdf.format(c.dueDate)

            OverdueClientDto(
                name = "${c.firstname} ${c.lastname}",
                dni = c.dni,
                dueDate = sdf.format(c.dueDate),
                status = "Vence hoy"
            )
        }.toMutableList()

        val rvClients = findViewById<RecyclerView>(R.id.rvOverdueClientList)
        rvClients.layoutManager = LinearLayoutManager(this)
        rvClients.adapter = OverdueClientAdapter(list)
    }
}