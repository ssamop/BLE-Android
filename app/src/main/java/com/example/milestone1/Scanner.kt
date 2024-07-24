package com.example.milestone1

import android.Manifest
import android.Manifest.permission_group.LOCATION
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Scanner : AppCompatActivity() {
    private val REQUEST_COARSE_LOCATION: Int = 1
    private val REQUEST_ENABLE_BLUETOOTH: Int = 11

    private lateinit var devicesList: ListView
    private lateinit var scanningBtn: Button
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var listAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        // Get Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // Initialize views
        devicesList = findViewById(R.id.list_view)
        scanningBtn = findViewById(R.id.button)

        // Create a simple array adapter to display devices detected
        listAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        devicesList.adapter = listAdapter

        // Check Bluetooth state
        checkBluetoothState()

        // Register a dedicated receiver for Bluetooth actions
        registerReceiver(devicesFoundReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(
            devicesFoundReceiver,
            IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        )
        registerReceiver(
            devicesFoundReceiver,
            IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        )

        scanningBtn.setOnClickListener {
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
                // Check if coarse location permission is granted
                if (checkCoarseLocationPermission()) {
                    listAdapter.clear()
                    bluetoothAdapter.startDiscovery()
                } else {
                    checkBluetoothState()
                    // Check coarse location permission at the start of the app
                    checkCoarseLocationPermission()
                }
            }
        }
    }

        override fun onPause() {
            super.onPause()
            unregisterReceiver(devicesFoundReceiver)
        }

    private fun checkCoarseLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_COARSE_LOCATION
            )
            return false
        }
    }

    private fun checkBluetoothState() {
        if (bluetoothAdapter == null) {
            Toast.makeText(
                this,
                "Bluetooth is not supported on your device",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (bluetoothAdapter.isEnabled) {
                if (bluetoothAdapter.isDiscovering) {
                    Toast.makeText(this, "Device discovering process", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Bluetooth is enabled", Toast.LENGTH_SHORT).show()
                    scanningBtn.isEnabled = true
                }
            } else {
                Toast.makeText(this, "You need to enable Bluetooth", Toast.LENGTH_SHORT).show()
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH)
            }
        }

         fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
                checkBluetoothState()
            }
        }

        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

           // when (requestCode) {
             //   LOCATION -> {
               //     if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 //       Toast.makeText(this, "Access coarse location granted. You can scan Bluetooth devices", Toast.LENGTH_SHORT).show()
                   // } else {
                     //   Toast.makeText(this, "Access coarse location forbidden. You can't scan Bluetooth devices", Toast.LENGTH_SHORT).show()
                    //}
                //}
            //}
        }

    }

    // We need to implement our receiver to get devices detected
    private val devicesFoundReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        if (ActivityCompat.checkSelfPermission(this@Scanner, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return
                        }
                        listAdapter.add("${it.name}\n${it.address}")
                        listAdapter.notifyDataSetChanged()
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    scanningBtn.text = "Scanning Bluetooth Devices"
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    scanningBtn.text = "Scanning in progress..."
                }
            }
        }
    }

}
