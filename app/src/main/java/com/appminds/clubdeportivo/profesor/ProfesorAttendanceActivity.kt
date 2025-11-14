package com.appminds.clubdeportivo.profesor

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.adapters.AttendanceAdapter
import com.appminds.clubdeportivo.data.dao.ProfesorAttendanceDao
import com.appminds.clubdeportivo.data.dao.ProfesorDao
import com.appminds.clubdeportivo.models.AttendanceCardDto
import com.appminds.clubdeportivo.models.enums.AttendanceStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ProfesorAttendanceActivity : AppCompatActivity() {
    private lateinit var attendanceDao: ProfesorAttendanceDao
    private lateinit var profesorDao: ProfesorDao
    private lateinit var adapter: AttendanceAdapter
    private lateinit var rvProfesores: RecyclerView

    private val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    private val displayFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profesor_attendance)

        initializeDaos()
        setupUI()
        loadAttendanceData()
    }

    private fun initializeDaos() {
        attendanceDao = ProfesorAttendanceDao(this)
        profesorDao = ProfesorDao(this)
    }

    private fun setupUI() {
        // Configurar fecha
        findViewById<TextView>(R.id.tvCurrentDate).text =
            displayFormatter.format(Date())

        // Configurar RecyclerView
        rvProfesores = findViewById(R.id.rvProfesores)
        rvProfesores.layoutManager = LinearLayoutManager(this)

        adapter = AttendanceAdapter(
            onSetPresent = { item -> updateAttendanceStatus(item, AttendanceStatus.PRESENTE) },
            onSetAbsent = { item -> updateAttendanceStatus(item, AttendanceStatus.AUSENTE) },
            externalScope = lifecycleScope
        )
        rvProfesores.adapter = adapter

        // Configurar botón de retroceso
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            navigateToMenu()
        }
    }

    private fun loadAttendanceData() {
        lifecycleScope.launch {
            val attendanceList = withContext(Dispatchers.IO) {
                val today = dateFormatter.format(Date())
                initializeAttendanceForToday(today)
                buildAttendanceList(today)
            }

            adapter.updateList(attendanceList)
        }
    }

    private fun initializeAttendanceForToday(today: String) {
        val profesorList = profesorDao.getAll()

        profesorList.forEach { profesor ->
            if (!attendanceDao.exist(profesor.id!!, today)) {
                attendanceDao.insert(profesor.id!!, today, AttendanceStatus.PENDIENTE)
            }
        }
    }

    private fun buildAttendanceList(date: String): List<AttendanceCardDto> {
        val profesorList = profesorDao.getAll()
        val attendanceList = attendanceDao.getByDate(date) ?: emptyList()

        return attendanceList.map { attendance ->
            val profesor = profesorList.find { it.id == attendance.idProfesor }
            AttendanceCardDto(
                id = attendance.id,
                fullname = "${profesor?.firstname ?: ""} ${profesor?.lastname ?: ""}".trim(),
                status = attendance.status,
                activity = "Pendiente" // TODO: Obtener actividad real
            )
        }.sortedBy { it.status.ordinal }
    }

    private suspend fun updateAttendanceStatus(
        item: AttendanceCardDto,
        status: AttendanceStatus
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val rowsUpdated = attendanceDao.updateStatus(item.id!!, status)

            if (rowsUpdated > 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo actualizar el estado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun navigateToMenu() {
        startActivity(Intent(this, ProfesorMenuActivity::class.java))
        finish()
    }
}