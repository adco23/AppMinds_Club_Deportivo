package com.appminds.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.appminds.clubdeportivo.models.ClientDto
import com.appminds.clubdeportivo.models.enums.ClientStatusEnum
import com.appminds.clubdeportivo.models.enums.ClientTypeEnum

class RegistrarPagoActivity : AppCompatActivity() {
    private val clientsMock = listOf(
        ClientDto("Lucía", "Gómez", "25678901", "Av. Belgrano 123, CABA", "1145678901", "lucia.gomez@mail.com", true, ClientTypeEnum.SOCIO, ClientStatusEnum.ACTIVO),
        ClientDto("Martín", "Ríos", "30123456", "Calle Mitre 456, Mendoza", "2617894560", "martin.rios@mail.com", true, ClientTypeEnum.NO_SOCIO, ClientStatusEnum.INACTIVO),
        ClientDto("Sofía", "Herrera", "28345678", "Av. Alem 789, Tucumán", "3816543210", "sofia.herrera@mail.com", true, ClientTypeEnum.SOCIO, ClientStatusEnum.ACTIVO),
        ClientDto("Carlos", "Méndez", "26987654", "Calle San Martín 321, Rosario", "3419876543", "carlos.mendez@mail.com", true, ClientTypeEnum.NO_SOCIO, ClientStatusEnum.INACTIVO),
        ClientDto("Valentina", "López", "29456789", "Av. Rivadavia 1590, Córdoba", "3511234567", "valentina.lopez@mail.com", true, ClientTypeEnum.SOCIO, ClientStatusEnum.ACTIVO)
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_pago)

        val searchInput = findViewById<EditText>(R.id.inputSearchClient)
        val searchBtn = findViewById<AppCompatButton>(R.id.btnSearchClient)
        val tlClientData = findViewById<TableLayout>(R.id.tlClientData)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnPagarCuota = findViewById<Button>(R.id.btnPagarCuota)
        val btnPagarActividad = findViewById<Button>(R.id.btnPagarActividad)

        val watcher = object  : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchBtn.isEnabled = searchInput.text.toString().trim().isNotEmpty()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        searchInput.addTextChangedListener(watcher)
        searchBtn.setOnClickListener {
            var searchText = searchInput.text.toString().trim()
            val client = clientsMock.find { it.dni.contains(searchText) }

            if (client != null) {
                findViewById<TextView>(R.id.tvClientIdP).setText(client.dni)
                findViewById<TextView>(R.id.tvClientNameP).setText("${client.firstname} ${client.lastname}")
                findViewById<TextView>(R.id.tvClientTypeP).setText(client.type.toString())
                findViewById<TextView>(R.id.tvClientStatusP).setText(client.status.toString())

                tlClientData.isVisible = true
                btnPagarCuota.isVisible = client.type == ClientTypeEnum.SOCIO
                btnPagarActividad.isVisible = client.type == ClientTypeEnum.NO_SOCIO
            } else {
                tlClientData.isVisible = true
                btnPagarCuota.isVisible = true
            }
        }

        btnBack.setOnClickListener { finish() }

        btnPagarCuota.setOnClickListener {
            val intent = Intent(this, PagarCuotaActivity::class.java)
            startActivity(intent)
        }

        btnPagarActividad.setOnClickListener {
            val intent = Intent(this, PagarActividadActivity::class.java)
            startActivity(intent)
        }


    }
}
