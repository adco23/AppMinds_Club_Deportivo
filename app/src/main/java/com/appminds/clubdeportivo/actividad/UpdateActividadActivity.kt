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
import com.appminds.clubdeportivo.data.dao.ActividadDao
import com.appminds.clubdeportivo.data.model.ActividadEntity
import com.appminds.clubdeportivo.profesor.ProfesorConfirmActivity
import java.util.Locale

class UpdateActividadActivity : AppCompatActivity() {

    private lateinit var inputName: EditText
    private lateinit var inputStartTime: EditText
    private lateinit var inputEndTime: EditText
    private lateinit var inputPrice: EditText
    private lateinit var inputCapacity: EditText
    private lateinit var btnSave: AppCompatButton

    // Checkboxes
    private lateinit var cbMon: CheckBox
    private lateinit var cbTue: CheckBox
    private lateinit var cbWed: CheckBox
    private lateinit var cbThu: CheckBox
    private lateinit var cbFri: CheckBox
    private lateinit var cbSat: CheckBox
    private lateinit var cbSun: CheckBox

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
        inputStartTime = findViewById(R.id.inputStartTime)
        inputEndTime = findViewById(R.id.inputEndTime)
        inputPrice = findViewById(R.id.inputPrice)
        inputCapacity = findViewById(R.id.inputCapacity)

        cbMon = findViewById(R.id.cbMon)
        cbTue = findViewById(R.id.cbTue)
        cbWed = findViewById(R.id.cbWed)
        cbThu = findViewById(R.id.cbThu)
        cbFri = findViewById(R.id.cbFri)
        cbSat = findViewById(R.id.cbSat)
        cbSun = findViewById(R.id.cbSun)

        val actividad = intent.getSerializableExtra("actividad") as? ActividadEntity
            ?: run { finish(); return }

        original = actividad.copy()

        // Prefill
        inputName.setText(actividad.name)
        inputStartTime.setText(actividad.startTime)
        inputEndTime.setText(actividad.endTime)
        inputPrice.setText(actividad.price.toString())
        inputCapacity.setText(actividad.capacity.toString())
        setDaysFromString(actividad.days)

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { checkForChangesAndValidity() }
        }

        listOf(inputName, inputStartTime, inputEndTime, inputPrice, inputCapacity)
            .forEach { it.addTextChangedListener(watcher) }

        getAllDayCheckboxes().forEach { cb ->
            cb.setOnCheckedChangeListener { _, _ -> checkForChangesAndValidity() }
        }

        // TimePickers
        inputStartTime.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                inputStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m))
                checkForChangesAndValidity()
            }, 9, 0, true).show()
        }

        inputEndTime.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                inputEndTime.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m))
                checkForChangesAndValidity()
            }, 10, 0, true).show()
        }

        btnSave.setOnClickListener {
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

            val current = original ?: return@setOnClickListener
            val updated = current.copy(
                name = inputName.text.toString().trim(),
                days = buildDaysString(),
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

        checkForChangesAndValidity()
    }

    // Helpers de días (mismos que en AddActividadActivity)

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

    private fun setDaysFromString(days: String) {
        cbMon.isChecked = days.contains("Lun")
        cbTue.isChecked = days.contains("Mar")
        cbWed.isChecked = days.contains("Mié")
        cbThu.isChecked = days.contains("Jue")
        cbFri.isChecked = days.contains("Vie")
        cbSat.isChecked = days.contains("Sáb")
        cbSun.isChecked = days.contains("Dom")
    }

    private fun hasAnyDaySelected(): Boolean =
        getAllDayCheckboxes().any { it.isChecked }

    private fun getAllDayCheckboxes(): List<CheckBox> =
        listOf(cbMon, cbTue, cbWed, cbThu, cbFri, cbSat, cbSun)

    private fun checkForChangesAndValidity() {
        val fieldsOk = listOf(
            inputName.text.toString().trim(),
            inputStartTime.text.toString().trim(),
            inputEndTime.text.toString().trim(),
            inputPrice.text.toString().trim(),
            inputCapacity.text.toString().trim()
        ).all { it.isNotBlank() }

        val o = original
        val changed = o != null && (
                o.name != inputName.text.toString().trim() ||
                        o.days != buildDaysString() ||
                        o.startTime != inputStartTime.text.toString().trim() ||
                        o.endTime != inputEndTime.text.toString().trim() ||
                        o.price.toString() != inputPrice.text.toString().trim() ||
                        o.capacity.toString() != inputCapacity.text.toString().trim()
                )

        btnSave.isEnabled = fieldsOk && hasAnyDaySelected() && changed
    }

    private fun isValidTime(value: String): Boolean {
        val parts = value.split(":")
        if (parts.size != 2) return false
        val h = parts[0].toIntOrNull() ?: return false
        val m = parts[1].toIntOrNull() ?: return false
        return h in 0..23 && m in 0..59
    }
}
