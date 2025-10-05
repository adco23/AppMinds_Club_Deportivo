package com.appminds.clubdeportivo.clients

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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.appminds.clubdeportivo.MainMenuActivity
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.models.ClientDto
import com.appminds.clubdeportivo.models.enums.ClientStatusEnum
import com.appminds.clubdeportivo.models.enums.ClientTypeEnum
import com.appminds.clubdeportivo.profesor.AddProfesorActivity
import com.appminds.clubdeportivo.profesor.ProfesorConfirmActivity

class AddClientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_client)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val firstname: EditText = findViewById(R.id.etClientName)
        val lastname: EditText = findViewById(R.id.etClientLastname)
        val dni: EditText = findViewById(R.id.etClientDNI)
        val address: EditText = findViewById(R.id.etClientAddress)
        val phone: EditText = findViewById(R.id.etClientPhone)
        val email: EditText = findViewById(R.id.etClientEmail)
        val cbSubmitDoc: AppCompatCheckBox = findViewById(R.id.cbSubmitDoc)
        val cbClientType: AppCompatCheckBox = findViewById(R.id.cbClientType)

        val btnSaveClient = findViewById<AppCompatButton>(R.id.btnSaveClient)

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkForm(firstname, lastname, dni, address, phone, email, cbSubmitDoc, btnSaveClient)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        firstname.addTextChangedListener(watcher)
        lastname.addTextChangedListener(watcher)
        dni.addTextChangedListener(watcher)
        address.addTextChangedListener(watcher)
        phone.addTextChangedListener(watcher)
        email.addTextChangedListener(watcher)
        cbSubmitDoc.setOnCheckedChangeListener { _, _ -> checkForm(firstname, lastname, dni, address, phone, email, cbSubmitDoc, btnSaveClient) }

        btnSaveClient.setOnClickListener {
            if (btnSaveClient.isEnabled) {
                val newClient = ClientDto(
                    firstname.text.toString().trim(),
                    lastname.text.toString().trim(),
                    dni.text.toString().trim(),
                    address.text.toString().trim(),
                    phone.text.toString().trim(),
                    email.text.toString().trim(),
                    cbSubmitDoc.isChecked,
                    if(cbClientType.isChecked) ClientTypeEnum.SOCIO else ClientTypeEnum.NO_SOCIO,
                    ClientStatusEnum.PENDIENTE
                )
                val intent = Intent(this, ProfesorConfirmActivity::class.java)
                intent.putExtra("message", "¡Cliente registrado con éxito!")
                intent.putExtra("labelBtn", "Volver a menú")
                intent.putExtra("goTo", MainMenuActivity::class.java)

                startActivity(intent)
                finish()
            }
        }
    }

    private fun checkForm(
        firstname: EditText,
        lastname: EditText,
        dni: EditText,
        address: EditText,
        phone: EditText,
        email: EditText,
        cbSubmitDoc: AppCompatCheckBox,
        btnSaveClient: AppCompatButton
    ) {
        val allFields = firstname.text.isNotEmpty() &&
                lastname.text.isNotEmpty() &&
                dni.text.isNotEmpty() &&
                address.text.isNotEmpty() &&
                phone.text.isNotEmpty() &&
                email.text.isNotEmpty() &&
                cbSubmitDoc.isChecked

        btnSaveClient.isEnabled = allFields
    }
}