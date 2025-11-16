package com.appminds.clubdeportivo.pagos

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.data.dao.ClientDao
import com.appminds.clubdeportivo.data.dao.PagoDao
import com.appminds.clubdeportivo.data.model.ClientEntity
import com.appminds.clubdeportivo.data.model.CuotaEntity
import com.appminds.clubdeportivo.utils.formatTimestampToDateString
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PagarCuotaActivity : AppCompatActivity() {

    private lateinit var clientDao: ClientDao
    private lateinit var pagoDao: PagoDao

    // Variables de estado
    private var clientId: Int = -1
    private var currentClient: ClientEntity? = null
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    // Almacenar los timestamps calculados
    private var fechaPagoMillis: Long? = null
    private var fechaVencimientoMillis: Long? = null

    private lateinit var btnPagar: AppCompatButton
    private lateinit var inputMonto: EditText
    private lateinit var inputFechaPago: EditText
    private lateinit var inputFechaVto: EditText

    // Vistas de Cliente
    private lateinit var tvClientIdDisplay: TextView
    private lateinit var tvClientNameDisplay: TextView

    // Checkboxes (Forma de Pago)
    private lateinit var cbIsEfectivo: AppCompatCheckBox
    private lateinit var cbIsTarjeta1: AppCompatCheckBox
    private lateinit var cbIsTarjeta2: AppCompatCheckBox
    private lateinit var cbIsTarjeta6: AppCompatCheckBox

    // Agrupación de Checkboxes para la lógica de selección única
    private lateinit var formaPagoCheckboxes: List<AppCompatCheckBox>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagar_cuota)

        clientDao = ClientDao(this)
        pagoDao = PagoDao(this)

        initData()
        initViews()
        setUpListeners()
        loadClientData()
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }

    // --- FUNCIONES DE INICIALIZACIÓN Y CARGA DE DATOS ---

    private fun initData() {
        clientId = intent.getIntExtra("CLIENT_ID", -1)

        if (clientId == -1) {
            Toast.makeText(this, "Error: ID de cliente no proporcionado.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initViews() {
        btnPagar = findViewById(R.id.btnPagar)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        // Inicialización de Views de Datos
        inputMonto = findViewById(R.id.inputMonto)
        inputFechaPago = findViewById(R.id.inputFechaPago)
        inputFechaVto = findViewById(R.id.inputFechaVto)
        // Solo lectura
        inputFechaPago.keyListener = null
        inputFechaVto.keyListener = null
        inputFechaVto.isFocusable = false
        // Mostrar el ID y Nombre
        tvClientIdDisplay = findViewById(R.id.tvClientIdDisplay)
        tvClientNameDisplay = findViewById(R.id.tvClientNameDisplay)

        // Inicialización de Checkboxes
        cbIsEfectivo = findViewById(R.id.cbIsEfectivo)
        cbIsTarjeta1 = findViewById(R.id.cbIsTarjeta1)
        cbIsTarjeta2 = findViewById(R.id.cbIsTarjeta3)
        cbIsTarjeta6 = findViewById(R.id.cbIsTarjeta6)

        // Agrupar Checkboxes
        formaPagoCheckboxes = listOf(cbIsEfectivo, cbIsTarjeta1, cbIsTarjeta2, cbIsTarjeta6)
    }

    /**
     * Muestra el selector de fechas y actualiza los campos de Pago
     */
    private fun showDatePickerDialog() {
        // ⭐ Zona horaria de Argentina
        val argentinaTimeZone = TimeZone.getTimeZone("America/Argentina/Buenos_Aires")
        val calendar = Calendar.getInstance(argentinaTimeZone)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // 1. Crear el Timestamp para la Fecha de Pago (normalizado a medianoche en hora Argentina)
                val calendarPago = Calendar.getInstance(argentinaTimeZone).apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    // ⭐ CLAVE: Normalizar a medianoche para evitar inconsistencias
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                fechaPagoMillis = calendarPago.timeInMillis

                // 2. Calcular el Vencimiento (30 días después)
                val calendarVto = Calendar.getInstance(argentinaTimeZone).apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    // ⭐ Sumar 30 días
                    add(Calendar.DAY_OF_YEAR, 30)
                }
                fechaVencimientoMillis = calendarVto.timeInMillis

                // Log para debugging
                Log.i("DatePicker", "Fecha Pago MS: $fechaPagoMillis")
                Log.i("DatePicker", "Fecha Vto MS: $fechaVencimientoMillis")

                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                Log.i("DatePicker", "Fecha Pago ISO: ${sdf.format(fechaPagoMillis!!)}")
                Log.i("DatePicker", "Fecha Vto ISO: ${sdf.format(fechaVencimientoMillis!!)}")

                // 3. Formatear las fechas a String legible (DD/MM/AAAA)
                val fechaPagoLegible = formatTimestampToDateString(fechaPagoMillis!!)
                val fechaVtoLegible = formatTimestampToDateString(fechaVencimientoMillis!!)

                // 4. Actualizar los EditText de la UI
                inputFechaPago.setText(fechaPagoLegible)
                inputFechaVto.setText(fechaVtoLegible)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun setUpListeners() {
        // Lógica de Checkbox: solo se puede seleccionar uno
        formaPagoCheckboxes.forEach { checkbox ->
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // Desmarcar todos los demás
                    formaPagoCheckboxes.filter { it != buttonView }.forEach { it.isChecked = false }
                }
            }
        }

        // LISTENER PARA ABRIR EL CALENDARIO al hacer clic en el campo de fecha de pago
        inputFechaPago.setOnClickListener { showDatePickerDialog() }

        // También al recibir el foco
        inputFechaPago.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDatePickerDialog()
            }
        }

        // Listener para el botón de Confirmar Pago
        btnPagar.setOnClickListener { handleFinalPayment() }
    }

    private fun loadClientData() {
        executor.execute {
            val clientEntity = clientDao.getClientByID(clientId)

            runOnUiThread {
                if (clientEntity != null) {
                    currentClient = clientEntity
                    Toast.makeText(this, "Socio: ${clientEntity.firstname} cargado.", Toast.LENGTH_SHORT).show()
                    displayClientInfo(clientEntity)
                } else {
                    Toast.makeText(this, "Error: Cliente con ID $clientId no válido.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    private fun displayClientInfo(client: ClientEntity) {
        tvClientIdDisplay.text = "ID: ${client.id}"
        tvClientNameDisplay.text = "${client.firstname} ${client.lastname}"
    }

    // --- LÓGICA DE NEGOCIO Y TRANSACCIÓN ---

    /**
     * Obtiene la forma de pago seleccionada del grupo de Checkboxes.
     */
    private fun getSelectedFormaPago(): String? {
        return formaPagoCheckboxes.find { it.isChecked }?.text?.toString()
    }

    /**
     * Maneja el clic de pagar cuota, ejecutando la transacción.
     */
    private fun handleFinalPayment() {
        val cliente = currentClient

        // 1. Captura de Datos y Validaciones
        val monto = inputMonto.text.toString().toDoubleOrNull()
        val formaPago = getSelectedFormaPago()

        if (cliente == null || cliente.id == null) {
            Toast.makeText(this, "Error: Cliente no cargado.", Toast.LENGTH_SHORT).show()
            return
        }
        if (monto == null || monto <= 0) {
            Toast.makeText(this, "Debe ingresar un monto válido.", Toast.LENGTH_SHORT).show()
            return
        }
        if (formaPago == null) {
            Toast.makeText(this, "Debe seleccionar una forma de pago.", Toast.LENGTH_SHORT).show()
            return
        }

        // ⭐ Validar que las fechas hayan sido seleccionadas
        if (fechaPagoMillis == null || fechaVencimientoMillis == null) {
            Toast.makeText(this, "Debe seleccionar una fecha de pago válida.", Toast.LENGTH_LONG).show()
            return
        }

        // 2. Preparación de Entidad con los timestamps ya calculados
        val nuevaCuota = CuotaEntity(
            clienteId = cliente.id!!,
            fechaVencimiento = fechaVencimientoMillis!!,
            fechaPago = fechaPagoMillis!!,
            monto = monto,
            formaPago = formaPago,
            promocion = ""
        )

        // Log final antes de guardar
        Log.i("PaymentTransaction", "Guardando fecha vencimiento: ${nuevaCuota.fechaVencimiento}")
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        Log.i("PaymentTransaction", "Fecha Vto ISO: ${sdf.format(nuevaCuota.fechaVencimiento)}")

        // 3. Ejecutar la TRANSACCIÓN en el hilo de fondo
        executePaymentTransaction(
            nuevaCuota = nuevaCuota,
            isSuccessful = { pagoDao.registrarPagoCuota(nuevaCuota, fechaVencimientoMillis!!) },
            successMessage = "Cuota registrada y Socio actualizado a Activo.",
            nextActivity = PagoConfirmActivity::class.java
        )
    }

    /**
     * Función para manejar la ejecución del DAO en el hilo de fondo (Executor).
     */
    private fun executePaymentTransaction(
        nuevaCuota: CuotaEntity,
        isSuccessful: () -> Boolean,
        successMessage: String,
        nextActivity: Class<*>
    ) {
        executor.execute {
            val result = isSuccessful()

            runOnUiThread {
                if (result) {
                    Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show()
                    val intent = Intent(this, nextActivity).apply {
                        putExtra("CLIENTE_ID", nuevaCuota.clienteId)
                        putExtra("MONTO", nuevaCuota.monto)
                        putExtra("FORMA_PAGO", nuevaCuota.formaPago)
                        putExtra("FECHA_PAGO_MILLIS", nuevaCuota.fechaPago)
                        putExtra("FECHA_VTO_MILLIS", nuevaCuota.fechaVencimiento)
                        putExtra("ES_CUOTA", true)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error en la transacción de cuota. El socio NO fue actualizado.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}