package com.example.temp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var userId: String
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        db = AppDatabase.getInstance(applicationContext)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        userId = prefs.getString("user_id", "") ?: ""

        recycler = findViewById(R.id.historyRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()

        // Каждый раз при возвращении обновляем список
        lifecycleScope.launch {
            val list = db.greetingDao().getGreetingsForUser(userId)

            recycler.adapter = GreetingAdapter(list) { greeting ->
                val intent = Intent(this@HistoryActivity, GreetingDetailsActivity::class.java)
                intent.putExtra("greeting_id", greeting.id)
                startActivity(intent)
            }
        }
    }
}
