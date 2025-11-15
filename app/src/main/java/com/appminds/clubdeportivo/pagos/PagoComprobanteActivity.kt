package com.appminds.clubdeportivo.pagos

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.data.dao.ClientDao
import com.appminds.clubdeportivo.utils.formatTimestampToDateString // Importar función de utilidad
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService

class PagoComprobanteActivity : AppCompatActivity() {

    private lateinit var clientDao: ClientDao
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    // VISTAS DEL COMPROBANTE DECLARADAS
    private lateinit var tvClienteId: TextView
    private lateinit var tvNombreyApellido: TextView
    private lateinit var tvConcepto: TextView
    private lateinit var tvMonto: TextView
    private lateinit var tvFechaPago: TextView
    private lateinit var btnDescargar: AppCompatButton
    private lateinit var btnGoToBack: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_comprobante)

        clientDao = ClientDao(this)

        initViews()
        loadPaymentData()
        setupListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }

    private fun initViews() {

        tvClienteId = findViewById(R.id.ClienteId)
        tvNombreyApellido = findViewById(R.id.NombreyApellido)
        tvConcepto = findViewById(R.id.Concepto)
        tvMonto = findViewById(R.id.Monto)
        tvFechaPago = findViewById(R.id.FechaPago)

        btnGoToBack = findViewById(R.id.btnGoToBack)
        btnDescargar = findViewById(R.id.btnDescargar)
    }

    private fun setupListeners() {
        btnGoToBack.setOnClickListener { finish() }

        btnDescargar.setOnClickListener {
            Toast.makeText(this, "Comprobante descargado con éxito.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadPaymentData() {
        val extras = intent.extras ?: return

        // 1. CAPTURA DE DATOS RECIBIDOS DEL INTENT
        val clienteId = extras.getInt("CLIENTE_ID", -1)
        val monto = extras.getDouble("MONTO", 0.0)
        val fechaPagoMillis = extras.getLong("FECHA_PAGO_MILLIS", 0L)
        val fechaVtoMillis = extras.getLong("FECHA_VTO_MILLIS", 0L) // Solo si es Cuota
        val esCuota = extras.getBoolean("ES_CUOTA", false)
        val nombreActividad = extras.getString("NOMBRE_ACTIVIDAD")

        // 2. DETERMINAR CONCEPTO
        val conceptoTexto = if (esCuota) {
            // Si es cuota, incluimos la fecha de vencimiento formateada
            val fechaVto = formatTimestampToDateString(fechaVtoMillis)
            "Cuota Mensual"
        } else {
            // Si es actividad, usamos el nombre de la actividad
            "Actividad Diaria: ${nombreActividad ?: "N/D"}"
        }

        // 3. ASIGNACIÓN A LA UI
        tvClienteId.text = "Cliente ID: $clienteId"
        tvConcepto.text = "Concepto: $conceptoTexto"
        tvMonto.text = "Monto: $${"%.2f".format(monto)}"

        if (fechaPagoMillis > 0) {
            val fechaPagoLegible = formatTimestampToDateString(fechaPagoMillis)
            tvFechaPago.text = "Fecha de Pago: $fechaPagoLegible"
        }

        // 4. BÚSQUEDA DEL NOMBRE

        if (clienteId != -1) {
            executor.execute {
                val clientEntity = clientDao.getClientByID(clienteId)

                runOnUiThread {
                    if (clientEntity != null) {
                        tvNombreyApellido.text = "Nombre y Apellido: ${clientEntity.firstname} ${clientEntity.lastname}"
                    } else {
                        tvNombreyApellido.text = "Nombre y Apellido: [Cliente no encontrado en BD]"
                    }
                }
            }
        }

    }
}