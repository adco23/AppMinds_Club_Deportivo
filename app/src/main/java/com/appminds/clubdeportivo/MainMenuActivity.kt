package com.appminds.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.appminds.clubdeportivo.clients.AddClientActivity
import com.appminds.clubdeportivo.clients.ClientOverdueActivity
import com.appminds.clubdeportivo.clients.SearchClientActivity
import com.appminds.clubdeportivo.actividad.ActividadMenuActivity
import com.appminds.clubdeportivo.profesor.ProfesorMenuActivity
import com.appminds.clubdeportivo.session.SessionManager

class MainMenuActivity : AppCompatActivity() {
    private val menuItems = listOf("Buscar cliente", "Registrar cliente", "Registrar pago", "Socios con cuotas vencidas", "Actividades", "Profesores")
    private lateinit var session: SessionManager
    private lateinit var tvHelloUser: TextView
    private lateinit var menuContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)

        session = SessionManager(applicationContext)

        initViews()

        if (!session.isLoggedIn()) {
            goToLoginAndFinish()
            return
        }

        loadMenu()
    }

    private fun initViews() {
        menuContainer = findViewById(R.id.menuContainer)
        tvHelloUser = findViewById(R.id.txtHelloUser)

        findViewById<Button>(R.id.btnLogout).setOnClickListener { goToLoginAndFinish() }
    }

    private fun loadMenu() {
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
                    "Actividades" -> this.goToActividadMenu()
                    "Profesores" -> this.goToProfesores()
                }
            }

            menuContainer.addView(button)
        }
    }

    private fun goToProfesores() { startActivity(Intent(this, ProfesorMenuActivity::class.java)) }
    private fun goToSearchClient() { startActivity(Intent(this, SearchClientActivity::class.java))}
    private fun goToAddClient() { startActivity(Intent(this, AddClientActivity::class.java))}
    private fun goToOverdueClient() { startActivity(Intent(this, ClientOverdueActivity::class.java))}
    private fun goToRegistrarPago() { startActivity(Intent(this, RegistrarPagoActivity::class.java))}
    private fun goToActividadMenu() { startActivity(Intent(this, ActividadMenuActivity::class.java)) }

//    private fun logOut() {
//        val intent = Intent(this, LoginActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intent)
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//        finish()
//    }

    override fun onResume() {
        super.onResume()
        updateWelcome()
    }

    private fun updateWelcome() {
        val username = session.getUsername()
        if (username != null) {
            tvHelloUser.text = "¡Hola, $username!"
        } else {
            tvHelloUser.text = "Bienvenido"
        }
    }

    private fun goToLoginAndFinish() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}