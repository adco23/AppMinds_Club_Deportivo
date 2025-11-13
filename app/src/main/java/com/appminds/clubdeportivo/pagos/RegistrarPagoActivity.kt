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

    // 💡 Variable de estado para guardar la entidad encontrada (de la DB)
    private var foundClient: ClientEntity? = null

    // Views de Búsqueda y Resultados
    private lateinit var searchInput: EditText
    private lateinit var searchBtn: AppCompatButton
    private lateinit var tlClientData: TableLayout

    // Vistas de la tabla (Para rellenar los datos del cliente)
    private lateinit var tvClientId: TextView
    private lateinit var tvClientName: TextView
    private lateinit var tvClientType: TextView
    private lateinit var tvClientStatus: TextView

    // Vistas de Navegación/Acción
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
        // Vistas de Búsqueda (Igual a tu modelo)
        searchInput = findViewById(R.id.inputSearchClient)
        searchBtn = findViewById(R.id.btnSearchClient)
        tlClientData = findViewById(R.id.tlClientData)

        // Vistas de la tabla (Datos del cliente)
        tvClientId = findViewById(R.id.tvClientIdP)
        tvClientName = findViewById(R.id.tvClientNameP)
        tvClientType = findViewById(R.id.tvClientTypeP)
        tvClientStatus = findViewById(R.id.tvClientStatusP)

        // Vistas de Navegación/Acción
        btnPagarCuota = findViewById (R.id.btnPagarCuota)
        btnPagarActividad = findViewById (R.id.btnPagarActividad)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun setUpListeners() {
        // Lógica de habilitación del botón idéntica a tu modelo
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
            // Cliente no encontrado: ocultar todo
            tlClientData.isVisible = false
            btnPagarCuota.isVisible = false
            btnPagarActividad.isVisible = false
            foundClient = null
            Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        // 💡 ASIGNACIÓN DE DATOS
        foundClient = client

        // Aquí usamos !! si client.id es Int?
        val clientIdSeguro = client.id // Si client.id es Int?, aquí lanza el error.

        // 💡 SOLUCIÓN: Usar el ID con !! para la UI y la Navegación.

        // Rellenar vistas
        tvClientId.text = client.id!!.toString() // Usamos !! en el ID para asegurar Int
        tvClientName.text = "${client.firstname} ${client.lastname}"
        tvClientType.text = client.type.toString()
        tvClientStatus.text = client.status.toString()

        // Mostrar tabla
        tlClientData.isVisible = true

        // Lógica de negocio: Mostrar botones condicionalmente
        val clientType = client.type
        btnPagarCuota.isVisible = clientType == ClientTypeEnum.SOCIO
        btnPagarActividad.isVisible = clientType == ClientTypeEnum.NO_SOCIO

        // 💡 CORRECCIÓN DE LA LLAMADA FINAL:
        // Aseguramos que el ID que pasamos a la función de navegación NO sea nulo.
        setupPaymentNavigationListeners(client.id!!, clientType)
    }

    private fun setupPaymentNavigationListeners(clientId: Int, clientType: ClientTypeEnum) {
        // El operador '::class.java' es la referencia a la clase para el Intent
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
            // Pasamos el ID del cliente a la siguiente Activity
            putExtra("CLIENT_ID", clientId)
        }
        startActivity(intent)
    }
}