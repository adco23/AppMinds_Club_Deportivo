package com.appminds.clubdeportivo.pagos

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.data.dao.ClientDao
import com.appminds.clubdeportivo.data.model.ClientEntity // 💡 Asumo esta clase
import com.appminds.clubdeportivo.models.enums.ClientTypeEnum

class RegistrarPagoActivity : AppCompatActivity() {

    private lateinit var clientDao: ClientDao
    private var foundClient: ClientEntity? = null
    private lateinit var searchInput: EditText
    private lateinit var searchBtn: AppCompatButton
    private lateinit var tlClientData: TableLayout
    private lateinit var tvClientId: TextView
    private lateinit var tvClientName: TextView
    private lateinit var tvClientType: TextView
    private lateinit var tvClientStatus: TextView
    private lateinit var btnPagarCuota: AppCompatButton
    private lateinit var btnPagarActividad: AppCompatButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_pago)

        clientDao = ClientDao(this)

        initViews()
        setUpListeners()
    }

    private fun initViews() {
        searchInput = findViewById(R.id.inputSearchClient)
        searchBtn = findViewById(R.id.btnSearchClient)
        tlClientData = findViewById(R.id.tlClientData)
        tvClientId = findViewById(R.id.tvClientIdP)
        tvClientName = findViewById(R.id.tvClientNameP)
        tvClientType = findViewById(R.id.tvClientTypeP)
        tvClientStatus = findViewById(R.id.tvClientStatusP)
        btnPagarCuota = findViewById (R.id.btnPagarCuota)
        btnPagarActividad = findViewById (R.id.btnPagarActividad)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun setUpListeners() {
        val textWatcher: (Editable?) -> Unit = {
            searchBtn.isEnabled = searchInput.text.toString().trim().isNotEmpty()
        }
        searchInput.addTextChangedListener(afterTextChanged = textWatcher)
        searchBtn.setOnClickListener { showDataClient() }
    }

    private fun showDataClient() {
        val dni = searchInput.text.toString().trim()
        val client: ClientEntity? = clientDao.getClientByDNI(dni)

        if(client == null) {
            // Cliente no encontrado: oculta todo
            tlClientData.isVisible = false
            btnPagarCuota.isVisible = false
            btnPagarActividad.isVisible = false
            foundClient = null
            Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        foundClient = client
        val clientIdSeguro = client.id
        // Rellenar vistas
        tvClientId.text = client.id!!.toString()
        tvClientName.text = "${client.firstname} ${client.lastname}"
        tvClientType.text = client.type.toString()
        tvClientStatus.text = client.status.toString()

        // Mostrar tabla
        tlClientData.isVisible = true

        // Mostrar botones condicionalmente
        val clientType = client.type
        btnPagarCuota.isVisible = clientType == ClientTypeEnum.SOCIO
        btnPagarActividad.isVisible = clientType == ClientTypeEnum.NO_SOCIO

        setupPaymentNavigationListeners(client.id!!, clientType)
    }

    private fun setupPaymentNavigationListeners(clientId: Int, clientType: ClientTypeEnum) {
        if (clientType == ClientTypeEnum.SOCIO) {
            btnPagarCuota.setOnClickListener {
                startPaymentActivity(PagarCuotaActivity::class.java, clientId)
            }
        }

        if (clientType == ClientTypeEnum.NO_SOCIO) {
            btnPagarActividad.setOnClickListener {
                startPaymentActivity(PagarActividadActivity::class.java, clientId)
            }
        }
    }

    private fun startPaymentActivity(activityClass: Class<*>, clientId: Int) {
        val intent = Intent(this, activityClass).apply {
            // Paso ID del cliente a la siguiente Activity
            putExtra("CLIENT_ID", clientId)
        }
        startActivity(intent)
    }
}