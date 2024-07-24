package com.example.milestone1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class DistancePlotView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var deviceData: Map<String, Double> = emptyMap()

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 40f
    }
    private val devicePaint = Paint().apply {
        color = Color.RED
    }
    private val originPaint = Paint().apply {
        color = Color.GREEN
    }
    private val linePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 2f
    }
    private val gridPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    fun setDeviceData(deviceData: Map<String, Double>) {
        this.deviceData = deviceData.mapValues { (_, distance) ->
            distance.coerceAtMost(50.0) // Limit maximum distance to 50 meters
        }
        invalidate() // Trigger a redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val originX = width / 2f
        val originY = height / 2f

        // Draw the grid
        drawGrid(canvas)

        // Draw the "You" label at the origin
        canvas.drawCircle(originX, originY, 20f, originPaint)
        canvas.drawText("You", originX - 20, originY - 30, textPaint)

        // Scale factor based on the maximum distance (50 meters)
        val maxDistance = 50f
        val scale = (height / 2f) / maxDistance

        // Draw each device based on its distance and a random angle for distribution
        deviceData.entries.forEachIndexed { index, (deviceName, distance) ->
            val angle = (index * (360.0 / deviceData.size)) * (Math.PI / 180)
            val positionX = originX + (distance.toFloat() * scale * cos(angle)).toFloat()
            val positionY = originY - (distance.toFloat() * scale * sin(angle)).toFloat()

            // Draw the device as a red circle
            canvas.drawCircle(positionX, positionY, 20f, devicePaint)

            // Draw the line from the origin to the device
            canvas.drawLine(originX, originY, positionX, positionY, linePaint)

            // Draw the distance text on the line
            val midX = (originX + positionX) / 2
            val midY = (originY + positionY) / 2
            canvas.drawText("${"%.2f".format(distance)}m", midX, midY - 10, textPaint)

            // Draw the device name near the device circle
            canvas.drawText(deviceName, positionX - 50, positionY - 30, textPaint)
        }
    }

    private fun drawGrid(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        val step = 100f // Distance between grid lines (e.g., every 100 pixels)

        for (x in 0..width.toInt() step step.toInt()) {
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height, gridPaint)
        }
        for (y in 0..height.toInt() step step.toInt()) {
            canvas.drawLine(0f, y.toFloat(), width, y.toFloat(), gridPaint)
        }
    }
}
