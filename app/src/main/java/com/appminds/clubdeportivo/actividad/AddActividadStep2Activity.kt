package com.appminds.clubdeportivo.actividad

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.models.AddActividadDto
import com.appminds.clubdeportivo.profesor.ProfesorConfirmActivity

class AddActividadStep2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_actividad_step2)

        val actividad = intent.getSerializableExtra("actividad") as? AddActividadDto
            ?: run { finish(); return }

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val btnSave: AppCompatButton = findViewById(R.id.btnAddActSave)

        val tvName: TextView = findViewById(R.id.tvName)
        val tvDays: TextView = findViewById(R.id.tvDays)
        val tvTime: TextView = findViewById(R.id.tvTime)
        val tvPrice: TextView = findViewById(R.id.tvPrice)
        val tvCapacity: TextView = findViewById(R.id.tvCapacity)

        tvName.text = actividad.name
        tvDays.text = actividad.days
        tvTime.text = actividad.time
        tvPrice.text = actividad.price
        tvCapacity.text = actividad.capacity

        btnBack.setOnClickListener { finish() }
        btnSave.setOnClickListener {
            // TODO: persistir (SQLite/Room o API)
            // Por ahora, mostramos confirmación reutilizando tu pantalla de éxito.
            val intent = Intent(this, ProfesorConfirmActivity::class.java).apply {
                putExtra("message", "¡Actividad registrada con éxito!")
                putExtra("labelBtn", "Volver al menú")
                putExtra("goTo", AddActividadActivity::class.java) // o MainMenuActivity::class.java
            }
            startActivity(intent)
            finish()
        }
    }
}
