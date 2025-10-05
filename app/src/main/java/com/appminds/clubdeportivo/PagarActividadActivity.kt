package com.appminds.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PagarActividadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagar_actividad)

        val btnPagar = findViewById<Button>(R.id.btnPagar)


        btnPagar.setOnClickListener {
            val intent = Intent(this, PagoConfirmActivity::class.java)
            startActivity(intent)
        }

    }
}