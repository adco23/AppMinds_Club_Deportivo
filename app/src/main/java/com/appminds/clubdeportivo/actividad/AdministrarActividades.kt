

package com.appminds.clubdeportivo.actividad

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.actividad.UpdateActividadActivity
import com.appminds.clubdeportivo.adapters.ActividadAdminAdapter
import com.appminds.clubdeportivo.data.dao.ActividadDao
import com.appminds.clubdeportivo.data.model.ActividadEntity
import android.widget.ImageButton

class AdministrarActividades : AppCompatActivity() {

    private lateinit var actividadDao: ActividadDao
    private lateinit var actividades: MutableList<ActividadEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_administrar_actividades)

        actividadDao = ActividadDao(this)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val rv = findViewById<RecyclerView>(R.id.rvActividades)

        actividades = actividadDao.getAll().toMutableList()

        val adapter = ActividadAdminAdapter(
            list = actividades,
            onEdit = { actividad ->
                startActivity(
                    Intent(this, UpdateActividadActivity::class.java)
                        .putExtra("actividad", actividad)
                )
            },
            onDelete = { actividad ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar actividad")
                    .setMessage("¿Seguro que querés eliminar \"${actividad.name}\"?")
                    .setPositiveButton("Sí, eliminar") { _, _ ->
                        val id = actividad.id ?: return@setPositiveButton
                        val deleted = actividadDao.deleteById(id)
                        if (deleted) {
                            val idx = actividades.indexOfFirst { it.id == id }
                            if (idx != -1) {
                                actividades.removeAt(idx)
                                rv.adapter?.notifyItemRemoved(idx)
                            }
                        } else {
                            Toast.makeText(this, "Error al eliminar actividad", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv.adapter = adapter

        btnBack.setOnClickListener { finish() }
    }
}
