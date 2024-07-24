package com.example.milestone1

import com.example.milestone1.DistancePlotView
import com.example.milestone1.R
import kotlin.math.pow
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DeviceDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_detail)

        val deviceName = intent.getStringExtra("DEVICE_NAME") ?: "Unknown Device"
        val deviceDistance = intent.getDoubleExtra("DEVICE_DISTANCE", -1.0)

        val distancePlotView: DistancePlotView = findViewById(R.id.distance_plot_view)
        val deviceData = mapOf(deviceName to deviceDistance)
        distancePlotView.setDeviceData(deviceData)
    }
}
