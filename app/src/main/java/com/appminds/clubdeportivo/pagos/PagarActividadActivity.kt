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
import com.appminds.clubdeportivo.data.model.PagoActividadEntity
import com.appminds.clubdeportivo.utils.convertDateToTimestamp //
import com.appminds.clubdeportivo.utils.formatTimestampToDateString
import java.util.Calendar
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService



class PagarActividadActivity : AppCompatActivity() {

    private lateinit var clientDao: ClientDao
    private lateinit var pagoDao: PagoDao

    // Variables de estado
    private var clientId: Int = -1
    private var currentClient: ClientEntity? = null
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    // VISTAS DE PAGO DECLARADAS
    private lateinit var btnPagar: AppCompatButton
    private lateinit var btnBack: ImageButton
    private lateinit var inputMonto: EditText
    private lateinit var inputFechaPago: EditText
    private lateinit var inputActividadNombre: EditText

    // Vistas de Cliente
    private lateinit var tvClientIdDisplay: TextView
    private lateinit var tvClientNameDisplay: TextView

    // Checkboxes (Forma de Pago)
    private lateinit var cbIsEfectivo: AppCompatCheckBox
    private lateinit var cbIsTarjeta1: AppCompatCheckBox
    private lateinit var cbIsTarjeta3: AppCompatCheckBox
    private lateinit var cbIsTarjeta6: AppCompatCheckBox

    // Agrupación de Checkboxes para la lógica de selección única
    private lateinit var formaPagoCheckboxes: List<AppCompatCheckBox>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagar_actividad)

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
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        inputMonto = findViewById(R.id.inputMonto)
        inputFechaPago = findViewById(R.id.inputFechaPago)
        inputActividadNombre = findViewById(R.id.inputActividad)
        inputFechaPago.keyListener = null

        // Mostrar el ID y Nombre
        tvClientIdDisplay = findViewById(R.id.tvClientIdDisplay1)
        tvClientNameDisplay = findViewById(R.id.tvClientNameDisplay1)

        // Inicialización de Checkboxes
        cbIsEfectivo = findViewById(R.id.cbIsEfectivo)
        cbIsTarjeta1 = findViewById(R.id.cbIs1pago)
        cbIsTarjeta3 = findViewById(R.id.cbIs3pagos)
        cbIsTarjeta6 = findViewById(R.id.cbIs6pagos)

        // Agrupar Checkboxes
        formaPagoCheckboxes = listOf(cbIsEfectivo, cbIsTarjeta1, cbIsTarjeta3, cbIsTarjeta6)
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


                // 3. Formatear las fechas a String legible (DD/MM/AAAA)
                val fechaPagoLegible = formatTimestampToDateString(fechaPagoMillis)


                // 4. Actualizar los EditText de la UI
                inputFechaPago.setText(fechaPagoLegible)

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
        // Se abra al recibir el foco
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
                    Toast.makeText(this, "Cliente: ${clientEntity.firstname} cargado.", Toast.LENGTH_SHORT).show()
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


    // --- LÓGICA DE TRANSACCIÓN ---

    /**
     * Obtiene la forma de pago seleccionada del grupo de Checkboxes.
     */
    private fun getSelectedFormaPago(): String? {
        return formaPagoCheckboxes.find { it.isChecked }?.text?.toString()
    }


    private fun handleFinalPayment() {

        val cliente = currentClient

        val monto = inputMonto.text.toString().toDoubleOrNull()
        val formaPago = getSelectedFormaPago()
        val fechaPagoText = inputFechaPago.text.toString()
        val nombreActividadIngresado = inputActividadNombre.text.toString().trim()

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
        if (nombreActividadIngresado.isEmpty()) {
            Toast.makeText(this, "Debe ingresar el nombre de la actividad.", Toast.LENGTH_SHORT).show()
            return
        }

        // 💡 CONVERSIÓN DE FECHA
        val fechaPagoMillis = convertDateToTimestamp(fechaPagoText) // Usamos la función de conversión

        if (fechaPagoMillis == null) {
            Toast.makeText(this, "Debe seleccionar una fecha de pago válida.", Toast.LENGTH_LONG).show()
            return // Detener la ejecución si la fecha es inválida
        }

        val nuevoPago = PagoActividadEntity(
            idCliente = cliente.id!!,
            idActividad = 1, // TEMPORAL
            fechaPago = fechaPagoMillis,
            formaPago = formaPago
        )

        executePaymentTransaction(
            nuevoPago,
            monto,
            isSuccessful = { pagoDao.registrarPagoActividad(nuevoPago) },
            successMessage = "Pago de actividad registrado con éxito.",
            nextActivity = PagoConfirmActivity::class.java
        )
    }

    private fun executePaymentTransaction(nuevoPago: PagoActividadEntity, monto: Double, isSuccessful: () -> Boolean, successMessage: String, nextActivity: Class<*>) {
        executor.execute {
            val result = isSuccessful()

            runOnUiThread {
                if (result) {
                    Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show()
                    val intent = Intent(this, nextActivity).apply {
                        // Datos esenciales:
                        putExtra("CLIENTE_ID", nuevoPago.idCliente)
                        putExtra("MONTO", monto)
                        putExtra("FORMA_PAGO", nuevoPago.formaPago)
                        putExtra("FECHA_PAGO_MILLIS", nuevoPago.fechaPago)
                        putExtra("ES_CUOTA", false)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al registrar el pago de actividad. La inserción falló.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}