package com.appminds.clubdeportivo.profesor

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.appminds.clubdeportivo.MainMenuActivity
import com.appminds.clubdeportivo.R

class ProfesorConfirmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profesor_confirm)

        val message = intent.getStringExtra("message") ?: "Operación realizada con éxito"
        val labelBtn = intent.getStringExtra("tvBtnMessage") ?: "Volver al menú"
        val goToBack = intent.getSerializableExtra("goTo") as? Class<*>

        val tvMessage = findViewById<TextView>(R.id.tvMessage)
        val btnGoToMain: AppCompatButton = findViewById(R.id.btnGoToMainMenu)
        val btnGoToBack: ImageButton = findViewById(R.id.btnGoToBack)

        tvMessage.text = message
        btnGoToMain.text = labelBtn

        btnGoToBack.setOnClickListener {
            if (goToBack != null) {
                startActivity(Intent(this, goToBack))
                finish()
            }
        }

        btnGoToMain.setOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }
    }
}