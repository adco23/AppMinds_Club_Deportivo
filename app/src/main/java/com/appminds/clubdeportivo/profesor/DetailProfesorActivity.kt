package com.appminds.clubdeportivo.profesor

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.appminds.clubdeportivo.R
import com.appminds.clubdeportivo.models.PlantelItemDto

class DetailProfesorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_profesor)

        val profesor = intent.getSerializableExtra("profesor") as? PlantelItemDto
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        profesor?.let {
            findViewById<TextView>(R.id.tvProfesorName).text = it.fullname
            findViewById<TextView>(R.id.tvDni).text = it.dni
            findViewById<TextView>(R.id.tvAddress).text = it.address
            findViewById<TextView>(R.id.tvPhone).text = it.phone
            findViewById<TableRow>(R.id.trIsSubstitute).isVisible = it.isSubstitute
        }

        btnBack.setOnClickListener { finish() }
    }
}