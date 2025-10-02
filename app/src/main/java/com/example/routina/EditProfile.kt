package com.example.routina

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)

        // Views
        val editNickname: EditText = findViewById(R.id.editNickname)
        val editBio: EditText = findViewById(R.id.editBio)
        val editAge: EditText = findViewById(R.id.editAge)
        val editPhone: EditText = findViewById(R.id.editPhone)
        val bloodGrp: EditText = findViewById(R.id.bloodgroup)
        val btnSave: Button = findViewById(R.id.btnSaveProfile)
        val btnCancel: Button = findViewById(R.id.btnCancel)


        // Load saved values
        editNickname.setText(sharedPref.getString("nickname", ""))
        editBio.setText(sharedPref.getString("bio", ""))
        editAge.setText(sharedPref.getString("age", ""))
        editPhone.setText(sharedPref.getString("phone", ""))
        bloodGrp.setText(sharedPref.getString("blood-grp",""))

        // Save changes
        btnSave.setOnClickListener {
            val newNick = editNickname.text.toString()
            val newBio = editBio.text.toString()
            val newAge = editAge.text.toString()
            val newPhone = editPhone.text.toString()
            val newBloodGrp = bloodGrp.text.toString()

            with(sharedPref.edit()) {
                putString("nickname", newNick)
                putString("bio", newBio)
                putString("age", newAge)
                putString("phone", newPhone)
                putString("blood-grp",newBloodGrp)
                apply()
            }

            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
            finish() // go back to ProfileFragment
        }

        btnCancel.setOnClickListener {
            finish() // just close without saving
        }
    }
}
