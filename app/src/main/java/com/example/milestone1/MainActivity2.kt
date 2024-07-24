package com.example.milestone1

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity2 : AppCompatActivity() {
    private lateinit var scanButton: Button
    private lateinit var scanListView: ListView
    private val stringArrayList = ArrayList<String>()
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private var myAdapter: BluetoothAdapter? = null
    private var myReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        scanButton = findViewById(R.id.searchBtn)
        scanListView = findViewById(R.id.pairedTv)

        // Check and request Bluetooth permission if necessary
        if (hasBluetoothPermission()) {
            initializeBluetooth()
        } else {
            requestBluetoothPermission()
        }

        scanButton.setOnClickListener(View.OnClickListener {
            startDiscovery()
        })

        arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, stringArrayList)
        scanListView.adapter = arrayAdapter
    }

    private fun hasBluetoothPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestBluetoothPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun initializeBluetooth() {
        myAdapter = BluetoothAdapter.getDefaultAdapter()

        myReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (BluetoothDevice.ACTION_FOUND == action) {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        if (ActivityCompat.checkSelfPermission(
                                context,
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
                        stringArrayList.add(it.name ?: "Unknown Device")
                        arrayAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        val intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(myReceiver, intentFilter)
    }

    private fun startDiscovery() {
        if (myAdapter != null && myAdapter!!.isEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (hasBluetoothPermission()) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_SCAN
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
                    myAdapter!!.startDiscovery()
                } else {
                    requestBluetoothPermission()
                }
            } else {
                myAdapter!!.startDiscovery()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeBluetooth()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}