package com.example.milestone1

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import java.security.Permission

class CustomGridView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    private var deviceDistances: Map<String, Double> = emptyMap()

    fun setDeviceDistances(deviceDistances: Map<String, Double>) {
        this.deviceDistances = deviceDistances
        invalidate() // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val maxDistance = deviceDistances.values.maxOrNull() ?: 1.0
        val minDistance = deviceDistances.values.minOrNull() ?: 0.0
        val width = width.toFloat()
        val height = height.toFloat()

        deviceDistances.forEach { (_, distance) ->
            val normalizedDistance = ((distance - minDistance) / (maxDistance - minDistance)).toFloat()
            val x = width / 2
            val y = height / 2
            val radius = normalizedDistance * Math.min(width, height) / 4  // Adjusted factor to avoid overlap

            canvas.drawCircle(x, y, radius, paint)
        }
    }
}

