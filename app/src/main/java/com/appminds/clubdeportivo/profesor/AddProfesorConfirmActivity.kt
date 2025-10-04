package com.appminds.clubdeportivo.profesor

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.appminds.clubdeportivo.MainMenuActivity
import com.appminds.clubdeportivo.R

class AddProfesorConfirmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_profesor_confirm)

        val btnGoToMain: AppCompatButton = findViewById(R.id.btnGoToMainMenu)
        val btnGoToForm: ImageButton = findViewById(R.id.btnGoToAddProfesorForm)

        btnGoToForm.setOnClickListener {
            startActivity(Intent(this, AddProfesorActivity::class.java))
            finish()
        }

        btnGoToMain.setOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }
    }
}