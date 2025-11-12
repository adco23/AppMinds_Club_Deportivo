package com.appminds.clubdeportivo.profesor

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
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.appminds.clubdeportivo.MainMenuActivity
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.data.dao.ProfesorDao
import com.appminds.clubdeportivo.data.model.ProfesorEntity
import com.appminds.clubdeportivo.models.AddProfesorDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddProfesorActivity : AppCompatActivity() {
    private lateinit var profesorDao: ProfesorDao
    private var isSaving = false

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

        profesorDao = ProfesorDao(this)
        initViews()
        setupListeners()
    }

    private fun initViews() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        btnNext = findViewById(R.id.btnAddProfNext)
        inputFirstname = findViewById(R.id.inputFirstname)
        inputLastname = findViewById(R.id.inputLastname)
        inputDNI = findViewById(R.id.inputDNI)
        inputAddress = findViewById(R.id.inputAddress)
        inputPhone = findViewById(R.id.inputPhone)
        cbIsSubstitute = findViewById(R.id.cbIsSubstitute)
    }

    private fun setupListeners() {
        val textWatcher: (Editable?) -> Unit = { checkForm() }

        inputFirstname.addTextChangedListener(afterTextChanged = textWatcher)
        inputLastname.addTextChangedListener(afterTextChanged = textWatcher)
        inputDNI.addTextChangedListener(afterTextChanged = textWatcher)
        inputAddress.addTextChangedListener(afterTextChanged = textWatcher)
        inputPhone.addTextChangedListener(afterTextChanged = textWatcher)

        btnNext.setOnClickListener { saveProfesor() }
    }

    private fun checkForm() {
        val fields = listOf(
            inputFirstname.text.toString().trim(),
            inputLastname.text.toString().trim(),
            inputDNI.text.toString().trim(),
            inputAddress.text.toString().trim(),
            inputPhone.text.toString().trim()
        )

        btnNext.isEnabled = !isSaving && fields.all { it.isNotBlank() }

        if (inputDNI.text.isNotEmpty() && !isValidDNI(inputDNI.text.toString())) {
            inputDNI.error = "DNI inválido (debe tener 8 dígitos)"
        }
        if (inputPhone.text.isNotEmpty() && !isValidPhone(inputPhone.text.toString())) {
            inputPhone.error = "Teléfono inválido"
        }
    }

    private fun isValidDNI(dni: String): Boolean {
        val trimmed = dni.trim()
        return trimmed.length == 8 && trimmed.all { it.isDigit() }
    }

    private fun isValidPhone(phone: String): Boolean {
        val trimmed = phone.trim()
        return trimmed.length >= 10 && trimmed.all { it.isDigit() || it == '+' || it == '-' || it == ' ' }
    }

    private fun saveProfesor() {
        if (isSaving) return

        isSaving = true
        btnNext.isEnabled = false

        lifecycleScope.launch {
            try {
                val existingProfesor = withContext(Dispatchers.IO) {
                    profesorDao.getByDNI(inputDNI.text.toString().trim())
                }

                if (existingProfesor != null) {
                    Toast.makeText(this@AddProfesorActivity, "Ya existe un profesor con este DNI", Toast.LENGTH_LONG ).show()
                    isSaving = false
                    btnNext.isEnabled = true
                    return@launch
                }

                val profesor = ProfesorEntity( null,
                    inputFirstname.text.toString().trim(),
                    inputLastname.text.toString().trim(),
                    inputDNI.text.toString().trim(),
                    inputAddress.text.toString().trim(),
                    inputPhone.text.toString().trim(),
                    cbIsSubstitute.isChecked
                )


                val intent = Intent(this@AddProfesorActivity, AddProfesorStep2Activity::class.java).apply { putExtra("profesor", profesor) }
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                Toast.makeText(this@AddProfesorActivity, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}