package com.example.milestone1

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Grid : AppCompatActivity() {

   // private lateinit var distanceView: DistanceView
    private lateinit var deviceInfoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid)

       // distanceView = findViewById(R.id.distance_view)
        deviceInfoTextView = findViewById(R.id.device_info)

        val deviceName = intent.getStringExtra("deviceName") ?: "Unknown Device"
        val deviceAddress = intent.getStringExtra("deviceAddress") ?: "Unknown Address"
        val rssi = intent.getShortExtra("rssi", Short.MIN_VALUE)
        val distance = rssiToDistance(rssi.toInt())

        deviceInfoTextView.text = "Device: $deviceName\nAddress: $deviceAddress\nRSSI: $rssi dBm\nDistance: %.2f m".format(distance)

       // distanceView.setDistance(distance.toFloat())

        // Log data for debugging
        Log.d("GraphActivity", "Received Device: $deviceName, $deviceAddress, $rssi, Distance: $distance")
    }

    private fun rssiToDistance(rssi: Int, txPower: Int = -59): Double {
        val n = 2.0 // Path-loss exponent, change based on the environment
        return Math.pow(10.0, (txPower - rssi) / (10 * n))
    }
}
