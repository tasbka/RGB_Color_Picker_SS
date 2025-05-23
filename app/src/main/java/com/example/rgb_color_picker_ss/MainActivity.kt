package com.example.rgb_color_picker_ss

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.widget.EditText
import android.widget.SeekBar

class MainActivity : AppCompatActivity() {

    private lateinit var colorPreview: View
    private lateinit var tvColorValue: TextView

    // Переменные для хранения текущих значений RGB
    private var currentRed = 255
    private var currentGreen = 255
    private var currentBlue = 255

    // Ключи для сохранения в Bundle
    companion object {
        private const val KEY_RED = "saved_red"
        private const val KEY_GREEN = "saved_green"
        private const val KEY_BLUE = "saved_blue"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Восстановление сохраненных значений (если есть)
        if (savedInstanceState != null) {
            currentRed = savedInstanceState.getInt(KEY_RED, 255)
            currentGreen = savedInstanceState.getInt(KEY_GREEN, 255)
            currentBlue = savedInstanceState.getInt(KEY_BLUE, 255)
        }

        colorPreview = findViewById(R.id.colorPreview)
        tvColorValue = findViewById(R.id.tvColorValue)

        // Установка начального цвета
        updateMainColorPreview()

        findViewById<Button>(R.id.btnOpenDialog).setOnClickListener {
            showColorPickerDialog()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохраняем текущие значения при повороте экрана
        outState.putInt(KEY_RED, currentRed)
        outState.putInt(KEY_GREEN, currentGreen)
        outState.putInt(KEY_BLUE, currentBlue)
    }

    private fun updateMainColorPreview() {
        val color = Color.rgb(currentRed, currentGreen, currentBlue)
        colorPreview.setBackgroundColor(color)
        tvColorValue.text = "RGB: $currentRed, $currentGreen, $currentBlue"
    }

    private fun showColorPickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_color_picker, null)
        val dialogColorPreview = dialogView.findViewById<View>(R.id.dialogColorPreview)

        val etRed = dialogView.findViewById<EditText>(R.id.etRed)
        val etGreen = dialogView.findViewById<EditText>(R.id.etGreen)
        val etBlue = dialogView.findViewById<EditText>(R.id.etBlue)

        val sbRed = dialogView.findViewById<SeekBar>(R.id.sbRed)
        val sbGreen = dialogView.findViewById<SeekBar>(R.id.sbGreen)
        val sbBlue = dialogView.findViewById<SeekBar>(R.id.sbBlue)

        // Установка сохраненных значений
        var red = currentRed
        var green = currentGreen
        var blue = currentBlue

        // Инициализация полей и ползунков сохраненными значениями
        etRed.setText(red.toString())
        etGreen.setText(green.toString())
        etBlue.setText(blue.toString())
        sbRed.progress = red
        sbGreen.progress = green
        sbBlue.progress = blue

        // Обновление предпросмотра в диалоге
        fun updateDialogPreview() {
            val color = Color.rgb(red, green, blue)
            dialogColorPreview.setBackgroundColor(color)
        }

        updateDialogPreview()

        val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                when (seekBar.id) {
                    R.id.sbRed -> {
                        red = progress
                        etRed.setText(progress.toString())
                    }
                    R.id.sbGreen -> {
                        green = progress
                        etGreen.setText(progress.toString())
                    }
                    R.id.sbBlue -> {
                        blue = progress
                        etBlue.setText(progress.toString())
                    }
                }
                updateDialogPreview()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        }

        sbRed.setOnSeekBarChangeListener(seekBarChangeListener)
        sbGreen.setOnSeekBarChangeListener(seekBarChangeListener)
        sbBlue.setOnSeekBarChangeListener(seekBarChangeListener)

        fun setupEditTextListener(editText: EditText, seekBar: SeekBar, update: (Int) -> Unit) {
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    try {
                        val value = editText.text.toString().toInt().coerceIn(0, 255)
                        update(value)
                        seekBar.progress = value
                        updateDialogPreview()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "Введите число от 0 до 255", Toast.LENGTH_SHORT).show()
                        // Восстановление предыдущего значения при ошибке
                        editText.setText(seekBar.progress.toString())
                    }
                }
            }
        }

        setupEditTextListener(etRed, sbRed) { red = it }
        setupEditTextListener(etGreen, sbGreen) { green = it }
        setupEditTextListener(etBlue, sbBlue) { blue = it }

        AlertDialog.Builder(this)
            .setTitle("Выберите цвет")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                // Сохраняем выбранные значения
                currentRed = red
                currentGreen = green
                currentBlue = blue

                // Обновляем основной предпросмотр
                updateMainColorPreview()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}