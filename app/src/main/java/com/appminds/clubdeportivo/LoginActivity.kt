package com.appminds.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import com.appminds.clubdeportivo.data.dao.UserDao
import com.appminds.clubdeportivo.session.SessionManager

class LoginActivity : AppCompatActivity() {
    private lateinit var userDao: UserDao
    private lateinit var inputEmail: EditText
    private lateinit var inputPass: EditText
    private lateinit var loginBtn: AppCompatButton
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)
        userDao = UserDao(this)

        if (sessionManager.isLoggedIn()) {
            goToMainMenu()
            return
        }

        initViews()
        setupListeners()
    }

    private fun goToMainMenu() {
        val intent = Intent(this, MainMenuActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        startActivity(intent)
        finish()
    }

    private fun setupListeners() {
        val textWatcher: (Editable?) -> Unit = { checkForm() }
        inputEmail.addTextChangedListener(afterTextChanged = textWatcher)
        inputPass.addTextChangedListener(afterTextChanged = textWatcher)
    }

    private fun initViews() {
        inputEmail = findViewById(R.id.inputEmail)
        inputPass = findViewById(R.id.inputPassword)
        loginBtn = findViewById(R.id.btnLogin)

        loginBtn.setOnClickListener { login() }
    }

    private fun checkForm() {
        val email = inputEmail.text.toString().trim()
        val pass = inputPass.text.toString().trim()
        loginBtn.isEnabled = email.isNotEmpty() && pass.isNotEmpty()
    }

    private fun login() {
        val user = userDao.getByEmail(inputEmail.text.toString())

        if (user == null) {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
        } else {
            if (user.clave != inputPass.text.toString()) {
                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Bienvenido ${user.nombre}", Toast.LENGTH_SHORT).show()

//                val intent = Intent(this, MainMenuActivity::class.java).apply {
//                    putExtra("username", user.nombre)
//                }

                sessionManager.createLoginSession(user.nombre)
                goToMainMenu()
//                startActivity(intent)
//                finish()
            }
        }
    }
}