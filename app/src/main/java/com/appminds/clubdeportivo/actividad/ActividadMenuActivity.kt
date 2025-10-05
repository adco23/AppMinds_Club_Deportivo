package com.appminds.clubdeportivo.actividad

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.appminds.clubdeportivo.R

class ActividadMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actividad_menu)

        // Ajuste de insets (igual que en Main)
        val root: View = findViewById(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        // Botón atrás
        findViewById<View>(R.id.btnGoToBack).setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Menu con 2 opciones como el Main pero reducido
        val container = findViewById<LinearLayout>(R.id.menuActividadesContainer)
        val items = listOf(
            "REGISTRAR" to { goToRegistrarActividad() },
            "ADMINISTRAR" to { goToAdministrarActividad() }
        )

        items.forEach { (label, action) ->
            val btn = AppCompatButton(this).apply {
                text = label
                setAllCaps(true)
                background = ContextCompat.getDrawable(context, R.drawable.bg_btn_regular)
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(0, 24, 0, 0)
                layoutParams = lp
            }
            btn.setOnClickListener { action() }
            container.addView(btn)
        }
    }

    private fun goToRegistrarActividad() {
        startActivity(Intent(this, AddActividadActivity::class.java))
    }

    private fun goToAdministrarActividad() {
        startActivity(Intent(this, AdministrarActividades::class.java))
    }

}
