package com.example.gametebakhewan

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ScoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        val tvScore = findViewById<TextView>(R.id.tvFinalScore)
        val btnHome = findViewById<Button>(R.id.btnHome)
        val btnRanking = findViewById<Button>(R.id.btnRanking)

        val score = intent.getIntExtra("score", 0)
        tvScore.text = "Skor Kamu: $score"

        saveScore(score)

        btnHome.setOnClickListener {
            startActivity(Intent(this, LevelActivity::class.java)) // langsung ke LevelActivity
            finish()
        }

        btnRanking.setOnClickListener {
            startActivity(Intent(this, RankingActivity::class.java))
        }
    }

    private fun saveScore(score: Int) {
        val prefs = getSharedPreferences("RANKING", MODE_PRIVATE)
        val editor = prefs.edit()

        val scores = prefs.getStringSet("scores", mutableSetOf())!!.toMutableSet()
        scores.add(score.toString())

        editor.putStringSet("scores", scores)
        editor.apply()
    }
}
