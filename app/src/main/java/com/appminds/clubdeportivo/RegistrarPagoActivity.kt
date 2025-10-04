package com.appminds.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrarPagoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_pago_buscar)

        val btnBuscar = findViewById<Button>(R.id.btnBuscar)

        btnBuscar.setOnClickListener {
            val intent = Intent(this, PagarSocioActivity::class.java)
            startActivity(intent)
        }


    }
}
