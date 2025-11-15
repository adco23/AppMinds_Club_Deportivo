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
import com.appminds.clubdeportivo.models.AddActividadDto

class AddActividadActivity : AppCompatActivity() {

    private lateinit var inputName: EditText
    private lateinit var inputDay: EditText
    private lateinit var inputStartTime: EditText
    private lateinit var inputEndTime: EditText
    private lateinit var inputPrice: EditText
    private lateinit var inputCapacity: EditText
    private lateinit var btnNext: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_actividad)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnNext = findViewById(R.id.btnAddActNext)

        inputName = findViewById(R.id.inputName)
        inputDay = findViewById(R.id.inputDay)
        inputStartTime = findViewById(R.id.inputStartTime)
        inputEndTime = findViewById(R.id.inputEndTime)
        inputPrice = findViewById(R.id.inputPrice)
        inputCapacity = findViewById(R.id.inputCapacity)

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { checkForm() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        listOf(inputName, inputDay, inputStartTime, inputEndTime, inputPrice, inputCapacity)
            .forEach { it.addTextChangedListener(watcher) }

        btnNext.setOnClickListener {
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
                days = inputDay.text.toString().trim(),
                startTime = inputStartTime.text.toString().trim(),
                endTime = inputEndTime.text.toString().trim(),
                price = price!!,
                capacity = capacity!!
            )

            val intent = Intent(this, AddActividadStep2Activity::class.java).apply {
                putExtra("actividad", act)
            }
            startActivity(intent)
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun checkForm() {
        val filled = listOf(
            inputName.text,
            inputDay.text,
            inputStartTime.text,
            inputEndTime.text,
            inputPrice.text,
            inputCapacity.text
        ).all { it.toString().trim().isNotBlank() }

        btnNext.isEnabled = filled
    }

    private fun isValidTime(value: String): Boolean {
        // chequeo simple de HH:mm
        val parts = value.split(":")
        if (parts.size != 2) return false
        val h = parts[0].toIntOrNull() ?: return false
        val m = parts[1].toIntOrNull() ?: return false
        return h in 0..23 && m in 0..59
    }
}
