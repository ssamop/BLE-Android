package com.example.milestone1

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BluetoothReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
            when (state) {
                BluetoothAdapter.STATE_OFF -> {
                    // Bluetooth turned off
                    Toast.makeText(context, "Bluetooth turned off", Toast.LENGTH_SHORT).show()
                }
                BluetoothAdapter.STATE_ON -> {
                    // Bluetooth turned on
                    Toast.makeText(context, "Bluetooth turned on", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}