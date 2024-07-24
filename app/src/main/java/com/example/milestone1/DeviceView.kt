package com.example.milestone1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DeviceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var deviceName: String = ""
    private var distance: Double = 0.0
    private val paint = Paint()

    init {
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        paint.textSize = 30f
        paint.isAntiAlias = true
    }

    fun setDeviceInfo(deviceName: String, distance: Double) {
        this.deviceName = deviceName
        this.distance = distance
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw red circle
        canvas.drawCircle(width / 2f, height / 2f, 20f, paint)
        // Draw device name and distance above the circle
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("$deviceName\n${"%.2f".format(distance)}m", width / 2f, height / 2f - 30f, paint)
    }
}
