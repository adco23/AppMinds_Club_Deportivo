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
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.models.AddProfesorDto

class AddProfesorStep2Activity : AppCompatActivity() {
    private lateinit var updateProfesor: AddProfesorDto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_profesor_step2)

        val profesor = intent.getSerializableExtra("profesor") as? AddProfesorDto

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val btnSave: AppCompatButton = findViewById(R.id.btnAddProfSave)
        val spinner: Spinner = findViewById(R.id.spinnerOptions)
        val container: LinearLayout = findViewById(R.id.ActivitySelectedContainer)
        val txtSelected: TextView = findViewById(R.id.ActivitySelected)
        val adapter = ArrayAdapter.createFromResource(
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

        btnBack.setOnClickListener { finish() }
        btnSave.setOnClickListener {
            startActivity(Intent(this, AddProfesorConfirmActivity::class.java))
        }

    }
}