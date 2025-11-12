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
import com.appminds.clubdeportivo.data.dao.ProfesorDao
import com.appminds.clubdeportivo.data.model.ProfesorEntity
import com.appminds.clubdeportivo.models.AddProfesorDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddProfesorStep2Activity : AppCompatActivity() {
    private lateinit var profesorDao: ProfesorDao
    private var isSaving = false
    private lateinit var profesor: ProfesorEntity
    private lateinit var updateProfesor: ProfesorEntity
    private lateinit var btnSave: AppCompatButton
    private lateinit var spinner: Spinner
    private lateinit var container: LinearLayout
    private lateinit var txtSelected: TextView
    private lateinit var adapter: ArrayAdapter<CharSequence>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_profesor_step2)

        profesorDao = ProfesorDao(this)

        profesor = intent.getSerializableExtra("profesor") as ProfesorEntity

        initViews()

    }

    private fun initViews() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        btnSave = findViewById(R.id.btnAddProfSave)
        spinner = findViewById(R.id.spinnerOptions)
        container = findViewById(R.id.ActivitySelectedContainer)
        txtSelected = findViewById(R.id.ActivitySelected)

        adapter = ArrayAdapter.createFromResource(
            this,
            R.array.options_example,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selection = parent?.getItemAtPosition(position).toString()

                if(selection.isNotBlank()) {
                    container.isVisible = true
                    btnSave.isEnabled = true
                    txtSelected.text = selection
                    updateProfesor = profesor?.copy(activity = selection)!!
                }
                if(selection.isBlank()) {
                    container.isVisible = false
                    btnSave.isEnabled = false
                    profesor?.activity = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }

        btnSave.setOnClickListener { saveData() }
    }

    private fun saveData() {
        if (isSaving) return

        isSaving = true
        btnSave.isEnabled = false

        lifecycleScope.launch {
            try {
                val insertResult = withContext(Dispatchers.IO) {
                    profesorDao.insert(updateProfesor)
                }

                if (insertResult > 0) {
                    updateProfesor.id = insertResult.toInt()
                    val intent = Intent(this@AddProfesorStep2Activity, ProfesorConfirmActivity::class.java)
                    intent.putExtra("message", "¡Profesor registrado con éxito!")
                    intent.putExtra("labelBtn", "Volver a menú")
                    intent.putExtra("goTo", AddProfesorActivity::class.java)

                    startActivity(intent)
                    finish()

                } else {
                    throw Exception("Error al guardar el cliente")
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@AddProfesorStep2Activity,
                    "Error al guardar: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                isSaving = false
                btnSave.isEnabled = true
            }
        }
    }
}