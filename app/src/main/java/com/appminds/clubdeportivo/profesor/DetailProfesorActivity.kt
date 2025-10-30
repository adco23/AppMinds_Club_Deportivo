package com.appminds.clubdeportivo.profesor

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.data.model.ProfesorEntity
import com.appminds.clubdeportivo.models.ProfesorDto

class DetailProfesorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_profesor)

        val profesor = intent.getSerializableExtra("profesor") as? ProfesorEntity

        profesor?.let {
            "${it.firstname} ${it.lastname}".also { findViewById<TextView>(R.id.tvProfesorName).text = it }
            findViewById<TextView>(R.id.tvDni).text = it.dni
            findViewById<TextView>(R.id.tvAddress).text = it.address
            findViewById<TextView>(R.id.tvPhone).text = it.phone
            findViewById<TableRow>(R.id.trIsSubstitute).isVisible = it.isSubstitute
            findViewById<TextView>(R.id.tvActividad).text = it.activity ?: "A definir"
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<AppCompatButton>(R.id.btnUpdateProfesor).setOnClickListener {
            val intent = Intent(this, UpdateProfesorActivity::class.java).apply {
                putExtra("profesor", profesor)
            }
            startActivity(intent)
        }
        findViewById<AppCompatButton>(R.id.btnDeleteProfesor).setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("¿Deseás eliminar al profesor ${profesor?.firstname} ${profesor?.lastname}?")
                .setMessage("Esta acción eliminará del plantel al profesor.")
                .setPositiveButton("Sí") { dialog, _ ->

                    dialog.dismiss()

                    val intent = Intent(this, ProfesorConfirmActivity::class.java)
                    intent.putExtra("message", "¡Profesor eliminado con éxito!")
                    intent.putExtra("labelBtn", "Volver a menú")
                    intent.putExtra("goTo", PlantelActivity::class.java)

                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .setCancelable(true)
                .show()
        }

    }
}