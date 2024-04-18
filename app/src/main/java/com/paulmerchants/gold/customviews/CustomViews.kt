package com.paulmerchants.gold.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.DisplayMetrics
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.paulmerchants.gold.R


class CustomViews(context: Context, val activity: LinearLayout) : View(context) {


    private var mSweepAngle: Float = 0f
    private var mStartAngle: Float = 0f
    private var outerPaint = Paint()
    private var mPaint = Paint()
    private val displayMetrics = DisplayMetrics()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Set the paint color and style
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f

        //get the dimension of the screen
//        activity.windowManager.defaultDisplay.getMetrics(displayMetrics);
        val screenWidth: Int = activity.width
        val screenHeight: Int = activity.height


        val centerX = screenWidth / 2
        val centerY = screenHeight / 2
        val ovalSize = Math.min(screenWidth, screenHeight) / 2

        // Set the arc properties
//        val rectF = RectF(50f, 50f, 250f, 250f) // Left, top, right, bottom
        val rectF = RectF(
            (centerX - ovalSize).toFloat(),
            (centerY - ovalSize).toFloat() + 10f,
            (centerX + ovalSize).toFloat(),
            (centerY + ovalSize).toFloat()
        )

        val startAngle = -180f // In degrees

        val sweepAngle = 90f // In degrees

        val useCenter = false // Whether to include the center of the oval

        // Draw the arc
        canvas?.drawArc(rectF, startAngle, sweepAngle, useCenter, paint)

        // Set the foreground color
        paint.color = ContextCompat.getColor(context, R.color.yellow_main)
        paint.strokeWidth = 20f
        canvas?.drawArc(rectF, startAngle, sweepAngle, false, paint)
    }


}