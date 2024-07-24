package com.example.milestone1

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.content.ContextCompat.startActivity
import android.os.Build
import android.text.method.LinkMovementMethod
import android.util.Log
import com.google.android.play.integrity.internal.i

class scan : AppCompatActivity() {

    private val REQUEST_CODE_ENABLE_BT: Int = 1
    private val REQUEST_CODE_DISCOVERABLE_BT: Int = 2
    private val REQUEST_ACCESS_COARSE_LOCATION: Int = 101
    private val REQUEST_ENABLE_BT = 1
    private val PERMISSION_REQUEST_FINE_LOCATION = 2
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var arrayList: ArrayList<String>

    //private lateinit var listView: ListView
    private val REQUEST_BLUETOOTH_PERMISSION = 1

    private lateinit var searchBtn: Button
    private lateinit var searchTv: TextView

    //private val onDeviceFound: (BluetoothDevice) -> Unit = TODO()

    lateinit var badapter: BluetoothAdapter
    lateinit var bluetoothManager: BluetoothManager
    lateinit var btEnableResultLauncher: ActivityResultLauncher<Intent>
    lateinit var devicebm: BluetoothDevice

    private val deviceReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                val deviceName: String = device?.name ?: "Unknown Device"
                val deviceAddress: String = device?.address ?: "Unknown Address"
                val rssi: Short = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)
                searchTv.append("$deviceName - $deviceAddress (RSSI: $rssi dBm)\n")
            }
        }
    }

    val requestEnableBtLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth turned on", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Bluetooth enabling canceled", Toast.LENGTH_SHORT).show()
            }
        }

    val requestDisableBtLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth turned off", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Bluetooth disabling canceled", Toast.LENGTH_SHORT).show()
            }
        }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scan)

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        badapter = bluetoothManager.adapter

        val bluetoothStatus = findViewById<TextView>(R.id.bluetoothStatus)
        val bluetoothIv = findViewById<ImageView>(R.id.bluetoothIv)
        val turnOn = findViewById<Button>(R.id.turnOn)
        val turnOff = findViewById<Button>(R.id.turnOff)
        val discoverable = findViewById<Button>(R.id.discoverable)
        val pairedBtn = findViewById<Button>(R.id.pairedBtn)
        val pairedTv = findViewById<TextView>(R.id.pairedTv)
        //val searchBtn = findViewById<Button>(R.id.searchBtn)
        val searchTv = findViewById<TextView>(R.id.searchTv)
        //val listView = findViewById<ListView>(R.id.pairedTv)
        arrayList = ArrayList()
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList)
        //listView.adapter = arrayAdapter
        //val searchTv = findViewById<TextView>(R.id.searchTv)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        badapter = bluetoothManager.adapter

        ///checking if bluetooth is on
        if (badapter == null) {
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_LONG).show()
            return
        }
        ///set image on/off

        //turn on bluetooth
        turnOn.setOnClickListener {
            if (!badapter.isEnabled) {
                if (checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
                    badapter.enable()
                    bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
                }
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                requestEnableBtLauncher.launch(enableBtIntent)
                Toast.makeText(this, "Bluetooth On", Toast.LENGTH_SHORT).show()
            } else {
                // Check if BLUETOOTH_CONNECT permission is granted
                if (checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted, start activity to enable Bluetooth
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    requestEnableBtLauncher.launch(enableBtIntent)
                }
            }
        }
        turnOff.setOnClickListener {
            navigateToBluetoothSettings()
            stopBluetoothDeviceScan()
        }

        discoverable.setOnClickListener {
            if (!badapter.isDiscovering) {
                Toast.makeText(this, "Making Your device discoverable", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
                startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT)
            }
        }
        //get list of paired devices
        pairedBtn.setOnClickListener {
            bluetoothIv.setImageResource(R.drawable.ic_bluetooth_connect)
            if (badapter.isEnabled) {
                pairedTv.text = getString(R.string.paired_devices)
                //get list
                val devices = badapter.bondedDevices
                for (device in devices) {
                    var rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)
                    val deviceName = device.name
                    val deviceAddress = device.address
                    val deviceRssi = "$rssi dBm"
                    pairedTv.append("\nDevice: $deviceName , $device, $deviceRssi , $deviceAddress")
                }
            } else {
                Toast.makeText(this, "Turn on Bluetooth first", Toast.LENGTH_LONG).show()
            }
        }

        searchBtn.setOnClickListener {
            searchTv.text = ""
            checkBluetoothPermissions()
        }
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
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(deviceReceiver, filter)

            bluetoothAdapter?.startDiscovery()
        }

        override fun onDestroy() {
            super.onDestroy()
            unregisterReceiver(deviceReceiver)
        }

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    //bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
                    Toast.makeText(this, "Bluetooth is on", Toast.LENGTH_LONG).show()
                } else {
                    //user denied
                    Toast.makeText(this, "Could not on bluetooth", Toast.LENGTH_LONG).show()
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private val discoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val action = it.action
                if (BluetoothDevice.ACTION_FOUND == action) {
                    val device = it.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val rssi = it.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)
                    //val deviceName = device?.name ?: "Unknown Device"
                    val deviceAddress = device?.address ?: "Unknown Address"

                    // Display device info with RSSI
                    val deviceInfo = "Device: $device, $deviceAddress (RSSI: $rssi dBm)"
                    val searchTv = findViewById<TextView>(R.id.searchTv)
                    searchTv.append("\n$deviceInfo")
                }
            }
        }
    }

    private fun navigateToBluetoothSettings() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
            // Bluetooth is enabled, navigate to Bluetooth settings
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            startActivity(intent)
            val bluetoothIv = findViewById<ImageView>(R.id.bluetoothIv)
            bluetoothIv.setImageResource(R.drawable.ic_bluetooth_off)
        } else {
            // Bluetooth is not enabled
            Toast.makeText(this, "Bluetooth is already off", Toast.LENGTH_SHORT).show()
        }
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            devicebm = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
            var rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)
            println("Found Bluetooth device: ${devicebm ?: "Unknown Device"} | RSSI: $rssi dBm")
        }
    }

    private fun stopBluetoothDeviceScan() {
        val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
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
        bluetoothLeScanner?.stopScan(scanCallback)
    }
}



