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
    private lateinit var inputTime: EditText
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
        inputTime = findViewById(R.id.inputTime)
        inputPrice = findViewById(R.id.inputPrice)
        inputCapacity = findViewById(R.id.inputCapacity)

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { checkForm() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        listOf(inputName, inputDay, inputTime, inputPrice, inputCapacity).forEach {
            it.addTextChangedListener(watcher)
        }

        btnNext.setOnClickListener {
            val act = AddActividadDto(
                inputName.text.toString().trim(),
                inputDay.text.toString().trim(),
                inputTime.text.toString().trim(),
                inputPrice.text.toString().trim(),
                inputCapacity.text.toString().trim()
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
            inputName.text, inputDay.text, inputTime.text, inputPrice.text, inputCapacity.text
        ).all { it.toString().trim().isNotBlank() }

        btnNext.isEnabled = filled
    }
}
