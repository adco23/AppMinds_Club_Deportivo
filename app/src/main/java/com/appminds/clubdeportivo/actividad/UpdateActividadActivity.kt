package com.appminds.clubdeportivo.actividad

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.data.dao.ActividadDao
import com.appminds.clubdeportivo.data.model.ActividadEntity
import com.appminds.clubdeportivo.profesor.ProfesorConfirmActivity

class UpdateActividadActivity : AppCompatActivity() {

    private lateinit var inputName: EditText
    private lateinit var inputDays: EditText
    private lateinit var inputStartTime: EditText
    private lateinit var inputEndTime: EditText
    private lateinit var inputPrice: EditText
    private lateinit var inputCapacity: EditText
    private lateinit var btnSave: AppCompatButton

    private var original: ActividadEntity? = null
    private lateinit var actividadDao: ActividadDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_actividad)

        actividadDao = ActividadDao(this)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        btnSave = findViewById(R.id.btnSaveChanges)
        inputName = findViewById(R.id.inputName)
        inputDays = findViewById(R.id.inputDays)
        inputStartTime = findViewById(R.id.inputStartTime)
        inputEndTime = findViewById(R.id.inputEndTime)
        inputPrice = findViewById(R.id.inputPrice)
        inputCapacity = findViewById(R.id.inputCapacity)

        val actividad = intent.getSerializableExtra("actividad") as? ActividadEntity
            ?: run { finish(); return }

        original = actividad.copy()

        inputName.setText(actividad.name)
        inputDays.setText(actividad.days)
        inputStartTime.setText(actividad.startTime)
        inputEndTime.setText(actividad.endTime)
        inputPrice.setText(actividad.price.toString())
        inputCapacity.setText(actividad.capacity.toString())

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { checkForChangesAndValidity() }
        }

        listOf(inputName, inputDays, inputStartTime, inputEndTime, inputPrice, inputCapacity)
            .forEach { it.addTextChangedListener(watcher) }

        checkForChangesAndValidity()

        btnSave.setOnClickListener {
            val price = inputPrice.text.toString().trim().toDoubleOrNull()
            val capacity = inputCapacity.text.toString().trim().toIntOrNull()

            if (price == null) {
                inputPrice.error = "Precio inválido"
                return@setOnClickListener
            }
            if (capacity == null) {
                inputCapacity.error = "Cupo inválido"
                return@setOnClickListener
            }

            val current = original ?: return@setOnClickListener
            val updated = current.copy(
                name = inputName.text.toString().trim(),
                days = inputDays.text.toString().trim(),
                startTime = inputStartTime.text.toString().trim(),
                endTime = inputEndTime.text.toString().trim(),
                price = price,
                capacity = capacity
            )

            val rows = actividadDao.update(updated)
            if (rows > 0) {
                val i = Intent(this, ProfesorConfirmActivity::class.java).apply {
                    putExtra("message", "¡Actividad modificada con éxito!")
                    putExtra("labelBtn", "Volver al listado")
                    putExtra("goTo", AdministrarActividades::class.java)
                }
                startActivity(i)
                finish()
            } else {
                Toast.makeText(this, "No se pudo actualizar la actividad", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkForChangesAndValidity() {
        val fieldsOk = listOf(
            inputName.text.toString().trim(),
            inputDays.text.toString().trim(),
            inputStartTime.text.toString().trim(),
            inputEndTime.text.toString().trim(),
            inputPrice.text.toString().trim(),
            inputCapacity.text.toString().trim()
        ).all { it.isNotBlank() }

        val o = original
        val changed = o != null && (
                o.name != inputName.text.toString().trim() ||
                        o.days != inputDays.text.toString().trim() ||
                        o.startTime != inputStartTime.text.toString().trim() ||
                        o.endTime != inputEndTime.text.toString().trim() ||
                        o.price.toString() != inputPrice.text.toString().trim() ||
                        o.capacity.toString() != inputCapacity.text.toString().trim()
                )

        btnSave.isEnabled = fieldsOk && changed
    }
}
