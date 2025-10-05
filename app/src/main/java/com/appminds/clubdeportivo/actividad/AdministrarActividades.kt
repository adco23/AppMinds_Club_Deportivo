package com.appminds.clubdeportivo.actividad

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.adapters.ActividadAdminAdapter
import com.appminds.clubdeportivo.models.ActividadDto
import com.appminds.clubdeportivo.profesor.ProfesorConfirmActivity

class AdministrarActividades : AppCompatActivity() {

    private val actividades = mutableListOf(
        ActividadDto("Yoga", "Lun, Mié y Vie", "15:00", "$ 20", "50"),
        ActividadDto("Funcional", "Mar y Jue", "19:00", "$ 25", "30"),
        ActividadDto("Spinning", "Lun a Vie", "07:30", "$ 30", "20"),
        ActividadDto("Fútbol", "Sábados", "10:00", "$ 15", "22"),
        ActividadDto("Hockey", "Miércoles", "18:00", "$ 18", "18"),
        ActividadDto("Musculación", "Lun a Vie", "Libre", "$ 10/día", "100")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_actividades)

        // --- si esto es null, el layout que se infló NO tiene btnBack ---
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
            ?: run {
                Toast.makeText(this, "btnBack no encontrado en el layout", Toast.LENGTH_LONG).show()
                return
            }

        val rv = findViewById<RecyclerView>(R.id.rvActividades)
            ?: run {
                Toast.makeText(this, "rvActividades no encontrado en el layout", Toast.LENGTH_LONG).show()
                return
            }

        val adapter = ActividadAdminAdapter(
            list = actividades,
            onEdit = { actividad ->
                Toast.makeText(this, "Editar: ${actividad.name}", Toast.LENGTH_SHORT).show()
                // Cuando tengas la pantalla:
                // startActivity(Intent(this, UpdateActividadActivity::class.java).putExtra("actividad", actividad))
            },
            onDelete = { actividad ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar actividad")
                    .setMessage("¿Seguro que querés eliminar \"${actividad.name}\"?")
                    .setPositiveButton("Sí, eliminar") { _, _ ->
                        val idx = actividades.indexOfFirst { it.name == actividad.name }
                        if (idx != -1) {
                            actividades.removeAt(idx)
                            rv.adapter?.notifyItemRemoved(idx)

                            startActivity(
                                Intent(this, ProfesorConfirmActivity::class.java).apply {
                                    putExtra("message", "¡Actividad eliminada con éxito!")
                                    putExtra("labelBtn", "Volver al listado")
                                    putExtra("goTo", AdministrarActividades::class.java)
                                }
                            )
                            finish()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        btnBack.setOnClickListener { finish() }
    }
}
