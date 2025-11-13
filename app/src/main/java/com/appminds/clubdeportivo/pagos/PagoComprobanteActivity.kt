package com.appminds.clubdeportivo.pagos

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.appminds.clubdeportivo.R

class PagoComprobanteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_comprobante)

        val btnBack = findViewById<ImageButton>(R.id.btnGoToBack)
        btnBack.setOnClickListener { finish() }

        val btnDescargar = findViewById<AppCompatButton>(R.id.btnDescargar)
        btnDescargar.setOnClickListener {
            Toast.makeText(this, "Descargando comprobante", Toast.LENGTH_SHORT).show()
            finish()
        }



    }
}