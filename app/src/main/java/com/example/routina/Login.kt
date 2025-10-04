package com.example.routina

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.routina.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Setup ViewBinding FIRST
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ✅ Check if user is already logged in
        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, HomePage::class.java))
            finish()
            return
        }

        // ✅ Register link navigation
        binding.RegLink.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        // ✅ Login button listener
        binding.btnLogin.setOnClickListener {
            val enteredEmail = binding.emailLogin.text.toString().trim()
            val enteredPass = binding.passLogin.text.toString().trim()

            val savedEmail = sharedPref.getString("email", null)
            val savedPass = sharedPref.getString("password", null)

            // Validation
            if (enteredEmail.isEmpty() || enteredPass.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Login success
            if (enteredEmail == savedEmail && enteredPass == savedPass) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                // Save login status
                sharedPref.edit().putBoolean("isLoggedIn", true).apply()

                // Redirect to HomePage
                startActivity(Intent(this, HomePage::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_LONG).show()
            }
        }
    }
}
