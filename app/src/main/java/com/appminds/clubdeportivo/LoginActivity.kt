package com.appminds.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var inputEmail: EditText
    private lateinit var inputPass: EditText
    private lateinit var loginBtn: androidx.appcompat.widget.AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        inputEmail = findViewById<EditText>(R.id.inputEmail)
        inputPass = findViewById<EditText>(R.id.inputPassword)
        loginBtn = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnLogin)

        loginBtn.isEnabled = false

        val watcher = object  : TextWatcher {
            override fun afterTextChanged(s: Editable?) { checkForm() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        inputEmail.addTextChangedListener(watcher)
        inputPass.addTextChangedListener(watcher)

        loginBtn.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkForm() {
        val email = inputEmail.text.toString().trim()
        val pass = inputPass.text.toString().trim()
        loginBtn.isEnabled = email.isNotEmpty() && pass.isNotEmpty()
    }
}