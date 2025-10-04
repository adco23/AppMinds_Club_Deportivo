package com.appminds.clubdeportivo.profesor

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.adapters.AttendanceAdapter
import com.appminds.clubdeportivo.models.AttendanceCardDto
import com.appminds.clubdeportivo.models.enums.AttendanceStatus

class ProfesorAttendanceActivity : AppCompatActivity() {
    private val listMock = listOf(
        AttendanceCardDto("Lucía Fernández", AttendanceStatus.PENDIENTE, "Natación"),
        AttendanceCardDto("Martín Gómez", AttendanceStatus.PRESENTE, "Fútbol"),
        AttendanceCardDto("Sofía Ramírez", AttendanceStatus.AUSENTE, "Tenis"),
        AttendanceCardDto("Julián Torres", AttendanceStatus.PRESENTE, "Básquet"),
        AttendanceCardDto("Valentina Ruiz", AttendanceStatus.PENDIENTE, "Yoga"),
        AttendanceCardDto("Tomás Herrera", AttendanceStatus.AUSENTE, "Vóley"),
        AttendanceCardDto("Camila Díaz", AttendanceStatus.PRESENTE, "Gimnasia"),
        AttendanceCardDto("Federico López", AttendanceStatus.PENDIENTE, "Atletismo"),
        AttendanceCardDto("Agustina Castro", AttendanceStatus.AUSENTE, "Natación"),
        AttendanceCardDto("Nicolás Medina", AttendanceStatus.PRESENTE, "Fútbol")
    ).sortedBy {
        when (it.status) {
            AttendanceStatus.PENDIENTE -> 0
            AttendanceStatus.PRESENTE -> 1
            AttendanceStatus.AUSENTE -> 2
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profesor_attendance)

        val dateElement = findViewById<TextView>(R.id.tvCurrentDate)
        val backButton = findViewById<ImageButton>(R.id.btnBack)

        val today = java.util.Date()
        val format = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        val date = format.format(today)

        dateElement.text = date

        val rvProfesores = findViewById<RecyclerView>(R.id.rvProfesores)
        rvProfesores.layoutManager = LinearLayoutManager(this)
        rvProfesores.adapter = AttendanceAdapter(listMock)

        backButton.setOnClickListener {
            startActivity(Intent(this, ProfesorMenuActivity::class.java))
            finish()
        }
    }
}