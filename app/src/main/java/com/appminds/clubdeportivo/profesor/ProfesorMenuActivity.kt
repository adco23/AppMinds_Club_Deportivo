package com.appminds.clubdeportivo.profesor

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.appminds.clubdeportivo.MainMenuActivity
import com.appminds.clubdeportivo.R

class ProfesorMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profesor_menu)

        val menuContainer = findViewById<LinearLayout>(R.id.profesorOptContainer)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val menuItems = listOf("Agregar profesor", "Asistencia", "Plantel")

        menuItems.forEach { label ->
            val button = AppCompatButton(this).apply {
                text = label
                setAllCaps(true)
                background = ContextCompat.getDrawable(context, R.drawable.bg_btn_regular)


                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 48, 0, 0)
                layoutParams = params
            }

            button.setOnClickListener {
                when (label) {
                    "Agregar profesor" -> startActivity(Intent(this, AddProfesorActivity::class.java))
                    "Asistencia" -> Toast.makeText(this, "Asistencia", Toast.LENGTH_SHORT).show()
                    "Plantel" -> Toast.makeText(this, "Plantel", Toast.LENGTH_SHORT).show()
                }
            }

            menuContainer.addView(button)
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
        }
    }
}