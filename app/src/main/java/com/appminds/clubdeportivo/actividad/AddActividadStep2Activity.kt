package com.appminds.clubdeportivo.actividad

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.data.dao.ActividadDao
import com.appminds.clubdeportivo.data.model.ActividadEntity
import com.appminds.clubdeportivo.models.AddActividadDto
import com.appminds.clubdeportivo.profesor.ProfesorConfirmActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddActividadStep2Activity : AppCompatActivity() {

    private lateinit var actividadDao: ActividadDao

    private lateinit var btnBack: ImageButton
    private lateinit var btnSave: AppCompatButton
    private lateinit var tvName: TextView
    private lateinit var tvDays: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvCapacity: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_actividad_step2)

        actividadDao = ActividadDao(this)

        val actividad = intent.getSerializableExtra("actividad") as? AddActividadDto
            ?: run { finish(); return }

        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnAddActSave)

        tvName = findViewById(R.id.tvName)
        tvDays = findViewById(R.id.tvDays)
        tvTime = findViewById(R.id.tvTime)
        tvPrice = findViewById(R.id.tvPrice)
        tvCapacity = findViewById(R.id.tvCapacity)

        tvName.text = actividad.name
        tvDays.text = actividad.days
        tvTime.text = "${actividad.startTime} - ${actividad.endTime}"
        tvPrice.text = actividad.price.toString()
        tvCapacity.text = actividad.capacity.toString()

        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val entity = ActividadEntity(
                id = null,
                name = actividad.name,
                days = actividad.days,
                startTime = actividad.startTime,
                endTime = actividad.endTime,
                price = actividad.price,
                capacity = actividad.capacity
            )

            lifecycleScope.launch {
                try {
                    val insertResult = withContext(Dispatchers.IO) {
                        actividadDao.insert(entity)
                    }

                    if (insertResult > 0) {
                        val intent = Intent(
                            this@AddActividadStep2Activity,
                            ProfesorConfirmActivity::class.java
                        ).apply {
                            putExtra("message", "¡Actividad registrada con éxito!")
                            putExtra("labelBtn", "Volver al menú")
                            putExtra("goTo", AddActividadActivity::class.java)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        throw Exception("No se pudo insertar la actividad")
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@AddActividadStep2Activity,
                        "Error al guardar: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
