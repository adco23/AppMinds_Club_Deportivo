package com.appminds.clubdeportivo.pagos

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.data.dao.ClientDao
import com.appminds.clubdeportivo.data.dao.PagoDao
import com.appminds.clubdeportivo.data.model.ClientEntity
import com.appminds.clubdeportivo.data.model.CuotaEntity
import java.util.Calendar
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService
import java.text.SimpleDateFormat
import java.util.Locale
import com.appminds.clubdeportivo.utils.formatTimestampToDateString

class PagarCuotaActivity : AppCompatActivity() {

    private lateinit var clientDao: ClientDao
    private lateinit var pagoDao: PagoDao

    // Variables de estado
    private var clientId: Int = -1
    private var currentClient: ClientEntity? = null
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

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
        val calendar = Calendar.getInstance()

        // Configura el diálogo con la fecha actual como valor por defecto
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // La fecha seleccionada se devuelve aquí (month es 0-11)

                // 1. Crear el Timestamp para la Fecha de Pago
                calendar.set(year, month, dayOfMonth)
                val fechaPagoMillis = calendar.timeInMillis

                // 2. Calcular el Vencimiento (30 días después)
                val fechaVencimientoMillis = calculateVtoTimestamp(fechaPagoMillis)

                // 3. Formatear las fechas a String legible (DD/MM/AAAA)
                val fechaPagoLegible = formatTimestampToDateString(fechaPagoMillis)
                val fechaVtoLegible = formatTimestampToDateString(fechaVencimientoMillis)

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
        // También es buena práctica que se abra al recibir el foco (opcional)
        inputFechaPago.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDatePickerDialog()
            }
            // Listener para el botón de Confirmar Pago
            btnPagar.setOnClickListener { handleFinalPayment() }
        }
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
        // 💡 Muestra el ID del cliente
        tvClientIdDisplay.text = "ID: ${client.id}"

        // 💡 Muestra el Nombre y Apellido
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
     * Convierte un String de fecha (ej: "20/12/2025") a un Long (timestamp).
     */
    private fun convertDateToTimestamp(dateString: String): Long? {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            dateFormat.parse(dateString)?.time
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Calcula la fecha de vencimiento (30 días después de la fecha de pago).
     */
    private fun calculateVtoTimestamp(fechaPagoMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaPagoMillis
        calendar.add(Calendar.DAY_OF_YEAR, 30)
        return calendar.timeInMillis
    }

    /**
     * Maneja el clic de pagar cuota, ejecutando la transacción.
     */
    private fun handleFinalPayment() {
        val cliente = currentClient

        // 1. Captura de Datos y Validaciones
        val monto = inputMonto.text.toString().toDoubleOrNull()
        val formaPago = getSelectedFormaPago()
        val fechaPagoText = inputFechaPago.text.toString()

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

        // 💡 CONVERSIÓN DE FECHA
        val fechaPagoMillis = convertDateToTimestamp(fechaPagoText) // Usamos la función de conversión

        if (fechaPagoMillis == null) {
            Toast.makeText(this, "Debe seleccionar una fecha de pago válida.", Toast.LENGTH_LONG).show()
            return // Detener la ejecución si la fecha es inválida
        }

        // 2. Preparación de Entidad
        // El cálculo de vencimiento debe usar el Long que acabamos de obtener
        val fechaVencimiento = calculateVtoTimestamp(fechaPagoMillis)

        val nuevaCuota = CuotaEntity(
            clienteId = cliente.id!!, // Aseguramos que el ID no sea nulo
            fechaVencimiento = fechaVencimiento,
            fechaPago = fechaPagoMillis,
            monto = monto,
            formaPago = formaPago,
            promocion = ""
        )

        // 3. Ejecutar la TRANSACCIÓN en el hilo de fondo
        executePaymentTransaction(nuevaCuota,
            isSuccessful = { pagoDao.registrarPagoCuota(nuevaCuota, fechaVencimiento) },
            successMessage = "Cuota registrada y Socio actualizado a Activo.",
            nextActivity = PagoConfirmActivity::class.java
        )
    }

    /**
     * Función para manejar la ejecución del DAO en el hilo de fondo (Executor).
     */
    private fun executePaymentTransaction(nuevaCuota: CuotaEntity, isSuccessful: () -> Boolean, successMessage: String, nextActivity: Class<*>) {
        executor.execute {
            val result = isSuccessful()

            runOnUiThread {
                if (result) {
                    Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show()
                    val intent = Intent(this, nextActivity).apply {
                        // Datos esenciales:
                        putExtra("CLIENTE_ID", nuevaCuota.clienteId)
                        putExtra("MONTO", nuevaCuota.monto)
                        putExtra("FORMA_PAGO", nuevaCuota.formaPago)
                        putExtra("FECHA_PAGO_MILLIS", nuevaCuota.fechaPago)

                        // 💡 DATOS ESPECÍFICOS DE CUOTA:
                        putExtra("FECHA_VTO_MILLIS", nuevaCuota.fechaVencimiento)
                        putExtra("ES_CUOTA", true) // Indicador de concepto
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