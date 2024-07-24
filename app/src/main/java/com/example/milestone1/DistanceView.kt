package com.example.milestone1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DistanceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var distance: Float = 0f
    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    fun setDistance(distance: Float) {
        this.distance = distance
        invalidate() // Request to redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = distance * 100 // scale distance for visualization purposes
        canvas.drawCircle(width / 2f, height / 2f, radius, paint)
    }
}
