package com.appminds.clubdeportivo.actividad

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.models.ActividadDto
import com.appminds.clubdeportivo.profesor.ProfesorConfirmActivity

class UpdateActividadActivity : AppCompatActivity() {

    private lateinit var inputName: EditText
    private lateinit var inputDays: EditText
    private lateinit var inputTime: EditText
    private lateinit var inputPrice: EditText
    private lateinit var inputCapacity: EditText
    private lateinit var btnSave: AppCompatButton

    private var original: ActividadDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_actividad)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        btnSave = findViewById(R.id.btnSaveChanges)
        inputName = findViewById(R.id.inputName)
        inputDays = findViewById(R.id.inputDays)
        inputTime = findViewById(R.id.inputTime)
        inputPrice = findViewById(R.id.inputPrice)
        inputCapacity = findViewById(R.id.inputCapacity)

        val actividad = intent.getSerializableExtra("actividad") as? ActividadDto
            ?: run { finish(); return }

        original = actividad.copy()

        // Prefill
        inputName.setText(actividad.name)
        inputDays.setText(actividad.days)
        inputTime.setText(actividad.time)
        inputPrice.setText(actividad.price)
        inputCapacity.setText(actividad.capacity)

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { checkForChangesAndValidity() }
        }

        listOf(inputName, inputDays, inputTime, inputPrice, inputCapacity)
            .forEach { it.addTextChangedListener(watcher) }

        checkForChangesAndValidity()

        btnSave.setOnClickListener {
            // TODO: persistir cambios (Room/API). Por ahora solo confirmación.
            val i = Intent(this, ProfesorConfirmActivity::class.java).apply {
                putExtra("message", "¡Actividad modificada con éxito!")
                putExtra("labelBtn", "Volver al listado")
                putExtra("goTo", AdministrarActividades::class.java)
            }
            startActivity(i)
            finish()
        }
    }

    private fun checkForChangesAndValidity() {
        val fieldsOk = listOf(
            inputName.text.toString().trim(),
            inputDays.text.toString().trim(),
            inputTime.text.toString().trim(),
            inputPrice.text.toString().trim(),
            inputCapacity.text.toString().trim()
        ).all { it.isNotBlank() }

        val changed =
            original?.name != inputName.text.toString().trim() ||
                    original?.days != inputDays.text.toString().trim() ||
                    original?.time != inputTime.text.toString().trim() ||
                    original?.price != inputPrice.text.toString().trim() ||
                    original?.capacity != inputCapacity.text.toString().trim()

        btnSave.isEnabled = fieldsOk && changed
    }
}
