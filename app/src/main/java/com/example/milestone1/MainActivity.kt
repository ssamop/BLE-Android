package com.example.milestone1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logButton = findViewById<Button>(R.id.bottomButton)

        logButton.setOnClickListener {
            val explicitIntent = Intent(this, loginpage::class.java)
            startActivity(explicitIntent)
        }

        val signButton = findViewById<Button>(R.id.signupButton)

        signButton.setOnClickListener {
            val explicitIntent = Intent(this, signup::class.java)
            startActivity(explicitIntent)
        }
    }
}