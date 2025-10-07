package com.example.routina

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Buttons & Inputs
        val btnRegister: Button = findViewById(R.id.buttonReg)
        val uName: EditText = findViewById(R.id.Name)
        val uAge: EditText = findViewById(R.id.Age)
        val uEmail: EditText = findViewById(R.id.emailReg)
        val uPass: EditText = findViewById(R.id.passReg)
        val uConfirmPass: EditText = findViewById(R.id.confirmPassReg)
        val loginLink: TextView = findViewById(R.id.LoginLink)

        btnRegister.setOnClickListener {
            val name = uName.text.toString().trim()
            val age = uAge.text.toString().trim()
            val email = uEmail.text.toString().trim()
            val pass = uPass.text.toString().trim()
            val confirmPass = uConfirmPass.text.toString().trim()

            when {
                name.isEmpty() || age.isEmpty() || email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty() -> {
                    Toast.makeText(this, "Please enter all details", Toast.LENGTH_LONG).show()
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "Invalid email format", Toast.LENGTH_LONG).show()
                }
                pass != confirmPass -> {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show()
                }
                else -> {
                    //Save details in SharedPreferences
                    val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("name", name)
                        putString("age", age)
                        putString("email", email)
                        putString("password", pass)
                        putBoolean("isLoggedIn", false) //then user must login
                        apply()
                    }

                    Toast.makeText(this, "Registration successful! Please log in.", Toast.LENGTH_LONG).show()

                    //Navigate to Login page
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish() //Close Register activity so user can’t come back with back button
                }
            }
        }

        //go to login
        loginLink.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}
