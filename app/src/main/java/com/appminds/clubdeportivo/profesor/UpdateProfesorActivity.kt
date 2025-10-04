package com.appminds.clubdeportivo.profesor

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.widget.addTextChangedListener
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.models.ProfesorDto

class UpdateProfesorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_profesor)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val btnSave = findViewById<AppCompatButton>(R.id.btnSaveChanges)

        val firstname = findViewById<EditText>(R.id.inputFirstname)
        val lastname = findViewById<EditText>(R.id.inputLastname)
        val dni = findViewById<EditText>(R.id.inputDNI)
        val address = findViewById<EditText>(R.id.inputAddress)
        val phone = findViewById<EditText>(R.id.inputPhone)
        val isSubstitute = findViewById<AppCompatCheckBox>(R.id.cbIsSubstitute)

        val spinner = findViewById<Spinner>(R.id.activitiesDropdown)
        val options = arrayOf("Natación", "Fútbol", "Gimnasia", "Tenis", "Yoga")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val profesor = intent.getSerializableExtra("profesor") as? ProfesorDto
        val original = profesor?.copy()

        if (profesor != null) {
            firstname.setText(profesor.firstname)
            lastname.setText(profesor.lastname)
            dni.setText(profesor.dni)
            address.setText(profesor.address)
            phone.setText(profesor.phone)
            isSubstitute.isChecked = profesor.isSubstitute

            val activityIndex = options.indexOf(profesor.activity)
            if (activityIndex != -1) {
                spinner.setSelection(activityIndex)
            }
        }

        fun checkForChanges() {
            val hasChanged = original?.firstname.toString() != firstname.text.toString().trim() ||
                    original?.lastname.toString() != lastname.text.toString().trim() ||
                    original?.dni.toString() != dni.text.toString().trim() ||
                    original?.address.toString() != address.text.toString().trim() ||
                    original?.phone.toString() != phone.text.toString().trim() ||
                    original?.isSubstitute != isSubstitute.isChecked ||
                    original?.activity.toString() != spinner.selectedItem.toString()

            btnSave.isEnabled = hasChanged
        }

        firstname.addTextChangedListener { checkForChanges() }
        lastname.addTextChangedListener { checkForChanges() }
        dni.addTextChangedListener { checkForChanges() }
        address.addTextChangedListener { checkForChanges() }
        phone.addTextChangedListener { checkForChanges() }
        isSubstitute.setOnCheckedChangeListener { _, _ -> checkForChanges() }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                checkForChanges()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnSave.setOnClickListener {
            val intent = Intent(this, ProfesorConfirmActivity::class.java)
            intent.putExtra("message", "¡Profesor modificado con éxito!")
            intent.putExtra("labelBtn", "Volver a menú")
            intent.putExtra("goTo", PlantelActivity::class.java)

            startActivity(intent)
            finish()
        }

    }
}