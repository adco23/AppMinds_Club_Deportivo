package com.appminds.clubdeportivo.pagos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.appminds.clubdeportivo.R

class PagoConfirmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_confirm)

        val btnComprobante = findViewById<Button>(R.id.btnComprobante)
        val btnBack = findViewById<ImageButton>(R.id.btnGoToBack)

        btnComprobante.setOnClickListener {
            val intent = Intent(this, PagoComprobanteActivity::class.java)
            startActivity(intent)
        }

        btnBack.setOnClickListener { finish() }
    }


}