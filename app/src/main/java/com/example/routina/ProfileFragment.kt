package com.example.routina

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val sharedPref = requireActivity().getSharedPreferences("UserData", AppCompatActivity.MODE_PRIVATE)

        // Display user name in the TextView (optional)
        val textView: TextView = view.findViewById(R.id.profileText)
        val name = sharedPref.getString("name", "User")
        textView.text = "Hello, $name"

        // Logout button
        val btnLogout: Button = view.findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            // Reset login state
            sharedPref.edit().putBoolean("isLoggedIn", false).apply()

            // Go to Login screen
            val intent = Intent(requireActivity(), Login::class.java)
            startActivity(intent)
            requireActivity().finish() // Close HomePage
        }

        return view
    }
}
