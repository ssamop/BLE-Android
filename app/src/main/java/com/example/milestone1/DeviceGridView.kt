package com.example.milestone1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DeviceGridView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_grid_view)

        val deviceDistances = intent.getSerializableExtra("DEVICE_DISTANCES") as HashMap<String, Double>
        val distancePlotView: DistancePlotView = findViewById(R.id.distancePlotView)
        distancePlotView.setDeviceData(deviceDistances)
    }
}
