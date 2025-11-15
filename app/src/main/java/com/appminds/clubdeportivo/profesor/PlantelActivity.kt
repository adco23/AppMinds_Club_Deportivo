package com.appminds.clubdeportivo.profesor

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.adapters.PlantelAdapter
import com.appminds.clubdeportivo.data.dao.ActividadDao
import com.appminds.clubdeportivo.data.dao.ProfesorDao
import com.appminds.clubdeportivo.models.PlantelCardDto
import com.appminds.clubdeportivo.models.ProfesorDto

class PlantelActivity : AppCompatActivity() {
    private val profesorDao by lazy { ProfesorDao(this) }
    private val activityDao by lazy { ActividadDao(this) }
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_plantel)

        initViews()
    }

    private fun initViews() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        recyclerView = findViewById(R.id.rvProfesores)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val activities = activityDao.getAll()
        val profesores = profesorDao.getAll()
        val list: MutableList<PlantelCardDto> = profesores.map { p ->
            PlantelCardDto(p.id!!, p.firstname, p.lastname, activities.find { it.id == p.activity }?.name ?: "-")
        }.toMutableList()


        recyclerView.adapter = PlantelAdapter(list)
    }
}