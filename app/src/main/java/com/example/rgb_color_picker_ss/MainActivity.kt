package com.example.rgb_color_picker_ss

// Импорт необходимых классов Android
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

    // Элементы интерфейса
    private lateinit var colorPreview: View  // View для отображения выбранного цвета
    private lateinit var tvColorValue: TextView  // TextView для отображения RGB значений

    // Текущие значения цветовых компонентов (по умолчанию белый цвет)
    private var currentRed = 255
    private var currentGreen = 255
    private var currentBlue = 255

    // Константы для сохранения состояния
    companion object {
        private const val KEY_RED = "saved_red"
        private const val KEY_GREEN = "saved_green"
        private const val KEY_BLUE = "saved_blue"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Установка макета

        // Восстановление сохраненных значений при повороте экрана
        if (savedInstanceState != null) {
            currentRed = savedInstanceState.getInt(KEY_RED, 255)
            currentGreen = savedInstanceState.getInt(KEY_GREEN, 255)
            currentBlue = savedInstanceState.getInt(KEY_BLUE, 255)
        }

        // Инициализация элементов интерфейса
        colorPreview = findViewById(R.id.colorPreview)
        tvColorValue = findViewById(R.id.tvColorValue)

        // Установка начального цвета
        updateMainColorPreview()

        // Обработчик нажатия кнопки
        findViewById<Button>(R.id.btnOpenDialog).setOnClickListener {
            showColorPickerDialog()  // Открытие диалога выбора цвета
        }
    }

    // Сохранение текущих значений при изменении конфигурации
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_RED, currentRed)
        outState.putInt(KEY_GREEN, currentGreen)
        outState.putInt(KEY_BLUE, currentBlue)
    }

    // Обновление основного предпросмотра цвета
    private fun updateMainColorPreview() {
        val color = Color.rgb(currentRed, currentGreen, currentBlue)
        colorPreview.setBackgroundColor(color)
        tvColorValue.text = "RGB: $currentRed, $currentGreen, $currentBlue"
    }

    // Показ диалогового окна выбора цвета
    private fun showColorPickerDialog() {
        // Создание кастомного вида для диалога
        val dialogView = layoutInflater.inflate(R.layout.dialog_color_picker, null)
        val dialogColorPreview = dialogView.findViewById<View>(R.id.dialogColorPreview)

        // Получение элементов управления из диалога
        val etRed = dialogView.findViewById<EditText>(R.id.etRed)
        val etGreen = dialogView.findViewById<EditText>(R.id.etGreen)
        val etBlue = dialogView.findViewById<EditText>(R.id.etBlue)

        val sbRed = dialogView.findViewById<SeekBar>(R.id.sbRed)
        val sbGreen = dialogView.findViewById<SeekBar>(R.id.sbGreen)
        val sbBlue = dialogView.findViewById<SeekBar>(R.id.sbBlue)

        // Установка текущих значений
        var red = currentRed
        var green = currentGreen
        var blue = currentBlue

        // Инициализация полей и ползунков текущими значениями
        etRed.setText(red.toString())
        etGreen.setText(green.toString())
        etBlue.setText(blue.toString())
        sbRed.progress = red
        sbGreen.progress = green
        sbBlue.progress = blue

        // Функция обновления предпросмотра в диалоге
        fun updateDialogPreview() {
            val color = Color.rgb(red, green, blue)
            dialogColorPreview.setBackgroundColor(color)
        }

        updateDialogPreview()  // Первоначальное обновление предпросмотра

        // Слушатель изменений для ползунков
        val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {// Это интерфейс, содержащий три абстрактных метода, которые должны быть реализованы
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
                updateDialogPreview()  // Обновление при изменении ползунка
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        }

        // Установка слушателей для всех ползунков
        sbRed.setOnSeekBarChangeListener(seekBarChangeListener)
        sbGreen.setOnSeekBarChangeListener(seekBarChangeListener)
        sbBlue.setOnSeekBarChangeListener(seekBarChangeListener)

        // Функции для настройки слушателей текстовых полей
        // Функция для красного цвета
        fun setupRedListener() {
            etRed.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {

                        val value = etRed.text.toString().toInt().coerceIn(0, 255)
                        red = value
                        sbRed.progress = value
                        updateDialogPreview()

                }
            }
        }

        // Функция для зеленого цвета
         fun setupGreenListener() {
            etGreen.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {

                        val value = etGreen.text.toString().toInt().coerceIn(0, 255)
                        green = value
                        sbGreen.progress = value
                        updateDialogPreview()

                }
            }
        }

        // Функция для синего цвета
         fun setupBlueListener() {
            etBlue.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {

                        val value = etBlue.text.toString().toInt().coerceIn(0, 255)
                        blue = value
                        sbBlue.progress = value
                        updateDialogPreview()

                }
            }
        }

        setupRedListener()
        setupGreenListener()
        setupBlueListener()

        // Создание и отображение диалога
        AlertDialog.Builder(this)
            .setTitle("Выберите цвет")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                // Сохранение выбранных значений
                currentRed = red
                currentGreen = green
                currentBlue = blue
                updateMainColorPreview()  // Обновление основного экрана
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}