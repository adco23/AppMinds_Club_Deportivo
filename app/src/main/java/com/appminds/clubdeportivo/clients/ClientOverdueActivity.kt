package com.appminds.clubdeportivo.clients

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.adapters.OverdueClientAdapter
import com.appminds.clubdeportivo.data.dao.ClientDao
import com.appminds.clubdeportivo.models.OverdueClientDto
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ClientOverdueActivity : AppCompatActivity() {
    private val clienteDao by lazy { ClientDao(this) }
    private lateinit var adapter: OverdueClientAdapter
    private lateinit var rvClients: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_overdue)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        rvClients = findViewById(R.id.rvOverdueClientList)
        rvClients.layoutManager = LinearLayoutManager(this)

        adapter = OverdueClientAdapter(emptyList())
        rvClients.adapter = adapter

        loadOverdueClients()
    }

    override fun onResume() {
        super.onResume()
        loadOverdueClients()
    }

    private fun loadOverdueClients() {
        // Obtener fecha actual en formato ISO
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dateIso = "%04d-%02d-%02d".format(year, month, day)

        // Consultar base de datos
        val clientList = clienteDao.getMorososByDate(dateIso)

        // Mapear a DTO
        val overdueClients = clientList.map { c ->
            Log.i("CLIENTE_MOROSO", "ID: ${c.id}, Nombre: ${c.firstname} ${c.lastname}, Vence: ${c.dueDate}")

            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("America/Argentina/Buenos_Aires")
            }

            OverdueClientDto(
                id = c.id,
                name = "${c.firstname} ${c.lastname}",
                dni = c.dni,
                dueDate = sdf.format(c.dueDate),
                status = "Vence hoy"
            )
        }

        // ⭐ Actualizar el adapter con los nuevos datos
        adapter.updateList(overdueClients)

        Log.i("CLIENTES_MOROSOS", "Total clientes con cuotas vencidas: ${overdueClients.size}")
    }
}