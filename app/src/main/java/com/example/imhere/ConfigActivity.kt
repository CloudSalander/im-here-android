package com.example.imhere

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ConfigActivity : AppCompatActivity() {

    private  var mode = 0
    private var colorIndex = 0
    private var speedIndex = 1

    private var textColorIndex = 0
    private var textSpeedIndex = 1
    private var textSize = 36f  // ← Cambiado: valor inicial más lógico
    private var scrollText = ""

    private val colors = listOf(
        Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
        Color.CYAN, Color.MAGENTA, Color.WHITE, Color.parseColor("#FFA500")
    )

    private val colorNames = listOf("Rojo", "Verde", "Azul", "Amarillo", "Cian", "Magenta", "Blanco", "Naranja")
    private val speeds = listOf("Lento (0.5 Hz)", "Medio (1 Hz)", "Rápido (2 Hz)", "Fijo")
    private val textSpeeds = listOf("Lento", "Medio", "Rápido")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        mode = intent.getIntExtra(MainActivity.EXTRA_MODE, MainActivity.MODE_BLINK)

        if (mode == MainActivity.MODE_BLINK) {
            setupBlinkConfig()
        } else {
            setupTextConfig()
        }

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            startExecution()
        }
    }

    private fun setupBlinkConfig() {
        findViewById<TextView>(R.id.tvConfigTitle).text = "Configurar Parpadeo"

        val spinnerColor = findViewById<Spinner>(R.id.spinnerColor)
        setupSpinner(spinnerColor, colorNames) { colorIndex = it }

        val spinnerSpeed = findViewById<Spinner>(R.id.spinnerSpeed)
        setupSpinner(spinnerSpeed, speeds) { speedIndex = it }

        // Ocultar controles de texto
        findViewById<View>(R.id.layoutTextConfig).visibility = View.GONE
        // Mostrar controles de parpadeo
        findViewById<View>(R.id.layoutBlinkConfig).visibility = View.VISIBLE
    }

    private fun setupTextConfig() {
        findViewById<TextView>(R.id.tvConfigTitle).text = "Configurar Texto Scroll"

        // Configurar spinner de color del texto
        val spinnerTextColor = findViewById<Spinner>(R.id.spinnerTextColor)
        setupSpinner(spinnerTextColor, colorNames) { textColorIndex = it }

        // Configurar spinner de velocidad del texto
        val spinnerTextSpeed = findViewById<Spinner>(R.id.spinnerTextSpeed)
        setupSpinner(spinnerTextSpeed, textSpeeds) { textSpeedIndex = it }

        // Configurar SeekBar de tamaño
        val seekBarTextSize = findViewById<SeekBar>(R.id.seekBarTextSize)
        val tvTextSizeValue = findViewById<TextView>(R.id.tvTextSizeValue)
        seekBarTextSize.progress = 36  // 24 + 12 = 36 sp
        tvTextSizeValue.text = "48 sp"
        seekBarTextSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textSize = (progress + 12).toFloat()
                tvTextSizeValue.text = "${textSize.toInt()} sp"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Configurar EditText del texto
        val etScrollText = findViewById<EditText>(R.id.etScrollText)
        etScrollText.setText("¡Estoy aquí!")  // Texto por defecto
        scrollText = etScrollText.text.toString()

        etScrollText.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                scrollText = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Ocultar controles de parpadeo y mostrar los de texto
        findViewById<View>(R.id.layoutBlinkConfig).visibility = View.GONE
        findViewById<View>(R.id.layoutTextConfig).visibility = View.VISIBLE
    }

    private fun setupSpinner(spinner: Spinner, items: List<String>, onItemSelected: (Int) -> Unit) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onItemSelected(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun startExecution() {
        val intent = Intent(this, ExecutionActivity::class.java)

        if (mode == MainActivity.MODE_BLINK) {
            intent.putExtra(ExecutionActivity.EXTRA_MODE, MainActivity.MODE_BLINK)
            intent.putExtra(ExecutionActivity.EXTRA_BLINK_COLOR, colors[colorIndex])
            intent.putExtra(ExecutionActivity.EXTRA_BLINK_SPEED, speedIndex)
        } else {
            intent.putExtra(ExecutionActivity.EXTRA_MODE, MainActivity.MODE_SCROLL_TEXT)
            intent.putExtra(ExecutionActivity.EXTRA_TEXT_COLOR, colors[textColorIndex])
            intent.putExtra(ExecutionActivity.EXTRA_TEXT_SPEED, textSpeedIndex)
            intent.putExtra(ExecutionActivity.EXTRA_TEXT_SIZE, textSize)
            intent.putExtra(ExecutionActivity.EXTRA_SCROLL_TEXT, scrollText)
        }

        startActivity(intent)
    }
}