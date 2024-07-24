package com.example.milestone1

import com.example.milestone1.DeviceDetailActivity
import com.example.milestone1.R
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.pow

class MainActivity3 : AppCompatActivity() {

    private val REQUEST_ACCESS_COARSE_LOCATION = 1
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var listView: ListView
    private lateinit var devicesList: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var mediaPlayer: MediaPlayer
    private val deviceDistances = mutableMapOf<String, Double>()

    private val deviceReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                val deviceName: String = device?.name ?: "Unknown Device"
                val deviceAddress: String = device?.address ?: "Unknown Address"
                val rssi: Short = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)
                val distance = rssiToDistance(rssi.toInt())
                Log.d("MainActivity3", "Device found: $deviceName, RSSI: $rssi, Distance: $distance")
                val deviceInfo = "$deviceName - $deviceAddress (RSSI: $rssi dBm, Distance: ${"%.2f".format(distance)} m)"
                devicesList.add(deviceInfo)
                adapter.notifyDataSetChanged()
                deviceDistances[deviceName] = distance
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        listView = findViewById(R.id.listview)
        devicesList = mutableListOf()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, devicesList)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedDeviceInfo = devicesList[position]
            val parts = selectedDeviceInfo.split(" - ", " ", "(", ")", ":")

            if (parts.size < 6) {
                Toast.makeText(this, "Invalid device info format", Toast.LENGTH_SHORT).show()
                return@OnItemClickListener
            }

            val deviceName = parts[0]
            val rssi: Int
            try {
                rssi = parts[5].toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid RSSI format", Toast.LENGTH_SHORT).show()
                return@OnItemClickListener
            }

            val distance = rssiToDistance(rssi)
            Log.d("MainActivity3", "Selected device: $deviceName, RSSI: $rssi, Calculated Distance: $distance")
            deviceDistances[deviceName] = distance

            val intent = Intent(this, DeviceGridView::class.java).apply {
                putExtra("DEVICE_DISTANCES", HashMap(deviceDistances))
            }
            startActivity(intent)
        }


        val scanButton: Button = findViewById(R.id.scan_button)
        scanButton.setOnClickListener {
            devicesList.clear()
            adapter.notifyDataSetChanged()
            checkBluetoothPermissions()
        }
    }

    private fun rssiToDistance(rssi: Int, txPower: Int = -59): Double {
        val distance = 10.0.pow((txPower - rssi) / (10 * 2.0))
        Log.d("MainActivity3", "RSSI: $rssi, Distance: $distance")
        return distance
    }


    private fun checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_ACCESS_COARSE_LOCATION
                )
            } else {
                startBluetoothDiscovery()
            }
        } else {
            startBluetoothDiscovery()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startBluetoothDiscovery() {
        if (bluetoothAdapter?.isEnabled == true) {
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(deviceReceiver, filter)
            bluetoothAdapter.startDiscovery()
            showPairedDevices()
        } else {
            Toast.makeText(this, "Turn on Bluetooth first", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showPairedDevices() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName: String = device.name ?: "Unknown Device"
            val deviceAddress: String = device.address ?: "Unknown Address"
            val deviceInfo = "$deviceName - $deviceAddress (Paired)"
            devicesList.add(deviceInfo)
        }
        adapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(deviceReceiver)
    }
}
