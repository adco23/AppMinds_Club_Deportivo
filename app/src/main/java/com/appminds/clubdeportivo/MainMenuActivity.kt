package com.appminds.clubdeportivo

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)

        val menuContainer = findViewById<LinearLayout>(R.id.menuContainer)
        val menuItems = listOf("Buscar cliente", "Registrar cliente", "Registrar pago", "Socios con cuotas vencidas", "Actividades", "Profesores")

        menuItems.forEach { label ->
            val button = AppCompatButton(this).apply {
                text = label
                setAllCaps(true)
                background = ContextCompat.getDrawable(context, R.drawable.btn_regular)


                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 48, 0, 0)
                layoutParams = params
            }

            button.setOnClickListener {
                when (label) {
                    "Buscar cliente" -> Toast.makeText(this, "Buscar cliente", Toast.LENGTH_SHORT).show()
                    "Registrar cliente" -> Toast.makeText(this, "Registrar cliente", Toast.LENGTH_SHORT).show()
                    "Registrar pago" -> Toast.makeText(this, "Registrar pago", Toast.LENGTH_SHORT).show()
                    "Socios con cuotas vencidas" -> Toast.makeText(this, "Socios con cuotas vencidas", Toast.LENGTH_SHORT).show()
                    "Actividades" -> Toast.makeText(this, "Actividades", Toast.LENGTH_SHORT).show()
                    "Profesores" -> Toast.makeText(this, "Profesores", Toast.LENGTH_SHORT).show()
                }
            }

            menuContainer.addView(button)
        }
    }
}