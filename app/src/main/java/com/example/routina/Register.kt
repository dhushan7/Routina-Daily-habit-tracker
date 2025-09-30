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
import org.w3c.dom.Text

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
        //Register button
        val btnRegister: Button = findViewById(R.id.buttonReg)

        //inputs
        val uName: EditText = findViewById(R.id.Name)
        val uAge: EditText = findViewById(R.id.Age)
        val uEmail: EditText = findViewById(R.id.emailReg)
        val uPass: EditText = findViewById(R.id.passReg)
        val uConfirmPass: EditText = findViewById(R.id.confirmPassReg)

        //login link
        val loginLink: TextView = findViewById(R.id.LoginLink)

        btnRegister.setOnClickListener {
            val name = uName.text.toString()
            val age = uAge.text.toString()
            val email = uEmail.text.toString()
            val pass = uPass.text.toString()
            val confirmPass = uConfirmPass.text.toString()
//            if(uName.text.toString()!=""){
//                val name = uName.text.toString()
//
//                val intent = Intent(this, Login::class.java)
//                intent.putExtra("User_Name", name)
//
//                startActivity(intent)
//            }
            when {
                name.isEmpty() || age.isEmpty() || email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty() -> {
                    Toast.makeText(this,"Enter all the details", Toast.LENGTH_LONG).show()
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this,"Invalid email format", Toast.LENGTH_LONG).show()
                }
                pass != confirmPass -> {
                    Toast.makeText(this,"Passwords do not match", Toast.LENGTH_LONG).show()
                }
                else -> {
                    // Save details in SharedPreferences
                    val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
                    val editor = sharedPref.edit()

                    editor.putString("name", name)
                    editor.putString("age", age)
                    editor.putString("email", email)
                    editor.putString("password", pass) // ❗ not secure, just for testing/demo
                    editor.apply() // save changes

                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show()

                    // Go to Login screen
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)

                    val intentUN = Intent(this, HomePage::class.java)
                    intentUN.putExtra("User_name", name)
                    startActivity(intentUN)
                }
            }
        }
        loginLink.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }
}
