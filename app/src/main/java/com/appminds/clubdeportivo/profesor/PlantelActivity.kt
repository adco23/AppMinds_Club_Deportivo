package com.appminds.clubdeportivo.profesor

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.adapters.AttendanceAdapter
import com.appminds.clubdeportivo.adapters.PlantelAdapter
import com.appminds.clubdeportivo.models.AttendanceCardDto
import com.appminds.clubdeportivo.models.PlantelItemDto
import com.appminds.clubdeportivo.models.enums.AttendanceStatus

class PlantelActivity : AppCompatActivity() {
    private val listMock = listOf(
        PlantelItemDto("Laura Benítez", "Natación", "25.678.901", "Av. Rivadavia 1234, CABA", "11-4567-8901", false ),
        PlantelItemDto("Carlos Méndez", "Fútbol", "30.123.456", "Calle San Martín 456, Rosario", "341-789-4560", true ),
        PlantelItemDto("Sofía Herrera", "Gimnasia", "28.345.678", "Av. Belgrano 789, Córdoba", "351-654-3210", false ),
        PlantelItemDto("Martín Ríos", "Tenis", "26.987.654", "Calle Mitre 321, Mendoza", "261-987-6543", true ),
        PlantelItemDto("Valentina López", "Yoga", "29.456.789", "Av. Alem 1590, Tucumán", "381-123-4567", false )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_plantel)

        val rvProfesores = findViewById<RecyclerView>(R.id.rvProfesores)
        rvProfesores.layoutManager = LinearLayoutManager(this)
        rvProfesores.adapter = PlantelAdapter(listMock)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }
    }
}