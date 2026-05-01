package com.example.imhere  // ← IMPORTANTE: cambió el package

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MODE = "extra_mode"
        const val MODE_BLINK = 1
        const val MODE_SCROLL_TEXT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnBlinkMode).setOnClickListener {
            startConfigActivity(MODE_BLINK)
        }

        findViewById<Button>(R.id.btnTextMode).setOnClickListener {
            startConfigActivity(MODE_SCROLL_TEXT)
        }
    }

    private fun startConfigActivity(mode: Int) {
        val intent = Intent(this, ConfigActivity::class.java)
        intent.putExtra(EXTRA_MODE, mode)
        startActivity(intent)
    }
}