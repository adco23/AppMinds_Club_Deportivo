package com.appminds.clubdeportivo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    private lateinit var inputEmail: EditText
    private lateinit var inputPass: EditText
    private lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

//        inputEmail = findViewById<EditText>(R.id.inputEmail)
//        inputPass = findViewById<EditText>(R.id.inputPassword)
//        loginBtn = findViewById<Button>(R.id.btnLogin)
//
//        loginBtn.isEnabled = false
//
//        val watcher = object  : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                checkForm()
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                TODO("Not yet implemented")
//            }
//        }
//
//        inputEmail.addTextChangedListener(watcher)
//        inputPass.addTextChangedListener(watcher)
    }
//df
//    private fun checkForm() {
//        val email = inputEmail.text.toString().trim()
//        val pass = inputPass.text.toString().trim()
//        loginBtn.isEnabled = email.isNotEmpty() && pass.isNotEmpty()
//    }
}