package com.appminds.clubdeportivo.clients

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Patterns
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.appminds.clubdeportivo.MainMenuActivity
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.data.dao.ClientDao
import com.appminds.clubdeportivo.data.model.ClientEntity
import com.appminds.clubdeportivo.models.enums.ClientStatusEnum
import com.appminds.clubdeportivo.models.enums.ClientTypeEnum
import com.appminds.clubdeportivo.profesor.ProfesorConfirmActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class AddClientActivity : AppCompatActivity() {
    private lateinit var clientDao: ClientDao
    private var isSaving = false

    // Views
    private lateinit var firstname: EditText
    private lateinit var lastname: EditText
    private lateinit var dni: EditText
    private lateinit var address: EditText
    private lateinit var phone: EditText
    private lateinit var email: EditText
    private lateinit var cbSubmitDoc: AppCompatCheckBox
    private lateinit var cbClientType: AppCompatCheckBox
    private lateinit var btnSaveClient: AppCompatButton
    private lateinit var progressBar: ProgressBar // Agregar al layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_client)

        clientDao = ClientDao(this)
        initViews()
        setupListeners()
    }

    private fun initViews() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        firstname = findViewById(R.id.etClientName)
        lastname = findViewById(R.id.etClientLastname)
        dni = findViewById(R.id.etClientDNI)
        address = findViewById(R.id.etClientAddress)
        phone = findViewById(R.id.etClientPhone)
        email = findViewById(R.id.etClientEmail)
        cbSubmitDoc = findViewById(R.id.cbSubmitDoc)
        cbClientType = findViewById(R.id.cbClientType)
        btnSaveClient = findViewById(R.id.btnSaveClient)
        // progressBar = findViewById(R.id.progressBar) // Descomenta si agregas al layout
    }

    private fun setupListeners() {
        val textWatcher: (Editable?) -> Unit = { validateForm() }

        firstname.addTextChangedListener(afterTextChanged = textWatcher)
        lastname.addTextChangedListener(afterTextChanged = textWatcher)
        dni.addTextChangedListener(afterTextChanged = textWatcher)
        address.addTextChangedListener(afterTextChanged = textWatcher)
        phone.addTextChangedListener(afterTextChanged = textWatcher)
        email.addTextChangedListener(afterTextChanged = textWatcher)
        cbSubmitDoc.setOnCheckedChangeListener { _, _ -> validateForm() }

        btnSaveClient.setOnClickListener { saveClient() }
    }

    private fun validateForm() {
        val isValid = firstname.text.isNotBlank() &&
                lastname.text.isNotBlank() &&
                isValidDNI(dni.text.toString()) &&
                address.text.isNotBlank() &&
                isValidPhone(phone.text.toString()) &&
                isValidEmail(email.text.toString()) &&
                cbSubmitDoc.isChecked

        btnSaveClient.isEnabled = isValid && !isSaving

        // Mostrar errores específicos
        if (email.text.isNotEmpty() && !isValidEmail(email.text.toString())) {
            email.error = "Email inválido"
        }
        if (dni.text.isNotEmpty() && !isValidDNI(dni.text.toString())) {
            dni.error = "DNI inválido (debe tener 8 dígitos)"
        }
        if (phone.text.isNotEmpty() && !isValidPhone(phone.text.toString())) {
            phone.error = "Teléfono inválido"
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }

    private fun isValidDNI(dni: String): Boolean {
        val trimmed = dni.trim()
        return trimmed.length == 8 && trimmed.all { it.isDigit() }
    }

    private fun isValidPhone(phone: String): Boolean {
        val trimmed = phone.trim()
        return trimmed.length >= 10 && trimmed.all { it.isDigit() || it == '+' || it == '-' || it == ' ' }
    }

    private fun saveClient() {
        if (isSaving) return

        isSaving = true
        btnSaveClient.isEnabled = false
        // progressBar.visibility = View.VISIBLE // Descomenta si agregas al layout

        lifecycleScope.launch {
            try {
                // Verificar duplicados en background thread
                val existingClient = withContext(Dispatchers.IO) {
                    clientDao.getClientByDNI(dni.text.toString().trim())
                }

                if (existingClient != null) {
                    Toast.makeText(
                        this@AddClientActivity, "Ya existe un cliente con este DNI", Toast.LENGTH_LONG ).show()
                    isSaving = false
                    btnSaveClient.isEnabled = true
                    // progressBar.visibility = View.GONE
                    return@launch
                }

                // Crear y guardar cliente
                val newClient = ClientEntity(
                    null,
                    firstname.text.toString().trim(),
                    lastname.text.toString().trim(),
                    dni.text.toString().trim(),
                    email.text.toString().trim(),
                    phone.text.toString().trim(),
                    address.text.toString().trim(),
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis()),
                    cbSubmitDoc.isChecked,
                    if (cbClientType.isChecked) ClientTypeEnum.SOCIO else ClientTypeEnum.NO_SOCIO,
                    ClientStatusEnum.PENDIENTE
                )

                val insertResult = withContext(Dispatchers.IO) {
                    clientDao.insert(newClient)
                }

                if (insertResult > 0) {
                    newClient.id = insertResult.toInt()
                    navigateToConfirmation(newClient)
                } else {
                    throw Exception("Error al guardar el cliente")
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@AddClientActivity,
                    "Error al guardar: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                isSaving = false
                btnSaveClient.isEnabled = true
                // progressBar.visibility = View.GONE
            }
        }
    }

    private fun navigateToConfirmation(client: ClientEntity) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_client_licence)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.findViewById<TextView>(R.id.tvCLId).text = client.id.toString()
        dialog.findViewById<TextView>(R.id.tvCLName).text = "${client.firstname} ${client.lastname}"
        dialog.findViewById<TextView>(R.id.tvCLType).text = client.type.toString()
        dialog.findViewById<TextView>(R.id.tvCLDate).text = client.registeredAt

        dialog.findViewById<Button>(R.id.btn_download)?.setOnClickListener {
            Toast.makeText(this, "Descargando...", Toast.LENGTH_SHORT).show()
        }
        dialog.findViewById<Button>(R.id.btn_continue)?.setOnClickListener {
            dialog.dismiss()

            val intent = Intent(this, ProfesorConfirmActivity::class.java).apply {
                putExtra("message", "¡Cliente registrado con éxito!")
                putExtra("labelBtn", "Volver a menú")
                putExtra("goTo", MainMenuActivity::class.java)
            }

            startActivity(intent)
            finish()
        }

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}