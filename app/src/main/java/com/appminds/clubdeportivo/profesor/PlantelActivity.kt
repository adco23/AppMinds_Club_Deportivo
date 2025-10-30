package com.appminds.clubdeportivo.profesor

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.adapters.PlantelAdapter
import com.appminds.clubdeportivo.data.dao.ProfesorDao
import com.appminds.clubdeportivo.models.ProfesorDto

class PlantelActivity : AppCompatActivity() {
    private lateinit var profesorDao: ProfesorDao
    private lateinit var recyclerView: RecyclerView

    private val listMock = listOf(
        ProfesorDto("Laura", "Benítez", "Natación", "25678901", "Av. Rivadavia 1234, CABA", "1145678901", false ),
        ProfesorDto("Carlos", "Méndez", "Fútbol", "30123456", "Calle San Martín 456, Rosario", "3417894560", true ),
        ProfesorDto("Sofía", "Herrera", "Gimnasia", "28345678", "Av. Belgrano 789, Córdoba", "3516543210", false ),
        ProfesorDto("Martín", "Ríos", "Tenis", "26987654", "Calle Mitre 321, Mendoza", "2619876543", true ),
        ProfesorDto("Valentina", "López", "Yoga", "29456789", "Av. Alem 1590, Tucumán", "3811234567", false )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_plantel)

        profesorDao = ProfesorDao(this)
        initViews()
    }

    private fun initViews() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        recyclerView = findViewById(R.id.rvProfesores)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val profesores = profesorDao.getAll().toMutableList()
        recyclerView.adapter = PlantelAdapter(profesores)
    }
}