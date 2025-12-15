package com.example.temp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import android.content.ClipboardManager
import android.content.ClipData
import android.content.Context

class GreetingDetailsActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var currentItem: GreetingEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_greeting_details)

        db = AppDatabase.getInstance(applicationContext)

        val greetingId = intent.getLongExtra("greeting_id", -1)

        val tvTitle = findViewById<TextView>(R.id.tvFullTitle)
        val tvDate = findViewById<TextView>(R.id.tvFullDate)
        val tvInfo = findViewById<TextView>(R.id.tvFullInfo)
        val tvText = findViewById<TextView>(R.id.tvFullText)

        val btnCopy = findViewById<Button>(R.id.btnCopy)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        if (greetingId == -1L) {
            tvTitle.text = "Ошибка: запись не найдена"
            return
        }

        lifecycleScope.launch {
            val userId = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("user_id", "") ?: ""

            val list = db.greetingDao().getGreetingsForUser(userId)
            val item = list.find { it.id == greetingId }

            if (item == null) {
                tvTitle.text = "Ошибка: поздравление не найдено"
                return@launch
            }

            currentItem = item

            tvTitle.text = "${item.name} — ${item.occasion}"

            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            tvDate.text = sdf.format(item.createdAt)

            tvInfo.text = """
Имя: ${item.name}
Пол: ${item.gender ?: "не указано"}
Отношение: ${item.relation ?: "не указано"}
Повод: ${item.occasion}
Тон: ${item.tone ?: "не указан"}
Возраст: ${item.age ?: "не указан"}
""".trimIndent()

            tvText.text = item.greetingText
        }

        // ========= КОПИРОВАНИЕ =========
        btnCopy.setOnClickListener {
            currentItem?.let {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("greeting", it.greetingText)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Скопировано в буфер", Toast.LENGTH_SHORT).show()
            }
        }

        // ========= УДАЛЕНИЕ =========
        btnDelete.setOnClickListener {
            currentItem?.let { entity ->
                lifecycleScope.launch {
                    db.greetingDao().deleteById(entity.id)
                    Toast.makeText(this@GreetingDetailsActivity, "Удалено", Toast.LENGTH_SHORT).show()
                    finish() // закрываем экран
                }
            }
        }
    }
}
