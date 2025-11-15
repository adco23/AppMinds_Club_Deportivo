package com.appminds.clubdeportivo.profesor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.data.dao.ActividadDao
import com.appminds.clubdeportivo.data.dao.ProfesorDao
import com.appminds.clubdeportivo.data.model.ActividadEntity
import com.appminds.clubdeportivo.data.model.ProfesorEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddProfesorStep2Activity : AppCompatActivity() {
    // DAOs
    private val profesorDao by lazy { ProfesorDao(this) }
    private val actividadDao by lazy { ActividadDao(this) }

    // Datos
    private lateinit var profesor: ProfesorEntity
    private var activityList: List<ActividadEntity> = emptyList()
    private var selectedActivity: ActividadEntity? = null
    private var selectedActivityName: String? = null

    // Views
    private lateinit var btnSave: AppCompatButton
    private lateinit var spinner: Spinner
    private lateinit var activityContainer: LinearLayout
    private lateinit var txtSelectedActivity: TextView
    private lateinit var tvDaysValue: TextView
    private lateinit var tvStartTimeValue: TextView
    private lateinit var tvEndTimeValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_profesor_step2)

        extractProfesorFromIntent()
        initViews()
        loadActivities()
    }

    private fun extractProfesorFromIntent() {
        profesor = (intent.getSerializableExtra("profesor") as? ProfesorEntity)
            ?: run {
                showError("Error al obtener datos del profesor")
                finish()
                return
            }
    }

    private fun initViews() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        btnSave = findViewById(R.id.btnAddProfSave)
        spinner = findViewById(R.id.spinnerOptions)
        activityContainer = findViewById(R.id.ActivitySelectedContainer)
        txtSelectedActivity  = findViewById(R.id.ActivitySelected)
        tvDaysValue = findViewById(R.id.tvDaysValue)
        tvStartTimeValue = findViewById(R.id.tvStartTimeValue)
        tvEndTimeValue = findViewById(R.id.tvEndTimeValue)


        updateSaveButtonState(false)
        activityContainer.isVisible = false
    }

    private fun loadActivities() {
        lifecycleScope.launch {
            try {
                activityList = withContext(Dispatchers.IO) {
                    actividadDao.getAll()
                }

                if (activityList.isEmpty()) {
                    showNoActivitiesError()
                    return@launch
                }

                setupSpinner(activityList)
                setupSaveButton()

            } catch (e: Exception) {
                showError("Error al cargar actividades: ${e.message}")
            }
        }
    }

    private fun showNoActivitiesError() {
        Toast.makeText(
            this,
            "No hay actividades registradas. Primero registrá una actividad (Yoga, Fútbol, etc.).",
            Toast.LENGTH_LONG
        ).show()
        updateSaveButtonState(false)
    }

    private fun setupSpinner(activities: List<ActividadEntity>) {
        val spinnerItems = buildSpinnerItems(activities)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            spinnerItems
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinner.adapter = adapter
        spinner.onItemSelectedListener = createSpinnerListener()
    }

    private fun buildSpinnerItems(activities: List<ActividadEntity>): List<String> {
        return mutableListOf<String>().apply {
            add(SPINNER_HINT)
            addAll(activities.map { it.name })
        }
    }

    private fun createSpinnerListener() = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            val selection = parent?.getItemAtPosition(position)?.toString() ?: ""
            handleActivitySelection(selection)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            handleActivitySelection("")
        }
    }

    private fun handleActivitySelection(activityName: String) {
        val isValidSelection = activityName.isNotBlank() && activityName != SPINNER_HINT

        if (isValidSelection) {
            selectedActivityName = activityName
            selectedActivity = activityList.find { it.name === selectedActivityName }
            showActivityPreview(selectedActivity)
            updateSaveButtonState(true)
        } else {
            selectedActivityName = null
            hideActivityPreview()
            updateSaveButtonState(false)
        }
    }

    private fun showActivityPreview(activity: ActividadEntity?) {
        activityContainer.isVisible = true
        txtSelectedActivity.text = activity?.name
        tvDaysValue.text = activity?.days
        tvStartTimeValue.text = activity?.startTime
        tvEndTimeValue.text = activity?.endTime
    }

    private fun hideActivityPreview() {
        activityContainer.isVisible = false
    }

    private fun updateSaveButtonState(enabled: Boolean) {
        btnSave.isEnabled = enabled
    }

    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            if (!btnSave.isEnabled) return@setOnClickListener
            saveProfesor()
        }
    }

    private fun saveProfesor() {
        // Deshabilitar botón para evitar clicks múltiples
        updateSaveButtonState(false)

        lifecycleScope.launch {
            try {
                val profesorToSave = profesor.copy(activity = selectedActivity?.id)

                val profesorId = withContext(Dispatchers.IO) {
                    profesorDao.insert(profesorToSave)
                }

                if (profesorId > 0) {
                    navigateToConfirmation()
                } else {
                    throw ProfesorSaveException("No se pudo guardar el profesor")
                }

            } catch (e: ProfesorSaveException) {
                handleSaveError(e.message ?: "Error desconocido")
            } catch (e: Exception) {
                handleSaveError("Error inesperado: ${e.message}")
            }
        }
    }

    private fun handleSaveError(message: String) {
        showError(message)
        updateSaveButtonState(true)
    }

    private fun navigateToConfirmation() {
        val intent = Intent(this, ProfesorConfirmActivity::class.java).apply {
            putExtra("message", "¡Profesor registrado con éxito!")
            putExtra("labelBtn", "Volver a menú")
            putExtra("goTo", AddProfesorActivity::class.java)
        }

        startActivity(intent)
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // Excepción personalizada para errores de guardado
    private class ProfesorSaveException(message: String) : Exception(message)

    companion object {
        private const val SPINNER_HINT = "Seleccionar actividad"
    }
}
