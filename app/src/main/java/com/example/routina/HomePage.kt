package com.example.routina

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.routina.databinding.ActivityHomePageBinding
import androidx.fragment.app.Fragment

class HomePage : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    private lateinit var binding: ActivityHomePageBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // ✅ Use ViewBinding
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //get from shared pref.
//        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
//        val textMsg: TextView = findViewById(R.id.TextMsgHome)
//        val intent = intent
//        val name = sharedPref.getString("name", "User")
//        textMsg.text = "Hello, $name"


        val binding: ActivityHomePageBinding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.navHealth -> {
                    replaceFragment(HealthFragment())
                    true
                }
                R.id.navToDo -> {
                    replaceFragment(ToDoFragment())
                    true
                }
                R.id.navProfile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}