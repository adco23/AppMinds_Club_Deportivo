package com.appminds.clubdeportivo.profesor

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.data.dao.ActividadDao
import com.appminds.clubdeportivo.data.dao.ProfesorDao
import com.appminds.clubdeportivo.data.model.ActividadEntity
import com.appminds.clubdeportivo.data.model.ProfesorEntity

class DetailProfesorActivity : AppCompatActivity() {
    private val profesorDao by lazy { ProfesorDao(this) }
    private val activityDao by lazy { ActividadDao(this) }
    private lateinit var profesor: ProfesorEntity
    private lateinit var activity: ActividadEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_profesor)

        extractProfesorFromIntent()
        initViews()
        setupListeners()
    }

    private fun extractProfesorFromIntent() {
        val profesorID: Int = (intent.getSerializableExtra("profesorID") as? Int)
            ?: run {
                showError("Error al obtener datos del profesor")
                finish()
                return
            }

        profesor = profesorDao.getByID(profesorID)
        activity = activityDao.getById(profesor.activity)!!
    }

    private fun initViews() {
        profesor.let {
            "${it.firstname} ${it.lastname}".also { findViewById<TextView>(R.id.tvProfesorName).text = it }
            findViewById<TextView>(R.id.tvDni).text = it.dni
            findViewById<TextView>(R.id.tvAddress).text = it.address
            findViewById<TextView>(R.id.tvPhone).text = it.phone
            findViewById<TableRow>(R.id.trIsSubstitute).isVisible = it.isSubstitute
            findViewById<TextView>(R.id.tvActividad).text = activity.name
        }
    }

    private fun setupListeners() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<AppCompatButton>(R.id.btnUpdateProfesor).setOnClickListener { updateDetailsView() }

        findViewById<AppCompatButton>(R.id.btnDeleteProfesor).setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("¿Deseás eliminar al profesor ${profesor.firstname} ${profesor.lastname}?")
                .setMessage("Esta acción eliminará al profesor del plantel.")
                .setPositiveButton("Sí") { dialog, _ ->

                    dialog.dismiss()
                    deleteProfesor()
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .setCancelable(true)
                .show()
        }
    }

    private fun updateDetailsView() {
        val intent = Intent(this, UpdateProfesorActivity::class.java)
            .apply { putExtra("profesorID", profesor.id) }
        startActivity(intent)
    }

    private fun deleteProfesor() {
        val result = profesorDao.deleteById(profesor.id!!)

        if (result) {

            val intent = Intent(this, ProfesorConfirmActivity::class.java)
            intent.putExtra("message", "¡Profesor eliminado con éxito!")
            intent.putExtra("labelBtn", "Volver a menú")
            intent.putExtra("goTo", PlantelActivity::class.java)

            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Error al eliminar profesor", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}