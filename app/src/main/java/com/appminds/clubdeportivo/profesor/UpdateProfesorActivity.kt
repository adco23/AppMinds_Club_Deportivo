package com.appminds.clubdeportivo.profesor

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.data.dao.ActividadDao
import com.appminds.clubdeportivo.data.dao.ProfesorDao
import com.appminds.clubdeportivo.data.model.ActividadEntity
import com.appminds.clubdeportivo.data.model.ProfesorEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class   UpdateProfesorActivity : AppCompatActivity() {
    private val profesorDao by lazy { ProfesorDao(this) }
    private val activityDao by lazy { ActividadDao(this) }
    private lateinit var profesor: ProfesorEntity
    private lateinit var originalProfesor: ProfesorEntity
    private var activity: ActividadEntity? = null
    private var originalActivity: ActividadEntity? = null
    private var activities: List<ActividadEntity> = emptyList()
    private lateinit var spinner: Spinner

    private lateinit var inputFirstname: EditText
    private lateinit var inputLastname: EditText
    private lateinit var inputDNI: EditText
    private lateinit var inputAddress: EditText
    private lateinit var inputPhone: EditText
    private lateinit var cbIsSubstitute: AppCompatCheckBox
    private lateinit var btnSave: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_profesor)

        extractProfesorFromIntent()
        initViews()
        setupListeners()
    }

    private fun setupListeners() {
        val textWatcher: (Editable?) -> Unit = { checkForChanges() }

        inputFirstname.addTextChangedListener(afterTextChanged = textWatcher)
        inputLastname.addTextChangedListener(afterTextChanged = textWatcher)
        inputDNI.addTextChangedListener(afterTextChanged = textWatcher)
        inputAddress.addTextChangedListener(afterTextChanged = textWatcher)
        inputPhone.addTextChangedListener(afterTextChanged = textWatcher)
        cbIsSubstitute.setOnCheckedChangeListener { _, _ -> checkForChanges() }

        btnSave.setOnClickListener {
            val selectedActivityId = if (spinner.selectedItemPosition > 0) {
                activities[spinner.selectedItemPosition - 1].id
            } else {
                null
            }

            profesor = profesor.copy(
                firstname = inputFirstname.text.toString().trim(),
                lastname = inputLastname.text.toString().trim(),
                address = inputAddress.text.toString().trim(),
                phone = inputPhone.text.toString().trim(),
                isSubstitute = cbIsSubstitute.isChecked,
                activity = selectedActivityId
            )

            AlertDialog.Builder(this)
                .setTitle("Guardar cambios")
                .setMessage("¿Desea guardar los cambios en ${profesor.firstname} ${profesor.lastname}?")
                .setPositiveButton("Sí") { _, _ ->
                    lifecycleScope.launch {
                        val success = withContext(Dispatchers.IO) {
                            profesorDao.update(profesor)
                        }
                        if (success > 0) {
                            val intent = Intent(this@UpdateProfesorActivity, ProfesorConfirmActivity::class.java)
                            intent.putExtra("message", "¡Profesor modificado con éxito!")
                            intent.putExtra("labelBtn", "Volver a menú")
                            intent.putExtra("goTo", PlantelActivity::class.java)

                            startActivity(intent)
                            finish()
                        } else {
                            showError("Error al actualizar profesor")
                        }
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

    }

    private fun initViews() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        btnSave = findViewById(R.id.btnSaveChanges)

        inputFirstname = findViewById(R.id.inputFirstname)
        inputLastname = findViewById(R.id.inputLastname)
        inputDNI = findViewById(R.id.inputDNI)
        inputDNI.isEnabled = false
        inputAddress = findViewById(R.id.inputAddress)
        inputPhone = findViewById(R.id.inputPhone)
        cbIsSubstitute = findViewById(R.id.cbIsSubstitute)

        loadSpinner()

        profesor.let {
            inputFirstname.setText( it.firstname )
            inputLastname.setText( it.lastname )
            inputDNI.setText( it.dni )
            inputAddress.setText( it.address )
            inputPhone.setText( it.phone )
            cbIsSubstitute.isChecked = it.isSubstitute

            if (it.activity != null) {
                val idx = activities.indexOfFirst { a -> a.id == it.activity }
                if (idx >= 0) {
                    spinner.setSelection(idx + 1)
                } else {
                    spinner.setSelection(0)
                }
            } else {
                spinner.setSelection(0)
            }
        }

        checkForChanges()
    }

    private fun loadSpinner() {
        spinner = findViewById(R.id.activitiesDropdown)

        val spinnerItems = mutableListOf("- Seleccionar actividad -")
        spinnerItems.addAll(activities.map { it.name })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                checkForChanges()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun extractProfesorFromIntent() {
        val profesorID: Int = intent.getIntExtra("profesorID", -1)
        if( profesorID == -1) {
            showError("Error al obtener datos del profesor")
            finish()
            return
        }

        profesor = profesorDao.getByID(profesorID)
        originalProfesor = profesor.copy()
        activities = activityDao.getAll()
        val foundActivity = activities.find { it.id == profesor.activity }
        activity = foundActivity
        activity?.let { originalActivity = it.copy() }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun checkForChanges() {
        val selectedPosition = spinner.selectedItemPosition
        val selectedActivityId = if (selectedPosition in activities.indices) activities[selectedPosition].id else -1

        val hasChanged = originalProfesor.firstname != inputFirstname.text.toString().trim() ||
                originalProfesor.lastname != inputLastname.text.toString().trim() ||
                originalProfesor.address != inputAddress.text.toString().trim() ||
                originalProfesor.phone != inputPhone.text.toString().trim() ||
                originalProfesor.isSubstitute != cbIsSubstitute.isChecked ||
                originalProfesor.activity != selectedActivityId

        btnSave.isEnabled = hasChanged
    }
}