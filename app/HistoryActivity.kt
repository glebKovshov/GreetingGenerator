package com.example.temp
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.temp.AppDatabase
import com.example.temp.R
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentActivity.setContentView(R.layout.activity_history)

        db = AppDatabase.Companion.getInstance(applicationContext)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        userId = prefs.getString("user_id", "") ?: ""

        val recycler = Activity.findViewById<RecyclerView>(R.id.historyRecycler)
        recycler.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val list = db.greetingDao().getGreetingsForUser(userId)
            recycler.adapter = GreetingAdapter(list)
        }
    }
}
