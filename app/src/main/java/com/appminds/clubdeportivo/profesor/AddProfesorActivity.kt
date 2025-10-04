package com.appminds.clubdeportivo.profesor

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import com.appminds.clubdeportivo.MainMenuActivity
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.models.AddProfesorDto

class AddProfesorActivity : AppCompatActivity() {
    private lateinit var inputFirstname: EditText
    private lateinit var inputLastname: EditText
    private lateinit var inputDNI: EditText
    private lateinit var inputAddress: EditText
    private lateinit var inputPhone: EditText
    private lateinit var cbIsSubstitute: AppCompatCheckBox
    private lateinit var btnNext: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_profesor)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        btnNext = findViewById(R.id.btnAddProfNext)
        inputFirstname = findViewById(R.id.inputFirstname)
        inputLastname = findViewById(R.id.inputLastname)
        inputDNI = findViewById(R.id.inputDNI)
        inputAddress = findViewById(R.id.inputAddress)
        inputPhone = findViewById(R.id.inputPhone)
        cbIsSubstitute = findViewById(R.id.cbIsSubstitute)

        val watcher = object  : TextWatcher {
            override fun afterTextChanged(s: Editable?) { checkForm() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        listOf(inputFirstname, inputLastname, inputDNI, inputAddress, inputPhone).forEach {
            it.addTextChangedListener(watcher)
        }

        btnNext.setOnClickListener {
            val profesor = AddProfesorDto(
                inputFirstname.text.toString().trim(),
                inputLastname.text.toString().trim(),
                inputDNI.text.toString().trim(),
                inputAddress.text.toString().trim(),
                inputPhone.text.toString().trim(),
                cbIsSubstitute.isChecked
            )

            val intent = Intent(this, AddProfesorStep2Activity::class.java).apply { putExtra("profesor", profesor) }
            startActivity(intent)
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun checkForm() {
        val fields = listOf(
            inputFirstname.text.toString().trim(),
            inputLastname.text.toString().trim(),
            inputDNI.text.toString().trim(),
            inputAddress.text.toString().trim(),
            inputPhone.text.toString().trim()
        )

        btnNext.isEnabled = fields.all { it.isNotBlank() }
    }
}