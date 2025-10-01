package com.example.routina

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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


        // checking  user is already logged in
        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        if (sharedPref.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, HomePage::class.java))
            finish() // close Login activity
        }

        // 2️⃣ Setup ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //login button
        val login: Button = findViewById(R.id.btnLogin)

        //inputs
        val loginEmail: EditText = findViewById(R.id.emailLogin)
        val passLogin: EditText = findViewById(R.id.passLogin)

        login.setOnClickListener {
            val enteredEmail = loginEmail.text.toString().trim()//ignoring start&ending spaces
            val enteredPass = passLogin.text.toString().trim()

            val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
            val savedEmail = sharedPref.getString("email", null)
            val savedPass = sharedPref.getString("password", null)


            if (enteredEmail.isEmpty() || enteredPass.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else if (enteredEmail == savedEmail && enteredPass == savedPass) {
                Toast.makeText(this, "Login Successful.", Toast.LENGTH_LONG).show()
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)

            } else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_LONG).show()
            }
        }
    }
}