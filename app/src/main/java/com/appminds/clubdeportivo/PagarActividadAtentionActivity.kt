package com.appminds.clubdeportivo

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class PagarActividadAtentionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagar_actividad_atention)

        val btnBack = findViewById<ImageButton>(R.id.btnGoToBack)
        val btnVolver = findViewById<ImageButton>(R.id.btnVolver)

        btnBack.setOnClickListener { finish() }
        btnVolver.setOnClickListener { finish() }
    }
}