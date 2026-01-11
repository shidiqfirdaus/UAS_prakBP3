package com.example.gametebakhewan

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RankingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        val tvRanking = findViewById<TextView>(R.id.tvRanking)
        val btnBack = findViewById<Button>(R.id.btnBack)

        val prefs = getSharedPreferences("RANKING", MODE_PRIVATE)
        val scores = prefs.getStringSet("scores", emptySet())!!
            .mapNotNull { it.toIntOrNull() }
            .sortedDescending()
            .take(5)

        tvRanking.text = if (scores.isNotEmpty()) {
            scores.mapIndexed { i, s -> "${i+1}. Skor: $s" }
                .joinToString("\n")
        } else {
            "Belum ada skor 😢"
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}
