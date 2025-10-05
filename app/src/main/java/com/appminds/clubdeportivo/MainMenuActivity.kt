package com.appminds.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.appminds.clubdeportivo.clients.AddClientActivity
import com.appminds.clubdeportivo.clients.ClientOverdueActivity
import com.appminds.clubdeportivo.clients.SearchClientActivity
import com.appminds.clubdeportivo.profesor.ProfesorMenuActivity

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)

        val menuContainer = findViewById<LinearLayout>(R.id.menuContainer)
        val menuItems = listOf("Buscar cliente", "Registrar cliente", "Registrar pago", "Socios con cuotas vencidas", "Actividades", "Profesores")
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnLogout.setOnClickListener {
            logOut()
        }

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
                    "Buscar cliente" -> goToSearchClient()
                    "Registrar cliente" -> goToAddClient()
                    "Socios con cuotas vencidas" -> goToOverdueClient()
                    "Registrar pago" -> goToRegistrarPago()
                    "Actividades" -> Toast.makeText(this, "Actividades", Toast.LENGTH_SHORT).show()
                    "Profesores" -> this.goToProfesores()
                }
            }

            menuContainer.addView(button)
        }
    }

    private fun goToProfesores() {
        val intent = Intent(this, ProfesorMenuActivity::class.java)
        startActivity(intent)
    }

    private fun goToSearchClient() { startActivity(Intent(this, SearchClientActivity::class.java))}
    private fun goToAddClient() { startActivity(Intent(this, AddClientActivity::class.java))}
    private fun goToOverdueClient() { startActivity(Intent(this, ClientOverdueActivity::class.java))}

    private fun logOut() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
    private fun goToRegistrarPago() { startActivity(Intent(this, RegistrarPagoActivity::class.java))}
}