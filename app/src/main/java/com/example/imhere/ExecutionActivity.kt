package com.example.imhere  // ← Cambiado

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ExecutionActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MODE = "extra_mode"
        const val EXTRA_BLINK_COLOR = "extra_blink_color"
        const val EXTRA_BLINK_SPEED = "extra_blink_speed"
        const val EXTRA_TEXT_COLOR = "extra_text_color"
        const val EXTRA_TEXT_SPEED = "extra_text_speed"
        const val EXTRA_TEXT_SIZE = "extra_text_size"
        const val EXTRA_SCROLL_TEXT = "extra_scroll_text"
    }

    private lateinit var container: FrameLayout
    private var isRunning = true
    private val handler = Handler(Looper.getMainLooper())
    private var blinkRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_execution)

        container = findViewById(R.id.container)

        val mode = intent.getIntExtra(EXTRA_MODE, MainActivity.MODE_BLINK)

        if (mode == MainActivity.MODE_BLINK) {
            startBlinkMode()
        } else {
            startScrollTextMode()
        }

        findViewById<Button>(R.id.btnStop).setOnClickListener {
            finish()
        }
    }

    private fun startBlinkMode() {
        val color = intent.getIntExtra(EXTRA_BLINK_COLOR, Color.RED)
        val speedIndex = intent.getIntExtra(EXTRA_BLINK_SPEED, 1)

        val blinkDelay = when (speedIndex) {
            0 -> 1000L
            1 -> 500L
            2 -> 250L
            else -> 0L
        }

        container.setBackgroundColor(color)

        if (blinkDelay > 0) {
            var isColorVisible = true
            blinkRunnable = object : Runnable {
                override fun run() {
                    if (!isRunning) return
                    if (isColorVisible) {
                        container.setBackgroundColor(Color.BLACK)
                    } else {
                        container.setBackgroundColor(color)
                    }
                    isColorVisible = !isColorVisible
                    handler.postDelayed(this, blinkDelay)
                }
            }
            handler.post(blinkRunnable!!)
        }
    }

    private fun startScrollTextMode() {
        val textColor = intent.getIntExtra(EXTRA_TEXT_COLOR, Color.WHITE)
        val speedIndex = intent.getIntExtra(EXTRA_TEXT_SPEED, 1)
        var textSize = intent.getFloatExtra(EXTRA_TEXT_SIZE, 24f)
        val scrollText = intent.getStringExtra(EXTRA_SCROLL_TEXT) ?: "¡Estoy aquí!"

        val speedPxPerSecond = when (speedIndex) {
            0 -> 100f
            1 -> 500f
            else -> 1000f
        }

        val textView = TextView(this).apply {
            text = scrollText
            setTextColor(textColor)
            this.textSize = textSize
            setBackgroundColor(Color.BLACK)
            setPadding(32, 32, 32, 32)
        }

        container.addView(textView, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_VERTICAL
        })

        textView.post {
            val screenWidth = container.width
            val textWidth = textView.width

            // En lugar de if (textWidth <= screenWidth)
// Siempre hacer scroll (pero mueves el texto desde el borde derecho)
            val animator = ValueAnimator.ofFloat(screenWidth.toFloat(), -textWidth.toFloat())
            animator.duration = ((textWidth + screenWidth) / speedPxPerSecond * 1000).toLong()
            animator.interpolator = LinearInterpolator()
            animator.repeatCount = ValueAnimator.INFINITE
            animator.addUpdateListener { animation ->
                textView.translationX = animation.animatedValue as Float
            }
            animator.start()
        }

        container.setBackgroundColor(Color.BLACK)
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        blinkRunnable?.let { handler.removeCallbacks(it) }
    }
}