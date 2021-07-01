package ru.netology.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import ru.netology.R
import ru.netology.util.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

@SuppressLint("NewApi")
class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var radius = 0F
    private var center = PointF(0F, 0F)
    private var circle = RectF(0F, 0F, 0F, 0F)

    private var lineWith = AndroidUtils.dp(context, 5F).toFloat()
    private var fontSize = AndroidUtils.dp(context, 40F).toFloat()
    private var colors = emptyList<Int>()
    private var fillingType = emptyList<Int>()

    private var progress = 0F
    private var valueAnimator: ValueAnimator? = null

    init {
        context.withStyledAttributes(attrs, R.styleable.StatsView) {
            lineWith = getDimension(R.styleable.StatsView_lineWidth, lineWith)
            fontSize = getDimension(R.styleable.StatsView_fontSize, fontSize)
            colors = listOf(
                getColor(
                    R.styleable.StatsView_color1,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color2,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color3,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color4,
                    randomColor()
                )
            )
            fillingType = listOf(
                getInteger(R.styleable.StatsView_fillingType, 0),
                getInteger(R.styleable.StatsView_fillingType, 1)
            )
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWith
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWith
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = fontSize
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            update()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWith / 2
        center = PointF(w / 2F, h / 2F)
        circle = RectF(
            center.x - radius, center.y - radius,
            center.x + radius, center.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) return

        var startFrom = -90F
        val maxAngle = 360 * progress + startFrom

        emptyPaint.color = resources.getColor(R.color.grey, resources.newTheme())
        canvas.drawCircle(center.x, center.y, radius, emptyPaint)

        canvas.drawText(
            "%.2f%%".format(data.sum() * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )

        for ((index, datum) in data.withIndex()) {
            var angle = 360F * datum
            val newAngle = min(angle, maxAngle - startFrom)
            if (startFrom > maxAngle) return

            paint.color = colors.getOrNull(index) ?: randomColor()
            setFillingType(fillingType[0], canvas, startFrom, angle, newAngle)
            paint.color = colors[0]
            canvas.drawArc(circle, -90F, 1F, false, paint)

            startFrom += angle
        }
    }

    private fun setFillingType(
        fillingType: Int,
        canvas: Canvas,
        startFrom: Float,
        angle: Float,
        newAngle: Float
    ) {
        when (fillingType) {
            0 -> canvas.drawArc(
                circle, startFrom, angle * progress, false, paint
            )
            1 -> canvas.drawArc(
                circle, startFrom, newAngle, false, paint
            )
        }
        invalidate()
        requestLayout()
    }

    private fun update() {
        valueAnimator?.let {
            it.removeAllListeners()
            it.cancel()
        }
        progress = 0F

        valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()
            }
            duration = 5000
            interpolator = LinearInterpolator()
        }.also {
            it.start()
        }
    }
}

private fun randomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())