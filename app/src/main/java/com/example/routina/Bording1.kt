package com.example.routina

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button

class Bording1 : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bording1)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔹 Read saved user data
        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        val email = sharedPref.getString("email", null)
        val password = sharedPref.getString("password", null)

        // 🔍 Decision flow:
        when {
            // ✅ Case 1: User is logged in → Go directly to HomePage
            isLoggedIn -> {
                startActivity(Intent(this, HomePage::class.java))
                finish()
            }

            // 🧾 Case 2: User has registered before but not logged in
            !isLoggedIn && email != null && password != null -> {
                startActivity(Intent(this, Login::class.java))
                finish()
            }

            // 🆕 Case 3: New user → Stay on Bording1
            else -> {
                val launchingBtn1: Button = findViewById(R.id.launchingbtn1)
                launchingBtn1.setOnClickListener {
                    val intent = Intent(this, Register::class.java)
                    startActivity(intent)
                    finish() // optional
                }
            }
        }
    }
}
