package com.example.milestone1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class messages : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_messages)
        val scanButton = findViewById<Button>(R.id.scan)

        scanButton.setOnClickListener {
            val explicitIntent = Intent(this, scan::class.java)
            startActivity(explicitIntent)
        }

        val devices = findViewById<Button>(R.id.devices)

        devices.setOnClickListener {
            val explicitIntent = Intent(this, scan::class.java)
            startActivity(explicitIntent)
        }
    }
}