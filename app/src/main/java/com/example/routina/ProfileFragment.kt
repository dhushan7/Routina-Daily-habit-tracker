package com.example.routina

import android.annotation.SuppressLint
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

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val sharedPref = requireActivity().getSharedPreferences("UserData", AppCompatActivity.MODE_PRIVATE)

        // Views
        val nameView: TextView = view.findViewById(R.id.viewName)
        val emailView: TextView = view.findViewById(R.id.viewEmail)
        val nicknameView: TextView = view.findViewById(R.id.viewNickname)
        val bioView: TextView = view.findViewById(R.id.viewBio)
        val ageView: TextView = view.findViewById(R.id.viewAge)
        val phoneView: TextView = view.findViewById(R.id.viewPhone)
        val bloodGrpView: TextView = view.findViewById(R.id.viewBloodGrp)
        val btnEdit: Button = view.findViewById(R.id.btnEditProfile)
        val btnLogout: Button = view.findViewById(R.id.btnLogout)

        // Load saved values
        val name = sharedPref.getString("name", "User")
        val email = sharedPref.getString("email", "N/A")
        val age = sharedPref.getString("age", "Not set")
        val nickname = sharedPref.getString("nickname", "Not set")
        val bio = sharedPref.getString("bio", "Not set")
        val phone = sharedPref.getString("phone", "Not set")
        val bloodGrp = sharedPref.getString("blood-grp", "Not set")

        // Display values
        nameView.text = name
        emailView.text = email
        ageView.text = age
        nicknameView.text = nickname
        bioView.text = bio
        phoneView.text = phone
        bloodGrpView.text = bloodGrp

        // Navigate to Edit Profile
        btnEdit.setOnClickListener {
            val intent = Intent(requireActivity(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        // Logout
        btnLogout.setOnClickListener {
            sharedPref.edit().putBoolean("isLoggedIn", false).apply()
            val intent = Intent(requireActivity(), Login::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }
}
