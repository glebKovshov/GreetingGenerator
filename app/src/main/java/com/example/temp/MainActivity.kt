package com.example.temp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.UUID
import android.content.Intent
import com.example.temp.HistoryActivity

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnHistory = findViewById<Button>(R.id.btnHistory)
        btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // Инициализируем БД
        db = AppDatabase.getInstance(applicationContext)

        // Получаем (или создаём) идентификатор пользователя
        userId = getOrCreateUserId()

        // Находим элементы по id
        val etName = findViewById<EditText>(R.id.etName)
        val etGender = findViewById<EditText>(R.id.etGender)
        val etRelation = findViewById<EditText>(R.id.etRelation)
        val etOccasion = findViewById<EditText>(R.id.etOccasion)
        val etTone = findViewById<EditText>(R.id.etTone)
        val etAge = findViewById<EditText>(R.id.etAge)

        val btnGenerate = findViewById<Button>(R.id.btnGenerate)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        // Обработка нажатия кнопки
        btnGenerate.setOnClickListener {
            val name = etName.text.toString().trim()
            val gender = etGender.text.toString().trim()
            val relation = etRelation.text.toString().trim()
            val occasion = etOccasion.text.toString().trim()
            val tone = etTone.text.toString().trim()
            val ageText = etAge.text.toString().trim()

            val age = ageText.toIntOrNull()

            if (name.isEmpty() || occasion.isEmpty()) {
                Toast.makeText(this, "Введите хотя бы имя и повод", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val greeting = generateGreeting(
                name = name,
                genderRaw = gender,
                relationRaw = relation,
                occasionRaw = occasion,
                toneRaw = tone,
                age = age
            )

            tvResult.text = greeting

            // Сохраняем поздравление в БД
            saveGreetingToDb(
                name = name,
                gender = gender,
                relation = relation,
                occasion = occasion,
                tone = tone,
                age = age,
                greetingText = greeting
            )
        }
    }

    /**
     * Создаёт или возвращает уже существующий userId.
     * Хранится в SharedPreferences и живёт всё время жизни приложения.
     */
    private fun getOrCreateUserId(): String {
        //Log.d("DB_TEST", "Saving greeting for userId=$userId")
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val existing = prefs.getString("user_id", null)
        if (existing != null) {
            return existing
        }

        val newId = UUID.randomUUID().toString()
        prefs.edit().putString("user_id", newId).apply()
        return newId
    }

    /**
     * Асинхронно сохраняем поздравление в локальную БД.
     */
    private fun saveGreetingToDb(
        name: String,
        gender: String?,
        relation: String?,
        occasion: String,
        tone: String?,
        age: Int?,
        greetingText: String
    ) {
        // Корутинка привязана к жизненному циклу Activity
        lifecycleScope.launch {
            val entity = GreetingEntity(
                userId = userId,
                name = name,
                gender = gender?.ifBlank { null },
                relation = relation?.ifBlank { null },
                occasion = occasion,
                tone = tone?.ifBlank { null },
                age = age,
                greetingText = greetingText,
                createdAt = System.currentTimeMillis()
            )
            db.greetingDao().insert(entity)
        }
    }

    /**
     * Основная функция генерации поздравления.
     */
    private fun generateGreeting(
        name: String,
        genderRaw: String,
        relationRaw: String,
        occasionRaw: String,
        toneRaw: String,
        age: Int?
    ): String {

        val gender = genderRaw.lowercase()
        val relation = relationRaw.lowercase()
        val occasion = occasionRaw.lowercase()
        val tone = toneRaw.lowercase()

        val appeal = when {
            relation.contains("мама") || relation.contains("мать") ->
                "Дорогая мама $name"

            relation.contains("пап") ->
                "Дорогой папа $name"

            relation.contains("друг") && (gender.startsWith("m") || gender.startsWith("м")) ->
                "Дорогой друг $name"

            relation.contains("друг") || relation.contains("подруг") ||
                    relation.contains("подруга") ->
                "Дорогая подруга $name"

            relation.contains("коллег") || relation.contains("начальник") ->
                "Уважаемый(ая) $name"

            else ->
                "Дорогой(ая) $name"
        }

        val occasionPhrase = when {
            occasion.contains("день рождения") || occasion.contains("др") ->
                "Поздравляю тебя с днём рождения! "

            occasion.contains("новый год") || occasion.contains("новым годом") ->
                "С наступающим Новым годом! "

            occasion.contains("8 марта") ->
                "С 8 Марта! "

            occasion.contains("23 февраля") ->
                "С 23 Февраля! "

            else ->
                "Поздравляю тебя с этим замечательным событием! "
        }

        val agePart = if (age != null &&
            (occasion.contains("день рождения") || occasion.contains("др"))
        ) {
            "Тебе уже $age — впереди ещё больше возможностей, ярких событий и интересных открытий. "
        } else {
            ""
        }

        val body = when {
            tone.contains("официаль") -> {
                "Желаю крепкого здоровья, стабильности, профессиональных успехов " +
                        "и реализации всех намеченных целей. Пусть каждый новый день приносит " +
                        "новые достижения и уверенность в завтрашнем дне. "
            }

            tone.contains("дружес") -> {
                "Желаю тебе побольше радости, верных людей рядом, крутых впечатлений " +
                        "и чтобы всё задуманное обязательно сбывалось. Пусть каждый день будет " +
                        "наполнен улыбками, приятными сюрпризами и хорошим настроением. "
            }

            tone.contains("юмор") || tone.contains("шут") -> {
                "Пусть в жизни будет как можно больше поводов для смеха и как можно меньше " +
                        "поводов вставать по будильнику. Желаю, чтобы денег хватало не только " +
                        "на еду и интернет, но ещё и на твои самые безумные идеи. И пусть все " +
                        "проблемы будут не сложнее выбора, что посмотреть вечером. "
            }

            else -> {
                "Желаю тебе счастья, здоровья, вдохновения, верных друзей рядом " +
                        "и больших успехов во всех делах. Пусть тебя окружают только " +
                        "добрые люди и самые приятные события. "
            }
        }

        val ending = when {
            relation.contains("коллег") && tone.contains("официаль") ->
                "С уважением и наилучшими пожеланиями."

            relation.contains("друг") || relation.contains("подруг") ->
                "Обнимаю и ещё раз поздравляю!"

            else ->
                "С самыми тёплыми пожеланиями."
        }

        return buildString {
            append(appeal)
            append("\n\n")
            append(occasionPhrase)
            append(agePart)
            append(body)
            append("\n")
            append(ending)
        }
    }
}
