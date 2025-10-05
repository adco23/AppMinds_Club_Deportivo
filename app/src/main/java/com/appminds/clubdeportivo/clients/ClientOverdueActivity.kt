package com.appminds.clubdeportivo.clients

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.adapters.OverdueClientAdapter
import com.appminds.clubdeportivo.models.OverdueClientDto

class ClientOverdueActivity : AppCompatActivity() {
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

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val rvClients = findViewById<RecyclerView>(R.id.rvOverdueClientList)
        rvClients.layoutManager = LinearLayoutManager(this)
        rvClients.adapter = OverdueClientAdapter(listMock)

    }
}