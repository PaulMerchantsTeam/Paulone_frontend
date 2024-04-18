package com.paulmerchants.gold.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.paulmerchants.gold.R
import java.util.*

class SemiCircleArcProgressBar : View {
    private var padding = 25
    private var progressPlaceHolderColor = 0
    private var progressBarColor = 0
    private var progressPlaceHolderWidth = 0
    private var progressBarWidth = 0
    private var percent = 0
    private var top = 0
    private var left = 0
    private var right = 0
    private var bottom = 0

    //Constructors
    constructor(context: Context?) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setAttrs(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        setAttrs(context, attrs)
    }

    override fun onDraw(canvas: Canvas) {
        padding =
            if (progressBarWidth > progressPlaceHolderWidth) progressBarWidth + 5 else progressPlaceHolderWidth + 5
        top = padding
        left = padding
        right = measuredWidth
        bottom = measuredHeight * 2
        val progressAmount = percent * 1.8.toFloat()
        canvas.drawArc(
            progressBarRectF,
            180f,
            180f,
            false,
            getPaint(progressPlaceHolderColor, progressPlaceHolderWidth)
        ) //arg2: For the starting point, the starting point is 0 degrees from the positive direction of the x coordinate system. How many angles are arg3 selected to rotate clockwise?
        canvas.drawArc(
            progressBarRectF,
            180f,
            progressAmount,
            false,
            getPaint(progressBarColor, progressBarWidth)
        ) //arg2: For the starting point, the starting point is 0 degrees from the positive direction of the x coordinate system. How many angles are arg3 selected to rotate clockwise?
    }

    //Private Methods
    private fun setAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.SemiCircleArcProgressBar, 0, 0)
        try {
            progressPlaceHolderColor = typedArray.getColor(
                R.styleable.SemiCircleArcProgressBar_progressPlaceHolderColor,
                Color.GRAY
            )
            progressBarColor = typedArray.getColor(
                R.styleable.SemiCircleArcProgressBar_progressBarColor,
                Color.WHITE
            )
            progressPlaceHolderWidth =
                typedArray.getInt(R.styleable.SemiCircleArcProgressBar_progressPlaceHolderWidth, 25)
            progressBarWidth =
                typedArray.getInt(R.styleable.SemiCircleArcProgressBar_progressBarWidth, 10)
            percent = typedArray.getInt(R.styleable.SemiCircleArcProgressBar_percent, 76)
        } finally {
            typedArray.recycle()
        }
    }

    private fun getPaint(color: Int, strokeWidth: Int): Paint {
        val paint = Paint()
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth.toFloat()
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        return paint
    }

    private val progressBarRectF: RectF
        get() = RectF(
            left.toFloat(),
            top.toFloat(),
            (right - padding).toFloat(),
            (bottom - padding * 2).toFloat()
        )

    //Setters
    fun setProgressPlaceHolderColor(color: Int) {
        progressPlaceHolderColor = color
        postInvalidate()
    }

    fun setProgressBarColor(color: Int) {
        progressBarColor = color
        postInvalidate()
    }

    fun setProgressPlaceHolderWidth(width: Int) {
        progressPlaceHolderWidth = width
        postInvalidate()
    }

    fun setProgressBarWidth(width: Int) {
        progressBarWidth = width
        postInvalidate()
    }

    fun setPercent(percent: Int) {
        this.percent = percent
        postInvalidate()
    }

    //Custom Setter
    fun setPercentWithAnimation(percent: Int) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            var step = 0
            override fun run() {
                if (step <= percent) setPercent(step++)
            }
        }, 0, 12)
    }
}