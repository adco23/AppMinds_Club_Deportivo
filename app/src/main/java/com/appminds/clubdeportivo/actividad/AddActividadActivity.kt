package com.appminds.clubdeportivo.actividad

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.models.AddActividadDto
import java.util.Locale

class AddActividadActivity : AppCompatActivity() {

    // Inputs
    private lateinit var inputName: EditText
    private lateinit var inputStartTime: EditText
    private lateinit var inputEndTime: EditText
    private lateinit var inputPrice: EditText
    private lateinit var inputCapacity: EditText
    private lateinit var btnNext: AppCompatButton

    // Checkboxes de días
    private lateinit var cbMon: CheckBox
    private lateinit var cbTue: CheckBox
    private lateinit var cbWed: CheckBox
    private lateinit var cbThu: CheckBox
    private lateinit var cbFri: CheckBox
    private lateinit var cbSat: CheckBox
    private lateinit var cbSun: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_actividad)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnNext = findViewById(R.id.btnAddActNext)

        // Inputs
        inputName = findViewById(R.id.inputName)
        inputStartTime = findViewById(R.id.inputStartTime)
        inputEndTime = findViewById(R.id.inputEndTime)
        inputPrice = findViewById(R.id.inputPrice)
        inputCapacity = findViewById(R.id.inputCapacity)

        // Checkboxes de días (IDs deben existir en el XML)
        cbMon = findViewById(R.id.cbMon)
        cbTue = findViewById(R.id.cbTue)
        cbWed = findViewById(R.id.cbWed)
        cbThu = findViewById(R.id.cbThu)
        cbFri = findViewById(R.id.cbFri)
        cbSat = findViewById(R.id.cbSat)
        cbSun = findViewById(R.id.cbSun)

        // Watcher para inputs de texto
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { checkForm() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        listOf(inputName, inputStartTime, inputEndTime, inputPrice, inputCapacity)
            .forEach { it.addTextChangedListener(watcher) }

        // Cuando cambia un checkbox, revalidamos el formulario
        getAllDayCheckboxes().forEach { cb ->
            cb.setOnCheckedChangeListener { _, _ -> checkForm() }
        }

        // TimePickers
        inputStartTime.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                inputStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m))
                checkForm()
            }, 9, 0, true).show()
        }

        inputEndTime.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                inputEndTime.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m))
                checkForm()
            }, 10, 0, true).show()
        }

        // Botón siguiente
        btnNext.setOnClickListener {
            val price = inputPrice.text.toString().trim().toDoubleOrNull()
            val capacity = inputCapacity.text.toString().trim().toIntOrNull()

            if (!hasAnyDaySelected()) {
                Toast.makeText(this, "Seleccioná al menos un día", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (price == null) {
                inputPrice.error = "Precio inválido"
                return@setOnClickListener
            }
            if (capacity == null) {
                inputCapacity.error = "Cupo inválido"
                return@setOnClickListener
            }
            if (!isValidTime(inputStartTime.text.toString().trim())) {
                inputStartTime.error = "Formato HH:mm"
                return@setOnClickListener
            }
            if (!isValidTime(inputEndTime.text.toString().trim())) {
                inputEndTime.error = "Formato HH:mm"
                return@setOnClickListener
            }

            val act = AddActividadDto(
                name = inputName.text.toString().trim(),
                days = buildDaysString(),   // "Lun, Mié, Vie"
                startTime = inputStartTime.text.toString().trim(),
                endTime = inputEndTime.text.toString().trim(),
                price = price,
                capacity = capacity
            )

            val intent = Intent(this, AddActividadStep2Activity::class.java).apply {
                putExtra("actividad", act)
            }
            startActivity(intent)
        }

        btnBack.setOnClickListener { finish() }
    }

    // ---- Helpers ----

    private fun buildDaysString(): String {
        val days = mutableListOf<String>()
        if (cbMon.isChecked) days.add("Lun")
        if (cbTue.isChecked) days.add("Mar")
        if (cbWed.isChecked) days.add("Mié")
        if (cbThu.isChecked) days.add("Jue")
        if (cbFri.isChecked) days.add("Vie")
        if (cbSat.isChecked) days.add("Sáb")
        if (cbSun.isChecked) days.add("Dom")
        return days.joinToString(", ")
    }

    private fun hasAnyDaySelected(): Boolean =
        getAllDayCheckboxes().any { it.isChecked }

    private fun getAllDayCheckboxes(): List<CheckBox> =
        listOf(cbMon, cbTue, cbWed, cbThu, cbFri, cbSat, cbSun)

    private fun checkForm() {
        val fieldsFilled = listOf(
            inputName.text,
            inputStartTime.text,
            inputEndTime.text,
            inputPrice.text,
            inputCapacity.text
        ).all { it.toString().trim().isNotBlank() }

        btnNext.isEnabled = fieldsFilled && hasAnyDaySelected()
    }

    private fun isValidTime(value: String): Boolean {
        val parts = value.split(":")
        if (parts.size != 2) return false
        val h = parts[0].toIntOrNull() ?: return false
        val m = parts[1].toIntOrNull() ?: return false
        return h in 0..23 && m in 0..59
    }
}
