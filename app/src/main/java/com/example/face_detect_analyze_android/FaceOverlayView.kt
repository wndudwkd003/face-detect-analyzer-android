package com.example.face_detect_analyze_android

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import org.opencv.core.Rect

class FaceOverlayView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private var detectedFaces: Array<Rect>? = null
    private var cWidth: Float = 0f
    private var cHeight: Float = 0f

    private val paint: Paint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    fun updateFaces(detectedFaces: Array<Rect>, cWidth: Float, cHeight: Float) {
        this.detectedFaces = detectedFaces
        this.cWidth = cWidth
        this.cHeight = cHeight
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        detectedFaces?.forEach { faceRect ->
            val scaleX: Float = width.toFloat() / cWidth
            val scaleY: Float = height.toFloat() / cHeight

            // 좌우 반전 적용
            val left = (width - (faceRect.x + faceRect.width) * scaleX)
            val right = (width - faceRect.x * scaleX)

            canvas.drawRect(left, faceRect.y * scaleY, right, (faceRect.y + faceRect.height) * scaleY, paint)
        }
    }

}
